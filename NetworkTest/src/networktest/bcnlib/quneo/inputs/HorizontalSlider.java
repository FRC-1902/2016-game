package networktest.bcnlib.quneo.inputs;

import networktest.Main;
import networktest.bcnlib.Utils;

/**
 * A class that represents any Horizontal Slider on the QuNeo.
 *
 * @author Ryan Shavell
 * @version 2016.10.3
 */
public class HorizontalSlider extends QuNeoInput {

    private int pressure = 0, location = 0;
    private final int cc_pressure, cc_location;

    public HorizontalSlider(int note) {
        super(note);
        inputType = InputType.HORIZONTAL_SLIDER;

        cc_pressure = 12 + note;
        cc_location = note;

        subscribeToCC(cc_pressure, cc_location);
    }

    @Override
    public void updateData(int cc, int data) {
        if (cc == cc_pressure) {
            pressure = data;
        } else if (cc == cc_location) {
            location = data;
        }
    }

    /**
     * Gets the current pressure on this HorizontalSlider. Ranges 0-1.
     *
     * @return The current pressure on this HorizontalSlider.
     */
    public double getPressure() {
        return Utils.toRobotScale(pressure, 127);
    }

    /**
     * Gets where this HorizontalSlider is being pressed. Ranges 0-127. 63 is "not being pressed" (TODO: Confirm this).
     * @return Where this HorizontalSlider is being pressed.
     */
    public int getLocation() {
        return location;
    }

    /**
     * Sets the lights of this HorizontalSlider.
     * @param progress How far the lights should go. Ranges 0-1.
     */
    public void setLights(double progress) {
        int inputCC = 11 - note;
        Main.quneo.setCC(0, inputCC, Utils.toScale(progress, 127));
    }

}
