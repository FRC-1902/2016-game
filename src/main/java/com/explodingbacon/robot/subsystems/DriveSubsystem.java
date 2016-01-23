package com.explodingbacon.robot.subsystems;

import com.explodingbacon.bcnlib.actuators.Motor;
import com.explodingbacon.bcnlib.framework.Subsystem;
import com.explodingbacon.robot.main.Map;
import edu.wpi.first.wpilibj.Talon;

public class DriveSubsystem extends Subsystem {

    private Motor leftMotor = new Motor(Talon.class, Map.LEFT_DRIVE);
    private Motor rightMotor = new Motor(Talon.class, Map.RIGHT_DRIVE);

    public DriveSubsystem() {
        super();
        leftMotor.setReversed(true);
    }

    public void tankDrive(double l, double r) {
        leftMotor.setPower(l);
        rightMotor.setPower(r);
    }

    public void arcadeDrive(double joyX, double joyY) {
        if (Math.abs(joyX) < 0.05) {
            joyX = 0;
        }
        if (Math.abs(joyY) < 0.05) {
            joyY = 0;
        }
        tankDrive(joyY - joyX, joyY + joyX);
    }

    @Override
    public void stop() {
        leftMotor.setPower(0);
        rightMotor.setPower(0);
    }
}