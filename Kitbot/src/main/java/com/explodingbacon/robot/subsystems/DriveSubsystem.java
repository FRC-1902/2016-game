package com.explodingbacon.robot.subsystems;

import com.explodingbacon.bcnlib.actuators.Motor;
import com.explodingbacon.bcnlib.framework.Subsystem;
import com.explodingbacon.robot.main.Map;
import edu.wpi.first.wpilibj.Talon;

public class DriveSubsystem extends Subsystem {

    public Motor leftMotor = new Motor(Talon.class, Map.LEFT_DRIVE).setName("Left Drive");
    public Motor rightMotor = new Motor(Talon.class, Map.RIGHT_DRIVE).setName("Right Drive");

    public DriveSubsystem() {
        super();
        leftMotor.setReversed(true);
    }

    public void tankDrive(double l, double r) {
        leftMotor.setPower(l);
        rightMotor.setPower(r);
    }

    public void arcadeDrive(double x, double y) {
        tankDrive(y - x, y + x);
    }

    @Override
    public void stop() {
        leftMotor.setPower(0);
        rightMotor.setPower(0);
    }
}