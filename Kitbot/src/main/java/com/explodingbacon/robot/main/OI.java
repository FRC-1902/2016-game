package com.explodingbacon.robot.main;

import com.explodingbacon.bcnlib.controllers.Button;
import com.explodingbacon.bcnlib.controllers.Joystick;
import com.explodingbacon.bcnlib.controllers.JoystickButton;
import com.explodingbacon.bcnlib.controllers.LogitechController;
import com.explodingbacon.bcnlib.framework.ExtendableOI;

public class OI extends ExtendableOI {

    public static LogitechController drive; //Left driver joystick
    public static Joystick manip; //Manipulator joystick (sometimes the same as the driver)

    public static Button intakeMotorIn;
    public static Button intakeMotorOut;

    public static Button shooterRev;

    public static Button targetLock;

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

        targetLock = new JoystickButton(manip, 8);
    }
}
