package com.trafalgartmc.guinep.Utility;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.trafalgartmc.guinep.ChatActivity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import core.ChatObject;

/**
 * Created by rohan on 4/30/2017.


public class ChatConnectionHelper  extends AsyncTask<Void,ChatObject,String> {
    private final String username   = "10001";
    private final String agent      = "Agent-10001";
    private final String ipAddress  = "192.168.100.68";
    private final int port          = 2416;
    private Socket socket           = null;
    private ObjectInputStream in    = null;
    private ObjectOutputStream out  = null;
    private boolean isRunning       = false;

    public ChatConnectionHelper() {
        execute();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(500);
                        double total = Runtime.getRuntime().totalMemory();
                        double free = Runtime.getRuntime().freeMemory();
                        String str =
                                "Momeory Usage " + (total-free)/1000000 + " mb, " + " Total " + total/1000000 + " Free " + free/1000000
                                + "\nThread activeCount " + Thread.activeCount()
                                + "\nThread currentThread " + Thread.currentThread()
                                + "\nServer " + socket;

                        Log.d(Common.LOG_TAG, str);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ChatConnectionHelper.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }

    @Override
    protected String doInBackground(Void... params) {
        connect();

        sendMsg(new ChatObject(null, username, agent, ChatObject.NULL));
        sendMsg(new ChatObject(null, username, agent, ChatObject.LOGIN));
        displayHandle(new ChatObject("Client connected to server at " + socket.getInetAddress(),null,null, ChatObject.NOTIFY));
        try {
            isRunning = true;
            while(isClientRunning()){
                ChatObject msgInput = (ChatObject) in.readObject();
                publishProgress(msgInput);
            }
        } catch (IOException ex) {
            displayHandle(new ChatObject("The Server has gone away", null, null, ChatObject.NOTIFY));
            try {
                if(socket != null){socket.close();}
            } catch (IOException ex1) {
                Logger.getLogger(ChatConnectionHelper.class.getName()).log(Level.SEVERE, null, ex1);
            }
            disconnect();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ChatConnectionHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(ChatObject... msg) {
        super.onProgressUpdate(msg);
        displayHandle(msg[0]);
    }

    private void connect() {
        try {
            displayHandle(new ChatObject("Locating Server...", null, null, ChatObject.NOTIFY));
            socket = new Socket(ipAddress, port);
            socket.setTcpNoDelay(true);
            Log.e(Common.LOG_TAG,socket.toString());
            out = new ObjectOutputStream(socket.getOutputStream());
            in  = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(ChatConnectionHelper.class.getName()).log(Level.SEVERE, null, ex);
            displayHandle(new ChatObject("Server " + ipAddress + " not found " + ex.getMessage(), null, null, ChatObject.NOTIFY));
        }
    }

    public boolean isClientRunning() {
        return isRunning;
    }

    private synchronized void displayHandle(ChatObject msg) {
        String textMsg = String.format("[%s Says]:%s", msg.getSender(), ChatObject.CTRLF + ChatObject.CTRTB + msg.getMessage() + ChatObject.CTRLF);
        //ChatActivity.mMsgArea.append(textMsg);
    }


    public void disconnect() {
        try {
            sendMsg(new ChatObject(null, username, agent, ChatObject.LOGOUT));
            isRunning = false;
            if(socket != null){socket.close();}
            if(in  !=null ){in.close();}
            if(out !=null ){out.close();}
        } catch (IOException ex) {
            Logger.getLogger(ChatConnectionHelper.class.getName()).log(Level.SEVERE, null, ex.getMessage());
        }
    }



    public void sendMsg(String msg){
        ChatObject obj = new ChatObject(msg, username, agent, ChatObject.MESSAGE);
        sendMsg(obj);
        displayHandle(obj);
    }

    private void sendMsg(ChatObject msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(ChatConnectionHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}*/
