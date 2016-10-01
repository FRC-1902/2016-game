package networktest.bcnlib;

import networktest.Main;
import networktest.quneo.ImageHandler;
import networktest.quneo.Pad;
import networktest.quneo.QuNeo;
import networktest.quneo.QuNeoColor;

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

                //Main.quneo.subscribeTo(QuNeo.Type.NOTE_ON, 1, 48, 49);
                //Main.quneo.subscribeTo(QuNeo.Type.NOTE_OFF, 1, 48, 49);
            } catch (Exception e) {
                Log.e("Server constructor exception!");
                e.printStackTrace();
            }
        });
    }

    boolean first = true;

    @Override
    public void onReceiveMessage(String message) {
        Log.i(String.format("Server got message \"%s\".", message));
        if (first) { //TODO move all of this to where it belongs
            Pad[][] pads = new Pad[4][4];
            int padNum = 48;
            for (int y=0;y<4;y++) {
                for (int x=0;x<4;x++) {
                    pads[x][y] = new Pad(padNum);
                    System.out.println("Pad " + padNum);
                    padNum++;
                }
                padNum -=8;
            }

            ImageHandler.displayImage(pads, "one.png");

            try {
                Thread.sleep(2000);
            } catch (Exception e) {}

            for (Pad[] row : pads) {
                for (Pad p : row) {
                    p.setColorOff();
                }
            }

            try {
                Thread.sleep(200);
            } catch (Exception e) {}

            ImageHandler.displayImage(pads, "nine.png");

            try {
                Thread.sleep(2000);
            } catch (Exception e) {}

            for (Pad[] row : pads) {
                for (Pad p : row) {
                    p.setColorOff();
                }
            }

            try {
                Thread.sleep(200);
            } catch (Exception e) {}

            ImageHandler.displayImage(pads, "zero.png");

            try {
                Thread.sleep(2000);
            } catch (Exception e) {}

            for (Pad[] row : pads) {
                for (Pad p : row) {
                    p.setColorOff();
                }
            }

            try {
                Thread.sleep(200);
            } catch (Exception e) {}

            ImageHandler.displayImage(pads, "two.png");

            /*
            pads[1][0].setColor(QuNeoColor.ORANGE, 127);
            pads[2][0].setColor(QuNeoColor.ORANGE, 127);
            pads[3][0].setColor(QuNeoColor.ORANGE, 127);
            pads[1][1].setColor(QuNeoColor.ORANGE, 127);
            pads[3][1].setColor(QuNeoColor.ORANGE, 127);
            pads[1][2].setColor(QuNeoColor.ORANGE, 127);
            pads[2][2].setColor(QuNeoColor.ORANGE, 127);
            pads[3][2].setColor(QuNeoColor.ORANGE, 127);
            pads[3][3].setColor(QuNeoColor.ORANGE, 127);

            try {
                Thread.sleep(2000);
            } catch (Exception e) {}

            for (Pad[] row : pads) {
                for (Pad p : row) {
                    p.setColorOff();
                }
            }

            try {
                Thread.sleep(200);
            } catch (Exception e) {}

            pads[1][0].setColor(QuNeoColor.ORANGE, 127);
            pads[2][0].setColor(QuNeoColor.ORANGE, 127);
            pads[3][0].setColor(QuNeoColor.ORANGE, 127);
            pads[1][1].setColor(QuNeoColor.ORANGE, 127);
            pads[3][1].setColor(QuNeoColor.ORANGE, 127);
            pads[1][2].setColor(QuNeoColor.ORANGE, 127);
            pads[3][2].setColor(QuNeoColor.ORANGE, 127);
            pads[1][3].setColor(QuNeoColor.ORANGE, 127);
            pads[2][3].setColor(QuNeoColor.ORANGE, 127);
            pads[3][3].setColor(QuNeoColor.ORANGE, 127);

*/
            first = false;
        }
        //sendMessage("Stop bugging me");
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
