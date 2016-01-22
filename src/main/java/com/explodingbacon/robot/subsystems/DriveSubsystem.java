package com.explodingbacon.robot.subsystems;

import com.explodingbacon.bcnlib.actuators.Talon;
import com.explodingbacon.bcnlib.framework.Subsystem;
import com.explodingbacon.robot.main.Map;

public class DriveSubsystem extends Subsystem {

    private Talon leftMotor = new Talon(Map.LEFT_DRIVE);
    private Talon rightMotor = new Talon(Map.RIGHT_DRIVE);

    public DriveSubsystem() {
        leftMotor.setReversed(true);
    }

    public void tankDrive(double l, double r) {
        leftMotor.setPower(l);
        rightMotor.setPower(r);
    }

    @Override
    public void stop() {
        leftMotor.setPower(0);
        rightMotor.setPower(0);
    }
}