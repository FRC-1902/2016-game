package networktest.bcnlib.quneo.inputs;

import networktest.Main;
import networktest.bcnlib.Log;
import networktest.bcnlib.Utils;

public class Rotary extends QuNeoInput {

    private int pressure = 0, direction = 0;
    private final int cc_pressure, cc_direction;

    public Rotary(int note) {
        super(note);
        inputType = InputType.ROTARY;

        int addAmt = (note - 4);
        cc_pressure = 16 + addAmt;
        cc_direction = 4 + addAmt;

        subscribeToCC(cc_pressure, cc_direction);
    }

    @Override
    public void updateData(int cc, int data) {
        if (cc == cc_pressure) {
            pressure = data;
        } else if (cc == cc_direction) {
            direction = data;
        }
        //Log.d("Rotary pressure: " + getPressure() + ", Direction: " + direction);
    }

    /**
     * Gets the current pressure on this Rotary. Ranges 0-1.
     *
     * @return The current pressure on this Rotary.
     */
    public double getPressure() {
        return Utils.toRobotScale(pressure, 127);
    }

    /**
     * Gets the direction of the most recent press on this Rotary. Ranges 0-127.
     *
     * @return The direction of the most recent press on this Rotary.
     */
    public int getDirection() {
        return direction;
    }

    /**
     * Sets the direction this Rotary's LED should point in. Ranges 0-127.
     *
     * @param direction The direction this Rotary's LED should point in.
     */
    public void setLED(int direction) {
        int cc = 6 + (note - 4);
        Main.quneo.setCC(0, cc, direction);
    }
}
