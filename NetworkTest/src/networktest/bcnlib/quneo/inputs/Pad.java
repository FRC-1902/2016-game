package networktest.bcnlib.quneo.inputs;

import networktest.Main;
import networktest.bcnlib.Log;
import networktest.bcnlib.Utils;
import networktest.bcnlib.quneo.QuNeoColor;

public class Pad extends QuNeoInput {

    private int pressure = 0, pressX = 0, pressY = 0;
    private final int cc_pressure, cc_pressX, cc_pressY;
    private QuNeoColor currentColor = QuNeoColor.NONE;

    public Pad(int note) {
        super(note);
        inputType = InputType.PAD;

        int addAmt = (note - 36) * 3;
        cc_pressure = 23 + addAmt;
        cc_pressX = 24 + addAmt;
        cc_pressY = 25 + addAmt;

        subscribeToCC(cc_pressure, cc_pressX, cc_pressY);
    }

    @Override
    public void updateData(int cc, int data) {
        if (cc == cc_pressure) {
            pressure = data;
        }
        else if (cc == cc_pressX) {
            pressX = data;
        }
        else if (cc == cc_pressY) {
            pressY = data;
        }
        /*
        if (pressure >= 60) setColor(QuNeoColor.RED, 127);
        else setColor(QuNeoColor.GREEN, 127);
        */
        Log.d("Pad pressure: " + getPressure() + ", Press X/Y: (" + pressX + ", " + pressY + ")");
    }

    /**
     * Gets the current pressure on this Pad. Ranges 0-1.
     *
     * @return The current pressure on this Pad.
     */
    public double getPressure() {
        return Utils.toRobotScale(pressure, 127);
    }

    /**
     * Gets where on the X-axis this Pad is being pressed. Ranges 0-127. 63 is "not being pressed".
     *
     * @return Where on the X-axis this Pad is being pressed.
     */
    public int getPressX() {
        return pressX;
    }

    /**
     * Gets where on the Y-axis this Pad is being pressed. Ranges 0-127. 63 is "not being pressed".
     *
     * @return Where on the Y-axis this Pad is being pressed.
     */
    public int getPressY() {
        return pressY;
    }

    private int[] getWholePadColors() {
        int addAmt = (note - 36) * 2;
        return new int[]{addAmt, 1 + addAmt};
    }

    private int[] getGridPadColors() {
        int row = -1;
        if (note <= 51 && note >= 48) {
            row = 1;
        } else if (note <= 47 && note >= 44) {
            row = 2;
        } else if (note <= 43 && note >= 40) {
            row = 3;
        } else if (note <= 39 && note >= 36) {
            row = 4;
        }
        if (row != -1) {
            int[] top, bottom, topAdds, bottomAdds;
            int firstNote;
            if (row == 1) {
                firstNote = 48;
                topAdds = new int[]{112, 113, 114, 115};
                bottomAdds = new int[]{96, 97, 98, 99};
            } else if (row == 2) {
                firstNote = 44;
                topAdds = new int[]{80, 81, 82, 83};
                bottomAdds = new int[]{64, 65, 66, 67};
            } else if (row == 3) {
                firstNote = 40;
                topAdds = new int[]{48, 49, 50, 51};
                bottomAdds = new int[]{32, 33, 34, 35};
            } else {
                firstNote = 36;
                topAdds = new int[]{16, 17, 18, 19};
                bottomAdds = new int[]{0, 1, 2, 3};
            }

            int addAmt = (note - firstNote) * 4;

            top = new int[]{topAdds[0] + addAmt, topAdds[1] + addAmt, topAdds[2] + addAmt, topAdds[3] + addAmt};
            bottom = new int[]{bottomAdds[0] + addAmt, bottomAdds[1] + addAmt, bottomAdds[2] + addAmt, bottomAdds[3] + addAmt};

            return new int[]{top[0], top[1], top[2], top[3], bottom[0], bottom[1], bottom[2], bottom[3]};
        }
        return new int[]{};
    }

    /**
     * Turns off this Pad's LEDs.
     */
    public void setColorOff() {
        setColor(QuNeoColor.NONE, 0);
    }

    /**
     * Sets this Pad to a solid color.
     *
     * @param color The color.
     * @param strength How strong the LEDs should be. Range 0-1.
     */
    public void setColor(QuNeoColor color, double strength) {
        if (currentColor != color) {
            int[] colors = getWholePadColors();

            setGridOff();

            Main.quneo.setColor(0, colors[0], color.green, Utils.toScale(strength, 127));
            Main.quneo.setColor(0, colors[1], color.red, Utils.toScale(strength, 127));

            currentColor = color;
        }
    }

    /**
     * Turns off this Pad's grid LEDs.
     */
    public void setGridOff() {
        setGridColor(QuNeoColor.NONE, QuNeoColor.NONE, QuNeoColor.NONE, QuNeoColor.NONE, 0, 0, 0, 0);
    }

    /**
     * Sets all of this Pad's grid LEDs.
     *
     * @param topLeft The color of the top left LED.
     * @param topRight The color of the top right LED.
     * @param bottomLeft The color of the bottom left LED.
     * @param bottomRight The color of the bottom right LED.
     * @param tLeftStrength How strong the top left LED should be. Range 0-1.
     * @param tRightStrength How strong the top right LED should be. Range 0-1.
     * @param bLeftStrength How strong the bottom left LED should be. Range 0-1.
     * @param bRightStrength How strong the bottom right LED should be. Range 0-1.
     */
    public void setGridColor(QuNeoColor topLeft, QuNeoColor topRight, QuNeoColor bottomLeft, QuNeoColor bottomRight,
                             double tLeftStrength, double tRightStrength, double bLeftStrength, double bRightStrength) {
        int[] colorIDs = getGridPadColors();
        QuNeoColor[] gridColors = new QuNeoColor[]{topLeft, topRight, bottomLeft, bottomRight};
        double[] strengths = new double[]{tLeftStrength, tRightStrength, bLeftStrength, bRightStrength};
        for (int i=0; i<gridColors.length; i++) {
            QuNeoColor color = gridColors[i];
            int greenID = i * 2;
            Main.quneo.setColor(1, colorIDs[greenID], color.green, Utils.toScale(strengths[i], 127));
            Main.quneo.setColor(1, colorIDs[greenID + 1], color.red, Utils.toScale(strengths[i], 127));
        }
    }
}
