package com.ncatz.yeray697.managepc.socket;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client extends Thread {

    public static final int ORDER_KEEP_ON = 0;
    public static final int ORDER_CLOSE = 1;
    public static final int ORDER_LEFT_CLICK_DOWN = 2;
    public static final int ORDER_LEFT_CLICK_UP = 3;
    public static final int ORDER_RIGHT_CLICK_DOWN = 4;
    public static final int ORDER_RIGHT_CLICK_UP = 5;
    public static final int ORDER_WHEEL_CLICK_DOWN = 6;
    public static final int ORDER_WHEEL_CLICK_UP = 7;
    public static final int ORDER_MOVE_WHEEL = 8;
    public static final int ORDER_MOUSE_MOVE = 9;
    public static final int ORDER_KEY_PRESSED = 10;
    public static final String KEYCODE_DEL = "-1";
    public static final String KEYCODE_SPACE = "-2";
    public static final String KEYCODE_ENTER = "-3";

    private String dstAddress;
    private int dstPort;
    private Socket socket;
    private DataOutputStream dOut;

    public Client() {
        dstAddress = "192.168.1.75";
        dstAddress = "192.168.1.135";
        dstPort = 5312;
    }

    public void sendMessage(final int option, final String additional) {
        new Thread() {
            @Override
            public void run() {
                try {
                    String message = String.valueOf(option) + ";";
                    if (additional != null && additional.length() > 0)
                        message += additional + ";";
                    Log.d("asdf",message);
                    dOut.writeUTF(message);
                    dOut.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    @Override
    public void run() {
        try {
            socket = new Socket(dstAddress, dstPort);
            dOut = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}