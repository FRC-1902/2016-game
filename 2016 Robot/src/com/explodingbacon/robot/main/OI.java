package com.explodingbacon.robot.main;

import com.explodingbacon.bcnlib.controllers.*;
import com.explodingbacon.bcnlib.framework.AbstractOI;

public class OI extends AbstractOI {

    public static LogitechController drive; //Driver controller
    public static XboxController manip; //Manipulator controller

    public static Button gyroForward;

    public static ButtonGroup lowShift;

    public static ButtonGroup intakeRetract;

    public static Button intakeMotorIn;
    public static Button intakeMotorOut;
    public static Button shooterRev;

    public static ButtonGroup shoot;

    public OI() {
        init();
        start();
    }

    /**
     * Initializes all the Joystick and Button variables.
     */
    public static void init() {
        drive = new LogitechController(0);
        manip = new XboxController(1);

        //Driver controls

        gyroForward = drive.one;

        lowShift = drive.bumpers;

        shoot = drive.triggers;

        //Manipulator controls

        intakeRetract = manip.bumpers;

        intakeMotorIn = manip.a; //Should be left button
        intakeMotorOut = manip.x; //Should be top button

        shooterRev = manip.b;
    }
}
