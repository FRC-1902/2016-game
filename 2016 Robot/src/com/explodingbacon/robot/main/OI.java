package com.explodingbacon.robot.main;

import com.explodingbacon.bcnlib.controllers.*;
import com.explodingbacon.bcnlib.framework.AbstractOI;

public class OI extends AbstractOI {

    public static LogitechController drive; //Driver controller
    public static XboxController manip; //Manipulator controller

    //Driver buttons

    public static ButtonGroup lowShift;

    public static ButtonGroup shoot;

    public static Button shootNoVision, shootAbort;

    //Manipulator buttons

    public static ButtonGroup intakeRetract;
    public static Button intakeMotorIn, intakeMotorOut;

    public static Button shooterRevLow, shooterRevHigh;
    public static ButtonGroup shooterRevButtons;

    public static Button climberShoot, climb;

    public static Button testingBall;

    public static Button trimLeftUp, trimLeftDown, trimRightUp, trimRightDown;
    public static Button resetLeftTrim, resetRightTrim;
    public static ButtonGroup trimButtons;

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

        lowShift = drive.bumpers;

        shoot = drive.triggers;

        shootNoVision = drive.one;

        shootAbort = drive.two;

        //Manipulator controls

        intakeRetract = manip.bumpers;

        intakeMotorIn = manip.x;
        intakeMotorOut = manip.a;

        shooterRevLow = manip.leftTrigger;
        shooterRevHigh = manip.rightTrigger;
        shooterRevButtons = manip.triggers;

        climberShoot = manip.select; //TODO: check if this is the left
        climb = manip.start;

        testingBall = new FakeButton();

        trimLeftUp = new FakeButton(() -> manip.getY2() < -0.8);
        trimLeftDown = new FakeButton(() -> manip.getY2() > 0.8);
        trimRightUp = new FakeButton(() -> manip.getY() < -0.8);
        trimRightDown = new FakeButton(() -> manip.getY() > 0.8);

        trimButtons = new ButtonGroup(trimLeftUp, trimLeftDown, trimRightUp, trimRightDown);

        resetLeftTrim = manip.leftJoyButton;
        resetRightTrim = manip.rightJoyButton;
    }
}
