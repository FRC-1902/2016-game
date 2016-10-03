package networktest;

import networktest.bcnlib.Communicator;
import networktest.bcnlib.Log;
import networktest.bcnlib.Utils;
import networktest.bcnlib.quneo.*;
import networktest.bcnlib.quneo.inputs.HorizontalArrow;
import networktest.bcnlib.quneo.inputs.HorizontalSlider;
import networktest.bcnlib.quneo.inputs.Pad;
import networktest.bcnlib.quneo.inputs.Rotary;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server code (robot-side) for communication between a robot and a client. Currently has some robot-specific code in
 * it (this will eventually be separated from this server "template").
 *
 * @author Ryan Shavell
 * @version 2016.10.3
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

    boolean first = true;

    @Override
    public void onReceiveMessage(String message) {
        //Log.i(String.format("Server got message \"%s\".", message));
        if (first) {
            Pad[][] pads = new Pad[4][4];
            int padNum = 48;
            for (int y=0;y<4;y++) {
                for (int x=0;x<4;x++) {
                    pads[x][y] = new Pad(padNum);
                    //System.out.println("Pad " + padNum);
                    padNum++;
                }
                padNum -=8;
            }

            //AnimationHandler.doAnimation(pads, .4, true, "one.png", "nine.png", "zero.png", "two.png");

            new HorizontalSlider(0).setLights(0.5);

            pads[1][1].onNotePress(data -> pads[1][1].setColor(QuNeoColor.RED, 1));
            pads[1][1].onNoteRelease(data -> pads[1][1].setColor(QuNeoColor.ORANGE, 1));

            Rotary r = new Rotary(5);
            r.onControlChange((cc, data) -> r.setLED(r.getDirection()));

            new HorizontalArrow(18).setColor(true, 1);

            new HorizontalArrow(19).setColor(true, 1);

            first = false;
        }

        if (message.startsWith("quneo:")) {
            message = message.replace("quneo:", "");
            if (Main.quneo != null) Main.quneo.handlePacket(message);
        }/* else if (message.startsWith("keyboard:")) { //example message: "keyboard:72:true"
            message = message.replace("keyboard:", "");
            String[] data = message.split(":");
            int keycode = Integer.parseInt(data[0]);
            KeyboardButton b = KeyboardButton.getButton(keycode);
            if (b != null) {
                b.set(Boolean.parseBoolean(data[1]));
            }
        }*/
    }
}
