package com.explodingbacon.robot.main;

import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.bcnlib.networking.Communicator;
import com.explodingbacon.bcnlib.networking.KeyboardButton;
import com.explodingbacon.bcnlib.utils.Utils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server code (robot-side) for communication between a robot and a client.
 *
 * @author Ryan Shavell
 * @version 2016.9.10
 */
public class Server extends Communicator {

    public static int PORT = 5800;

    private ServerSocket server;
    private Socket client;

    public Server() {
        super();
        Utils.runInOwnThread(() -> {
            try {
                server = new ServerSocket(PORT);
                client = server.accept();

                PrintWriter o = new PrintWriter(client.getOutputStream(), true);
                BufferedReader i = new BufferedReader(new InputStreamReader(client.getInputStream()));

                initialize(o, i);
                Log.i("Server init");
            } catch (Exception e) {
                Log.e("Server constructor exception!");
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onReceiveMessage(String message) {
        Log.i(String.format("Server got message \"%s\".", message));
        //sendMessage("Stop bugging me");
        if (message.startsWith("quneo:")) {
            message = message.replace("quneo:", "");
            if (Robot.quneo != null) Robot.quneo.handlePacket(message);
            //TODO: Figure out the QuNeo data sending format
        } else if (message.startsWith("keyboard:")) { //example message: "keyboard:72:true"
            message = message.replace("keyboard:", "");
            String[] data = message.split(":");
            int keycode = Integer.parseInt(data[0]);
            KeyboardButton b = KeyboardButton.getButton(keycode);
            if (b != null) {
                b.set(Boolean.parseBoolean(data[1]));
            }
        }
    }
}
