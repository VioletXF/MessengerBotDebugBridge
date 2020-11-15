package com.xfl.mdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
public class MDB{
    final ADB adb;
    public MDB(ADB adb){
        this.adb = adb;
    }
    private String convertBotName(String botName){
        return botName.replaceAll(" ","\\\\ ");
    }

    /**
     * @param botName Bot's name to compile
     * @return adb execute results
     * @throws IOException when IOException occurs while executing adb command
     */
    public String compile(String botName) throws IOException {
        String namearg = convertBotName(botName);

        return adb.execute("shell am broadcast -a com.xfl.msgbot.broadcast.compile -p com.xfl.msgbot --es name "+namearg);
    }

    /**
     * @param botName Bot's name to set power
     * @param power true to turn on, false to turn off
     * @return adb execute results
     * @throws IOException when IOException occurs while executing adb command
     */
    public String setBotPower(String botName, Boolean power) throws IOException{
        String namearg = convertBotName(botName);
        String powerString = power.toString();
        return adb.execute("shell am broadcast -a com.xfl.msgbot.broadcast.set_bot_power -p com.xfl.msgbot --es name "+namearg+" --ez power "+powerString);
    }

    /**
     * @param activation true to activate MessengerBot App, false to deactivate MessengerBot App
     * @return adb execute results
     * @throws IOException when IOException occurs while executing adb command
     */
    public String setActivation(Boolean activation) throws IOException {
        String activationString = activation.toString();
        return adb.execute("shell am broadcast -a com.xfl.msgbot.broadcast.set_activation -p com.xfl.msgbot --ez activation "+activationString);
    }


}

