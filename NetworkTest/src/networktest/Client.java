package networktest;

import networktest.bcnlib.Communicator;
import networktest.bcnlib.Log;
import networktest.bcnlib.MidiAPI;
import networktest.bcnlib.Utils;
import networktest.bcnlib.quneo.QuNeo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
/**
 * Client code (non-robot side) for communication between a robot and a client.
 *
 * @author Ryan Shavell
 * @version 2016.9.28
 */
public class Client extends Communicator {

    private String ip = "localhost";//"10.19.2.2";
    private int port = 5800;
    private Socket server;

    private MidiAPI api = new MidiAPI("QUNEO");

    public Client() {
        super();
        Utils.runInOwnThread(() -> {
            try {
                server = new Socket(ip, port);

                PrintWriter o = new PrintWriter(server.getOutputStream(), true);
                BufferedReader i = new BufferedReader(new InputStreamReader(server.getInputStream()));

                initialize(o, i);

                Log.c("CLIENT", "QuNeo client initialized!");

                sendMessage("Hello server :)");
            } catch (Exception e) {
                Log.e("Client constructor exception!");
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onReceiveMessage(String message) {
        //Log.i(String.format("Client got message \"%s\".", message));
        if (message.startsWith("quneo:")) {
            message = message.replace("quneo:", "");
            if (message.startsWith("subscribe:")) { //example: quneo:subscribe:note_on:5:2:78
                message = message.replace("subscribe:", "");
                String[] data = message.split(":");
                int type = Integer.parseInt(data[0]);
                int[] subscribees = new int[data.length-1];
                for (int i=1;i<data.length;i++) {
                    subscribees[i-1] = (Integer.parseInt(data[i]));
                    //Log.d(subscribees[i-1] + " - subscriber note");
                }
                //Log.d("Subscriber count: " + subscribees.length + ", type: " + type + ", " + subscribees.toString());
                api.registerListener((eventType, channel, note, data1) -> {
                    sendMessage("quneo:update:"+ eventType + ":" + note + ":" + data1); //quneo:update:type:note:data
                }, type, 1, subscribees);
            } else if (message.startsWith("setlight:")) {
                message = message.replace("setlight:", "");
                String[] data = message.split(":");
                api.sendNote(Byte.parseByte(data[0]), Boolean.parseBoolean(data[2]), Byte.parseByte(data[1]), Byte.parseByte(data[3]));
            } else if (message.startsWith("setcc:")) { //quneo:setcc:channel:note:data
                Log.d("Client set cc");
                message = message.replace("setcc:", "");
                String[] data = message.split(":");
                System.out.println("Data: " + Byte.parseByte(data[2]));
                api.sendCC(Byte.parseByte(data[0]), Byte.parseByte(data[1]), Byte.parseByte(data[2]));
            }
        }
    }
}
