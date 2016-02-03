package com.explodingbacon.robot.main;

import com.explodingbacon.bcnlib.framework.Button;
import com.explodingbacon.bcnlib.framework.ExtendableOI;
import com.explodingbacon.bcnlib.framework.Joystick;
import com.explodingbacon.bcnlib.framework.JoystickButton;
import com.explodingbacon.bcnlib.utils.LogitechController;

public class OI extends ExtendableOI {

    public static LogitechController drive; //Left driver joystick
    public static Joystick manip; //Manipulator joystick (sometimes the same as the driver)

    public static Button intakeMotorIn;
    public static Button intakeMotorOut;

    public static Button shooterRev;

    public static Button takePicture;

    public OI() {
        init();
        start();
    }

    public static void init() {
        drive = new LogitechController(0);
        manip = new Joystick(1);

        intakeMotorIn = new JoystickButton(manip, 3);
        intakeMotorOut = new JoystickButton(manip, 5);

        shooterRev = new JoystickButton(manip, 1);

        takePicture = new JoystickButton(manip, 8);
    }
}
