package networktest.bcnlib.quneo;

import networktest.bcnlib.quneo.inputs.Pad;

/**
 * Plays animations on the 4x4 Pad section of the QuNeo.
 *
 * @author Ryan Shavell
 * @version 2016.10.1
 */
public class AnimationHandler {

    public static void doAnimation(Pad[][] pads, Double secondsPerFrame, boolean wipeEffect, String...fileNames) {
        for (String s : fileNames) {
            try {
                ImageHandler.displayImage(pads, s);
                Double millis = secondsPerFrame * 1000;
                Thread.sleep(millis.intValue());
                if (wipeEffect) {
                    for (Pad[] array : pads) {
                        for (Pad p : array) p.setGridOff();
                    }
                    Thread.sleep(100);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (Pad[] array : pads) {
            for (Pad p : array) p.setGridOff();
        }
    }
}
