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

    public static Button shooterRev, shooterRevBad;
    public static ButtonGroup shooterRevButtons;

    public static Button deployClimber, climb;

    public static Button testingBall;

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
        intakeMotorOut = manip.y;

        shooterRev = manip.rightTrigger;
        shooterRevBad = manip.leftTrigger;
        shooterRevButtons = manip.triggers;

        deployClimber = manip.start; //TODO: check if this is the left
        climb = manip.select;

        testingBall = new FakeButton();
    }
}
