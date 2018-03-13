package com.trafalgartmc.guinep.Classes;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.trafalgartmc.guinep.Adapters.ChatAdapter;
import com.trafalgartmc.guinep.R;
import com.trafalgartmc.guinep.Utility.Common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import core.ChatObject;

public class NetWorkHelper {
    private static final String IP_ADDRESS = "192.81.219.62";
    private static final int PORT = 2416;
    public static final String AGENT_ID = "agent_id";
    public static String username;
    private static String fullName;
    public static String myId;
    public static String agentId;
    private static Socket socket = null;
    private static ObjectInputStream in = null;
    private static ObjectOutputStream out = null;
    private static boolean isRunning = false;
    private static ChatAdapter cAdapter;
    private static RecyclerView recyclerView;
    private static Context mContext;
    private static Activity mActivity;
    public static List<ChatObject> msgObj = new ArrayList<>();

    public NetWorkHelper(Activity act, ChatAdapter cAdtr, RecyclerView rView) {
        mActivity   = act;
        mContext    = mActivity.getBaseContext();
        fullName    = Common.getSession(mContext).getString(mContext.getString(R.string.SESSION_NAME),null);
        username    = fullName.split(" ")[0];
        myId        = String.valueOf(Common.getSession(mContext).getInt(mContext.getString(R.string.SESSION_ID),0));
        cAdapter    = cAdtr;
        recyclerView= rView;
    }

    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                connect();
            }
        }).start();
    }

    private void connect() {
        try {
            displayHandle(new ChatObject("Locating Server...", null, null, null, ChatObject.NOTIFY));
            connectToServer();
            new ClientThread();
        } catch (IOException ex) {
            Log.e(Common.LOG_TAG, ex.getMessage());
            displayHandle(new ChatObject("Server " + IP_ADDRESS + " not found " + ex.getMessage(), null, null, null, ChatObject.NOTIFY));
        }
    }

    private static void connectToServer() throws IOException {
        socket = new Socket(IP_ADDRESS, PORT);
        socket.setTcpNoDelay(true);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in  = new ObjectInputStream(socket.getInputStream());
    }

    private static void displayHandle(ChatObject msgObj) {
        if(msgObj==null) return;
        switch(msgObj.getMessageType()){
            case ChatObject.WHOISIN:
            break;
            case ChatObject.MESSAGE:
                printMessageToDisplay(msgObj);
            break;
            case ChatObject.LOGOUT:
            break;
        }
        Log.e(Common.LOG_TAG, msgObj.getMessage());
    }

    public static void printMessageToDisplay(ChatObject msg) {
        if(msg.getMessageType() == ChatObject.MESSAGE){
            msgObj.add(msg);
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(msgObj.size());
                }
            });
        }
    }

    public static void sendMsg(ChatObject msg) {
        try {
            if(!socket.isClosed()){
                out.writeObject(msg);
                out.flush();
            }
        } catch (IOException ex) {
            displayHandle(new ChatObject(ex.getMessage(), null, null, null, ChatObject.NOTIFY));
        }
    }

    public static void sendMsgToSever(final ChatObject msg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendMsg(msg);
                printMessageToDisplay(msg);
            }
        }).start();;
    }

    public static void disconnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    isRunning = false;
                    msgObj.clear();
                    if(socket != null){
                        sendMsg(new ChatObject(null, myId, agentId, null, ChatObject.LOGOUT));
                        socket.close();
                    }
                    if(in  !=null ){in.close();}
                    if(out !=null ){out.close();}
                } catch (IOException ex) {
                    displayHandle(new ChatObject(ex.getMessage(), null, null, null, ChatObject.NOTIFY));
                }
            }
        }).start();
    }

    public static boolean isClientRunning() {
        return isRunning;
    }

    private class ClientThread{
        public ClientThread() {
            /* Send client ID, Username and Full name as the first message to display on the welcome */
            sendMsg(new ChatObject(myId, username, fullName, null, ChatObject.NULL));
            //Pass your ID and your AgentId with Message Type LOGIN.
            sendMsg(new ChatObject(null, myId, agentId, null, ChatObject.LOGIN));
            displayHandle(new ChatObject("Client connected to server at " + socket.getInetAddress(),null,null, null, ChatObject.NOTIFY));
            try {
                isRunning = true;
                while(isClientRunning()){
                    ChatObject msgInput = (ChatObject) in.readObject();
                    displayHandle(msgInput);
                }
            } catch (IOException ex) {
                displayHandle(new ChatObject("The Server has gone away >> " + ex.getLocalizedMessage(), null, null,  null, ChatObject.NOTIFY));
            } catch (ClassNotFoundException ex) {
                displayHandle(new ChatObject(ex.getMessage(), null, null, null, ChatObject.NOTIFY));
            }finally {
                disconnect();
            }
        }
    }

    public static String getAgentList() {
        ChatObject msg = null;
        try {
            connectToServer();
            socket.setSoTimeout(10000);
            sendMsg(new ChatObject(myId, username, fullName, null, ChatObject.NULL));
            sendMsg(new ChatObject(null, myId, agentId, "GET_LIST", ChatObject.LOGIN));
            try {
                msg = (ChatObject) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException ex) {
            Log.e(Common.LOG_TAG, "Error get agent list >> " + ex.getMessage());
        }finally {
            NetWorkHelper.disconnect();
        }
        return msg != null ? msg.getMessage() : null;
    }
}
