package networktest.bcnlib.quneo.inputs;

import networktest.Main;
import networktest.bcnlib.quneo.IntArrayGetter;
import networktest.bcnlib.quneo.QuNeo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * An asbtract class that represents any button or slider on the QuNeo.
 *
 * @author Ryan Shavell
 * @version 2016.10.2
 */
public abstract class QuNeoInput {

    public static List<QuNeoInput> quNeoInputs = new ArrayList<>();

    protected Consumer<Integer> notePress = null, noteRelease = null;
    protected CCEvent controlChange = null;
    protected List<Integer> ccs = new ArrayList<>();

    protected final int note;
    protected InputType inputType = null;

    private boolean isPressed = false;

    public QuNeoInput(int note) {
        this.note = note;
        boolean taken = false;
        for (QuNeoInput i : quNeoInputs) {
            if (i.note == note) {
                taken = true;
                break;
            }
        }
        if (!taken) {
            Main.quneo.subscribeTo(QuNeo.Type.NOTE_ON, 1, note);
            Main.quneo.subscribeTo(QuNeo.Type.NOTE_OFF, 1, note);
            quNeoInputs.add(this);
        }
    }

    public void subscribeToCC(int...ccList) {
        Main.quneo.subscribeTo(QuNeo.Type.CONTROL_CHANGE, 1, ccList);
        for (int i : ccList) {
            ccs.add(i);
        }
    }

    public void onNotePress(Consumer<Integer> t) {
        notePress = t;
    }

    public void onNoteRelease(Consumer<Integer> t) {
        noteRelease = t;
    }

    public void onControlChange(CCEvent e) {
        controlChange = e;
    }

    public void triggerNotePress(int data) {
        setPressed(true);
        //updateData(data);
        if (notePress != null) notePress.accept(data);
    }

    public void triggerNoteRelease(int data) {
        setPressed(false);
        //updateData(data);
        if (noteRelease != null) noteRelease.accept(data);
    }

    public void triggerControlChange(int cc, int data) {
        updateData(cc, data);
        if (controlChange != null) controlChange.onControlChange(cc, data);
    }

    public abstract void updateData(int cc, int data);

    public int getNote() {
        return note;
    }

    public List<Integer> getCCS() {
        return new ArrayList<>(ccs);
    }

    public boolean getPressed() {
        return isPressed;
    }

    protected void setPressed(boolean b) {
        isPressed = b;
    }

    public static QuNeoInput getInput(int note) {
        for (QuNeoInput i : quNeoInputs) {
            if (i.note == note) {
                return i;
            }
        }
        return null;
    }

    public static QuNeoInput getInputByCC(int cc) {
        for (QuNeoInput i : quNeoInputs) {
            if (i.ccs.contains(cc)) {
                return i;
            }
        }
        return null;
    }

    public enum Type {
        ROTARIES(n -> new int[]{n + 12, n}, null, 4, 5),

        PAD(n -> {
            int addAmt = (n - 36) * 3;
            return new int[]{59 + addAmt, 60 + addAmt, 61 + addAmt};
        }, n -> {
            int addAmt = (n - 48) * 2;
            //System.out.println("Note: " + n + ", addAmt: " + addAmt);
            return new int[]{24 + addAmt, 25 + addAmt};
        }, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51),

        BUTTON(n -> {
            return new int[]{0}; //TODO: formula for cc channels
        }, null, 11, 12, 13, 14, 15, 16, 17, 18);

        /*
        RHOMBUS(new int[]{19}, n-> new int[]{79}),
        VERTICAL_SLIDER(new int[]{6 ,7, 8, 9}, n -> {
            int vertID = n - 6;
            return new int[]{18 + vertID, n};
        }),

        HORIZONTAL_SLIDER(new int[]{0, 1, 2, 3}, n-> {
            return new int[]{12 + n, n};
        }),
        BIG_SLIDER(new int[]{10}, n -> new int[]{22, 10, 11}),
        UP_DOWN(new int[]{20, 21, 22, 23}, n -> {
            int updownID = n - 20;
            return new int[]{80 + updownID};
        });*/

        int[] ids;
        IntArrayGetter noteToCC;
        IntArrayGetter noteToColors;

        Type(IntArrayGetter ccSupplier, IntArrayGetter colorSupplier, int...ids) {
            this.ids = ids;
            noteToCC = ccSupplier;
            noteToColors = colorSupplier;
        }
    }
}
