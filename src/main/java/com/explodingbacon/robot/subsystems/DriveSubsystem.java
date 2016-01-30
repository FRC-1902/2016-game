package com.explodingbacon.robot.subsystems;

import com.explodingbacon.bcnlib.actuators.Motor;
import com.explodingbacon.bcnlib.framework.Subsystem;
import com.explodingbacon.bcnlib.utils.Drive;
import com.explodingbacon.robot.main.Map;
import edu.wpi.first.wpilibj.Talon;

public class DriveSubsystem extends Subsystem {

    private Motor leftMotor = new Motor(Talon.class, Map.LEFT_DRIVE).setName("Left Drive");
    private Motor rightMotor = new Motor(Talon.class, Map.RIGHT_DRIVE).setName("Right Drive");

    public DriveSubsystem() {
        super();
        leftMotor.setReversed(true);
    }

    public void configureDrive() {
        Drive.configure(leftMotor, rightMotor, Drive.DriveType.ARCADE);
    }

    @Override
    public void stop() {
        leftMotor.setPower(0);
        rightMotor.setPower(0);
    }
}