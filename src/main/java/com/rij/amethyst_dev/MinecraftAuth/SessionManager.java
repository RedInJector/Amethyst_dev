package com.rij.amethyst_dev.MinecraftAuth;

import com.rij.amethyst_dev.jsons.minecraftAuth.MinecraftSession;

import java.util.Calendar;
import java.util.HashMap;

public class SessionManager {
    private final HashMap<MinecraftSession, Long> sessions = new HashMap<>();
    private final HashMap<String, MinecraftSession> nameSession = new HashMap<>();
    private int maxSessionTime = 120;

    public SessionManager(){}
    public SessionManager(int maxSessionTime){
        this.maxSessionTime = maxSessionTime;
    }

    public void setMaxSessionTime(int maxSessionTime) {
        this.maxSessionTime = maxSessionTime;
    }

    public boolean isValid(String name, String ip){
        MinecraftSession session = new MinecraftSession(ip, name);

        if(!sessions.containsKey(session))
            return false;

        Long sessionTime = sessions.get(session);
        if(Calendar.getInstance().getTimeInMillis() > sessionTime)
            return false;

        return true;
    }
    public boolean isValid(MinecraftSession session){
        if(!sessions.containsKey(session))
            return false;

        Long sessionTime = sessions.get(session);
        if(Calendar.getInstance().getTimeInMillis() > sessionTime) {
            sessions.remove(session);
            return false;
        }

        return true;
    }

    public void saveSession(String name, String ip){
        MinecraftSession session = new MinecraftSession(ip, name);

        if(sessions.containsKey(session)){
            sessions.remove(session);
        }
        //int maxSessionTime = 125;
        int maxSessionTime = this.maxSessionTime;

        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, maxSessionTime);
        sessions.put(session, c.getTimeInMillis());
    }

    public void saveSession(MinecraftSession session){
        if(nameSession.containsKey(session.getName())) {
            sessions.remove(nameSession.get(session));
            nameSession.remove(session.getName());
        }

        if(sessions.containsKey(session)){
            sessions.remove(session);
        }
        //int maxSessionTime = 125;
        int maxSessionTime = this.maxSessionTime;

        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, maxSessionTime);
        sessions.put(session, c.getTimeInMillis());
        nameSession.put(session.getName(), session);
    }
    public void clearSessions(){
        sessions.clear();
    }

    public void removeSession(MinecraftSession session){

    }
}
