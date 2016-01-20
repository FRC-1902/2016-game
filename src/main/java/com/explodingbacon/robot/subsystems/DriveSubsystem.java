package com.explodingbacon.robot.subsystems;

import com.explodingbacon.bcnlib.actuators.CANTalon;
import com.explodingbacon.bcnlib.actuators.Talon;
import com.explodingbacon.bcnlib.actuators.TalonSRX;
import com.explodingbacon.bcnlib.framework.Subsystem;
import com.explodingbacon.bcnlib.utils.MotorGroup;
import com.explodingbacon.robot.main.Map;

public class DriveSubsystem extends Subsystem {

    private MotorGroup leftMotors = new MotorGroup(Talon.class, Map.LEFT_DRIVE_1, Map.LEFT_DRIVE_2);
    private MotorGroup rightMotors = new MotorGroup(Talon.class, Map.RIGHT_DRIVE_1, Map.RIGHT_DRIVE_2, Map.RIGHT_DRIVE_3);

    public DriveSubsystem() {
        leftMotors.setReversed(true);
        rightMotors.setReversed(true);
        leftMotors.addMotor(new CANTalon(Map.LEFT_DRIVE_3)) ;
    }

    public void tankDrive(double l, double r) {
        leftMotors.setPower(l);
        rightMotors.setPower(r);
    }

    @Override
    public void stop() {
        leftMotors.setPower(0);
        rightMotors.setPower(0);
    }
}