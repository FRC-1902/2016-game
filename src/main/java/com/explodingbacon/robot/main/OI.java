package com.explodingbacon.robot.main;

import com.explodingbacon.bcnlib.framework.Button;
import com.explodingbacon.bcnlib.framework.ExtendableOI;
import com.explodingbacon.bcnlib.framework.JoystickButton;
import edu.wpi.first.wpilibj.Joystick;

public class OI extends ExtendableOI {

    public static Joystick drive = new Joystick(0); //Left driver joystick
    public static Joystick manip = new Joystick(1); //Manipulator joystick

    public static Button intakeMotorIn = new JoystickButton(manip, 3);
    public static Button intakeMotorOut = new JoystickButton(manip, 5);

    public static Button shooterRev = new JoystickButton(manip, 1);
    public static Button shooterShoot = new JoystickButton(manip, 2);

    public OI() {
        start();
    }
}
