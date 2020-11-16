package com.xfl.mdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ADB {
    private final String adbPath;
    public ADB(String adbPath){
        this.adbPath = adbPath;
    }
    public String execute(String args) throws Exception {
        Process process = Runtime.getRuntime().exec(adbPath+" "+args);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        StringBuilder errorSb = new StringBuilder();
        String errorLine;
        while((errorLine = errorReader.readLine())!=null){
            errorSb.append(errorLine).append("\n");
        }
        if(!errorSb.isEmpty()){
            throw new Exception(errorSb.toString());
        }
        return sb.toString();
    }
}