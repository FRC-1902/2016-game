package networktest.quneo;

import networktest.Main;
import networktest.bcnlib.Log;
import networktest.bcnlib.MidiAPI;

public class QuNeo {

    /**
     * Subscribes the server to updates on the specified notes.
     *
     * @param t The type of event that is being tracked.
     * @param channel The channel on which the events are happening.
     * @param notes The notes that are launching the events.
     */
    public void subscribeTo(Type t, int channel, int...notes) {
        String noteString = "";
        for (int i : notes) noteString += ":" + i;
        Main.server.sendMessage("quneo:subscribe:" + t.getInt() + noteString);
    }

    /**
     * Handles a QuNeo-related packet that has been sent by the client.
     *
     * @param message The QuNeo-related packet.
     */
    public void handlePacket(String message) {
        if (message.startsWith("update:")) {
            Log.d("QuNeo.handlePacket()");
            message = message.replaceFirst("update:", "");
            String[] data = message.split(":");
            int type = Integer.parseInt(data[0]);
            int note = Integer.parseInt(data[1]);
            //Log.d(QuNeoInput.quNeoInputs.size() + " size");
            QuNeoInput qInput = QuNeoInput.getInput(note);
            if (qInput != null) {
                int colorCC = QuNeoInput.Type.PAD.getColors(note)[1];
                //Log.d("qInput is not null");
                if (type == MidiAPI.NOTE_ON) {
                    qInput.set(true);
                    //Log.d("Sending message");
                    Main.server.sendMessage("quneo:setlight:0:" + colorCC + ":true:127");
                } else if (type == MidiAPI.NOTE_OFF) {
                    qInput.set(false);
                    //Log.d("Sending message 2");
                    Main.server.sendMessage("quneo:setlight:0:" + colorCC + ":false:0");
                } else if (type == MidiAPI.CONTROL_CHANGE) {
                    qInput.handleControlChange(data[2], data[3]);
                }
            }
        }
    }

    public enum Type {
        NOTE_ON(MidiAPI.NOTE_ON),
        NOTE_OFF(MidiAPI.NOTE_OFF),
        CONTROL_CHANGE(MidiAPI.CONTROL_CHANGE);

        int t;

        Type(int i) {
            t = i;
        }

        public int getInt() {
            return t;
        }

        public String getString() {
            return this.toString().toLowerCase();
        }
    }
}
