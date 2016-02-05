package com.explodingbacon.robot.main;

import com.explodingbacon.bcnlib.controllers.*;
import com.explodingbacon.bcnlib.framework.ExtendableOI;

public class OI extends ExtendableOI {

    public static LogitechController drive; //Driver controller
    public static XboxController manip; //Manipulator controller

    public static ButtonGroup intakeRetract;

    public static Button intakeMotorIn;
    public static Button intakeMotorOut;

    public static Button shooterRev;

    public static ButtonGroup shoot;

    public OI() {
        init();
        start();
    }

    public static void init() {
        drive = new LogitechController(0);
        manip = new XboxController(1);

        //Driver controls

        shoot = drive.triggers;

        //Manipulator controls

        intakeRetract = manip.bumpers;

        intakeMotorIn = manip.a; //Should be left button
        intakeMotorOut = manip.x; //Should be top button

        shooterRev = manip.b;
    }
}
