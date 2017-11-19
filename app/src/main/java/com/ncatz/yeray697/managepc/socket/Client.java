package com.ncatz.yeray697.managepc.socket;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client extends Thread {

    public static final int SEND_PASS = -10;
    public static final int INVALID_PASS = -9;
    public static final int VALID_PASS = -8;
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
    public static final int ORDER_VOLUME_UP = 11;
    public static final int ORDER_VOLUME_DOWN = 12;
    public static final int ORDER_TIMER = 13;

    public static final String KEYCODE_DEL = "-1";
    public static final String KEYCODE_SPACE = "-2";
    public static final String KEYCODE_ENTER = "-3";

    private String dstAddress;
    private int dstPort;
    private String password;
    private Socket socket;
    private DataOutputStream dOut;
    private DataInputStream dIn;
    private boolean passwordCheckedSuccessful;

    public Client() {
        dstAddress = "192.168.1.75";
        dstAddress = "192.168.1.135";
        dstAddress = "192.168.1.101";
        dstPort = 5312;
        password = "72370";
        passwordCheckedSuccessful = false;
    }

    public void sendMessage(final int option, final String additional) {
        new Thread() {
            @Override
            public void run() {
                if (passwordCheckedSuccessful) {
                    try {
                        String message = String.valueOf(option) + ";";
                        if (additional != null && additional.length() > 0)
                            message += additional + ";";
                        Log.d("asdf", message);
                        dOut.writeUTF(message);
                        dOut.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }

    @Override
    public void run() {
        try {
            socket = new Socket(dstAddress, dstPort);
            dOut = new DataOutputStream(socket.getOutputStream());
            dIn = new DataInputStream(socket.getInputStream());
            dOut.writeUTF(String.valueOf(SEND_PASS) + ";" + password+";");
            dOut.flush();
            byte[] line = new byte[1];
            dIn.read(line);
            String received = new String(line);
            if (received.equals("1")) {
                passwordCheckedSuccessful = true;
            } else {
                Log.d("asdf","error");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}