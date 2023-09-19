package com.rij.amethyst_dev.Configuration;

public class URLS {
    private static String ServerIPPort = null;
    public static void setServerIPPort(String serverIPPort){
        ServerIPPort = serverIPPort;
    }


    public static String LibertyBansBan(){ return "http://" + ServerIPPort + "/libertybans/ban";}
    public static String LibertyBansUnBan(){ return "http://" + ServerIPPort + "/libertybans/unban";}

    public static String WhitelistAdd() { return "http://" + ServerIPPort + "/whitelist/add"; }
    public static String WhitelistRemove() { return  "http://" + ServerIPPort + "/whitelist/remove";}

    public static String Skin() { return "http://" + ServerIPPort + "/skin";}
    public static String isOnline() { return "http://" + ServerIPPort + "/isonline"; }

}
