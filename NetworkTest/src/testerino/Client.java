package testerino;

import testerino.bcnlib.Log;
import testerino.bcnlib.MidiAPI;
import testerino.bcnlib.Utils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
/**
 * Client code (non-robot side) for communication between a robot and a client.
 *
 * @author Ryan Shavell
 * @version 2016.9.10
 */
public class Client extends Communicator {

    private String ip = "localhost";//"10.19.2.2";
    private int port = 5800;
    private Socket server;

    private MidiAPI api = new MidiAPI("quneo");

    public Client() {
        super();
        Utils.runInOwnThread(() -> {
            try {
                server = new Socket(ip, port);

                PrintWriter o = new PrintWriter(server.getOutputStream(), true);
                BufferedReader i = new BufferedReader(new InputStreamReader(server.getInputStream()));

                initialize(o, i);

                Log.i("Client init");

                sendMessage("Hello server :)");
            } catch (Exception e) {
                Log.e("Client constructor exception!");
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onReceiveMessage(String message) {
        Log.i(String.format("Client got message \"%s\".", message));
        if (message.startsWith("quneo:")) {
            message = message.replace("quneo:", "");
            if (message.startsWith("subscribe:")) { //example: quneo:subscribe:note_on:5:2:78
                message = message.replace("subscribe:", "");
                String[] data = message.split(":");
                int type = Integer.parseInt(data[0]);
                int[] subscribees = new int[data.length-1];
                for (int i=1;i<data.length;i++) {
                    subscribees[i-1] = (Integer.parseInt(data[i]));
                }
                api.registerListener((eventType, channel, note, data1) -> {
                    sendMessage("quneo:update:"+ data[0] + ":" + note + ":" + eventType + ":" + data1); //quneo:update:type:note:data
                }, type, 1, subscribees);
            }
        }
    }
}