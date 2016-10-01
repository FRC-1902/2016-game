package networktest.quneo;

import networktest.Main;

import java.util.ArrayList;
import java.util.List;

public class QuNeoInput {

    public static List<QuNeoInput> quNeoInputs = new ArrayList<>();

    protected Runnable notePress = null;
    protected Runnable noteRelease = null;

    protected final int note;
    protected Type inputType = null;
    protected int pressure = 0;

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

    public boolean get() {
        return isPressed;
    }

    public void set(boolean b) {
        isPressed = b;
    }

    public void handleControlChange(String eventType, String data) {}

    public void onNotePress(Runnable r) {
        notePress = r;
    }

    public void onNoteRelease(Runnable r) {
        noteRelease = r;
    }

    protected void triggerNotePress() {
        if (notePress != null) notePress.run();
    }

    protected void triggerNoteRelease() {
        if (noteRelease != null) noteRelease.run();
    }

    public static QuNeoInput getInput(int note) {
        for (QuNeoInput i : quNeoInputs) {
            if (i.note == note) {
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

        public int[] getCCS(int note) {
            return noteToCC.get(note);
        }

        public int[] getColors(int note) {
            return noteToColors.get(note);
        }
    }
}
