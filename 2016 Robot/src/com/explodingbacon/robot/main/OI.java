package com.explodingbacon.robot.main;

import com.explodingbacon.bcnlib.controllers.*;
import com.explodingbacon.bcnlib.framework.AbstractOI;
import com.explodingbacon.bcnlib.networking.KeyboardButton;
import com.explodingbacon.bcnlib.utils.Utils;

public class OI extends AbstractOI {

    public static XboxController manip; //Manipulator controller

    public static Button rev;
    public static Button indexGo;

    public OI() {
        init();
        start();
    }

    /**
     * Initializes all the Joystick and Button variables.
     */
    public static void init() {
        manip = new XboxController(0);

        rev = manip.b;
        indexGo = manip.a;
    }
}
