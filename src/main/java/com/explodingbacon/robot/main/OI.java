package com.explodingbacon.robot.main;

import com.explodingbacon.bcnlib.framework.Button;
import com.explodingbacon.bcnlib.framework.ExtendableOI;
import com.explodingbacon.bcnlib.framework.Joystick;
import com.explodingbacon.bcnlib.framework.JoystickButton;
import com.explodingbacon.bcnlib.utils.Xbox;

public class OI extends ExtendableOI {

    public static Joystick drive; //Left driver joystick
    public static Joystick manip; //Manipulator joystick (sometimes the same as the driver)

    public static Button leftJoy, rightJoy;

    public static Button intakeMotorIn;
    public static Button intakeMotorOut;

    public static Button shooterRev;
    public static Button shooterShoot;

    public static Button takePicture;

    public OI() {
        init();
        start();
    }

    public static void init() {
        drive = new Joystick(0);
        if (Robot.driverChooser.getSelected().equals("one")) {
            manip = drive;
        } else {
            manip = new Joystick(1);
        }

        leftJoy = new JoystickButton(drive, 11);
        rightJoy = new JoystickButton(drive, 12);

        intakeMotorIn = new JoystickButton(manip, 3);
        intakeMotorOut = new JoystickButton(manip, 5);

        shooterRev = new JoystickButton(manip, 1);
        shooterShoot = new JoystickButton(drive, 6);

        takePicture = new JoystickButton(manip, 8);
    }
}
