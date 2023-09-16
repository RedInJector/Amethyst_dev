package com.rij.amethyst_dev.Configuration;

public class URLS {
    private static String ServerIPPort = "asd";
    public static void setServerIPPort(String serverIPPort){
        ServerIPPort = serverIPPort;
    }


    public static String LibertyBansBan = "http://" + ServerIPPort + "/libertybans/ban";
    public static String LibertyBansUnBan = "http://" + ServerIPPort + "/libertybans/unban";

    public static String WhitelistAdd = "http://" + ServerIPPort + "/whitelist/add";
    public static String WhitelistRemove = "http://" + ServerIPPort + "/whitelist/remove";

    public static String Skin = "http://" + ServerIPPort + "/skin";
    public static String isOnline = "http://" + ServerIPPort + "/isonline";

}
