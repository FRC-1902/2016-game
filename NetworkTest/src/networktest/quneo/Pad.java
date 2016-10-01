package networktest.quneo;

import networktest.Main;
import networktest.bcnlib.Log;

import java.util.ArrayList;
import java.util.List;

public class Pad extends QuNeoInput {

    public Pad(int note) {
        super(note);
        inputType = Type.PAD;
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

    public void setColorOff() {
        setColor(QuNeoColor.NONE, 0);
    }

    public void setColor(QuNeoColor color, int strength) {
        int[] colors = getWholePadColors();
        boolean red = false, green = false;
        if (color == QuNeoColor.ORANGE) {
            green = true;
            red = true;
        } else if (color == QuNeoColor.GREEN) green = true;
        else if (color == QuNeoColor.RED) red = true;

        //if (color == QuNeoColor.NONE) Log.d("NONE PLS");

       // Log.d(color.toString() + " : green " + green + " : red " + red);

        setGridOff();

        Main.quneo.setColor(0, colors[0], green, strength);
        Main.quneo.setColor(0, colors[1], red, strength);
    }

    public void setGridOff() {
        setGridColor(QuNeoColor.NONE, QuNeoColor.NONE, QuNeoColor.NONE, QuNeoColor.NONE, 0, 0, 0, 0);
    }

    public void setGridColor(QuNeoColor topLeft, QuNeoColor topRight, QuNeoColor bottomLeft, QuNeoColor bottomRight,
                             int tLeftStrength, int tRightStrength, int bLeftStrength, int bRightStrength) {
        int[] colorIDs = getGridPadColors();
        QuNeoColor[] gridColors = new QuNeoColor[]{topLeft, topRight, bottomLeft, bottomRight};
        int[] strengths = new int[]{tLeftStrength, tRightStrength, bLeftStrength, bRightStrength};
        for (int i=0; i<gridColors.length; i++) {
            QuNeoColor color = gridColors[i];
            int greenID = i * 2;
            Main.quneo.setColor(1, colorIDs[greenID], color.green, strengths[i]);
            Main.quneo.setColor(1, colorIDs[greenID + 1], color.red, strengths[i]);
        }
    }
}
