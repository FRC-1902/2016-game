package com.explodingbacon.robot.main;

import com.explodingbacon.bcnlib.controllers.*;
import com.explodingbacon.bcnlib.framework.AbstractOI;

public class OI extends AbstractOI {

    public static LogitechController drive; //Driver controller
    public static XboxController manip; //Manipulator controller

    //Driver buttons

    public static ButtonGroup lowShift;

    public static ButtonGroup shoot;

    public static Button shootNoVision;

    //Manipulator buttons

    public static ButtonGroup intakeRetract;
    public static Button intakeMotorIn;
    public static Button intakeMotorOut;

    public static Button shooterBadRev;
    public static Button shooterGoodRev;
    public static ButtonGroup shooterRevButtons;

    public static Button climberDeploy;
    public static Button climberRetract;

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

        //Manipulator controls

        intakeRetract = manip.bumpers;

        intakeMotorIn = manip.x;
        intakeMotorOut = manip.y;

        shooterBadRev = manip.leftTrigger;
        shooterGoodRev = manip.rightTrigger;
        shooterRevButtons = manip.triggers;

        climberDeploy = manip.start; //HAS TO BE LEFT BUTTON
        climberRetract = manip.select; //HAS TO BE RIGHT BUTTON

        testingBall = new FakeButton();
    }
}
