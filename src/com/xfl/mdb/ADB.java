package com.xfl.mdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ADB {
    private String adbPath;
    public ADB(String adbPath){
        this.adbPath = adbPath;
    }
    public String execute(String args) throws IOException {
        System.out.println(args);
        Process process = Runtime.getRuntime().exec(adbPath+" "+args);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }
}