package com.explodingbacon.robot.main;

import com.explodingbacon.bcnlib.controllers.*;
import com.explodingbacon.bcnlib.framework.AbstractOI;

public class OI extends AbstractOI {

    public static LogitechController drive; //Driver controller
    public static XboxController manip; //Manipulator controller

    //Driver buttons

    public static Button gyroForward;

    public static ButtonGroup lowShift;

    public static ButtonGroup shoot;

    //Manipulator buttons

    public static ButtonGroup intakeRetract;
    public static Button intakeMotorIn;
    public static Button intakeMotorOut;

    public static Button shooterRev;

    public static Button climberDeploy;
    public static Button climberRetract;

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

        intakeMotorIn = manip.x;
        intakeMotorOut = manip.y;

        shooterRev = manip.triggers; //TODO: Only use this if there is not a delay from press to telling the motors to move

        climberDeploy = manip.start; //HAS TO BE LEFT BUTTON
        climberRetract = manip.select; //HAS TO BE RIGHT BUTTON
    }
}
