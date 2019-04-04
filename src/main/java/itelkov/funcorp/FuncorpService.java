package itelkov.funcorp;

import itelkov.funcorp.exception.InputParamException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

@Component
public class FuncorpService {

    private final String[] envNames = {"FLIGHT_PROGRAM","EXCHANGE_URI","TELEMETRY_FREQ"};
    private String flightProgramPath;
    private String exchangeUri;
    private int telemetryFreq;

    @PostConstruct
    public void init() throws IOException {
        initInputParams();
        initProgram();
    }


    private void initInputParams(){
        Map<String, String> env = System.getenv();
        Arrays.asList(envNames).forEach(envName -> validateEnv(env, envName));

        flightProgramPath = env.get(envNames[0]);
        exchangeUri = env.get(envNames[1]);

        try {
            telemetryFreq = Integer.parseInt(env.get(envNames[2]));
        }
        catch (NumberFormatException e){
            throw new InputParamException("Variable " + envNames[2] + " isn't integer: " + env.get(envNames[2]));
        }
    }

    private void initProgram() throws IOException {
        FileInputStream in = new FileInputStream(flightProgramPath);
        try {
            java.nio.channels.FileLock lock = in.getChannel().lock();
            try {
                Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
        //...
            } finally {
                lock.release();
            }
        } finally {
            in.close();
        }
    }

    private void validateEnv(Map<String, String> env, String envName){
        if (!env.containsKey(envName) || env.get(envName).isEmpty()){
            throw new InputParamException("Variable " + envName + " isn't defined");
        }
    }
}
