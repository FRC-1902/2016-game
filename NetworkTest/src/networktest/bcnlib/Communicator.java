package networktest.bcnlib;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for communicating between two sockets.
 *
 * @author Ryan Shavell
 * @version 2016.10.2
 */
public abstract class Communicator {

    private PrintWriter out = null;
    private BufferedReader in = null;

    private static boolean init = false;

    private List<String> outQueue = new ArrayList<>();
    private final Object MESSAGE_QUEUE_EDIT = new Object();

    public Communicator() {}

    public Communicator(PrintWriter o, BufferedReader i) {
        out = o;
        in = i;
    }

    public void initialize(PrintWriter o, BufferedReader i) {
        if (o != null || i != null) {
            out = o;
            in = i;
        }
        initialize();
    }

    public void initialize() {
        messageOut.start();
        messageIn.start();
        init = true;
    }

    public abstract void onReceiveMessage(String message);

    protected CodeThread messageIn = new CodeThread(() -> {
        try {
            String input;
            while (in.ready() && (input = in.readLine()) != null) {
                onReceiveMessage(input);
            }
            Thread.sleep(5); //To stop from looping at the speed of go
        } catch (Exception e) {
            Log.e("Communicator.messageIn exception!");
            try {
                in.close();
            } catch (Exception e2) {
                e2.printStackTrace();;
            }
        }
    });

    protected CodeThread messageOut = new CodeThread(() -> {
        try {
            synchronized (MESSAGE_QUEUE_EDIT) {
                for (String m : outQueue) {
                    out.println(m);
                    //Log.d(String.format("Sent message \"%s\"", m));
                }
                outQueue.clear();
            }
            Thread.sleep(5); //To stop from looping at the speed of go
        } catch (Exception e) {
            Log.e("Communicator.messageOut exception!");
            try {
                out.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    });

    /**
     * Sends a message.
     *
     * @param m The message to send.
     */
    public void sendMessage(String m) {
        synchronized (MESSAGE_QUEUE_EDIT) {
            outQueue.add(m);
        }
    }

    /**
     * Checks if this Communicator is initialized.
     *
     * @return If this Communicator is initialized.
     */
    public static boolean isInit() {
        return init;
    }
}
