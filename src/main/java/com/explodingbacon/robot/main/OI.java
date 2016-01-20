package com.explodingbacon.robot.main;

import com.explodingbacon.bcnlib.framework.Button;
import com.explodingbacon.bcnlib.framework.ExtendableOI;
import com.explodingbacon.bcnlib.framework.JoystickButton;
import edu.wpi.first.wpilibj.Joystick;

public class OI extends ExtendableOI {

    public static Joystick left = new Joystick(0); //Left driver joystick
    public static Joystick right = new Joystick(1); //Right driver joystick
    public static Joystick manip = new Joystick(2); //Manipulator joystick

    public static Button intakeMotorIn = new JoystickButton(manip, 4);
    public static Button intakeMotorOut = new JoystickButton(manip, 5);

    public static Button shooterRev = new JoystickButton(manip, 1);
    public static Button shooterShoot1 = new JoystickButton(manip, 2);
    public static Button shooterShoot2 = new JoystickButton(manip, 3);

    public OI() {
        start();
    }
}
