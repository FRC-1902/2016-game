package networktest.bcnlib.quneo;

/**
 * Created by LenovoBacon1 on 10/1/2016.
 */
public enum QuNeoColor {

    RED(false, true),
    GREEN(true, false),
    ORANGE(true, true),
    NONE(false, false);

    public final boolean green, red;

    QuNeoColor(boolean g, boolean r) {
        green = g;
        red = r;
    }

}
