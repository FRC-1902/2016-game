package com.explodingbacon.robot.subsystems;

import com.explodingbacon.bcnlib.actuators.Talon;
import com.explodingbacon.bcnlib.framework.Subsystem;;
import com.explodingbacon.bcnlib.utils.MotorGroup;
import com.explodingbacon.robot.main.Map;

public class ShooterSubsystem extends Subsystem {

    private MotorGroup shooter = new MotorGroup(Talon.class, Map.SHOOTER_MOTOR_1, Map.SHOOTER_MOTOR_2);
    private Talon outRoller = new Talon(Map.SHOOTER_ROLLER);

    public void setShooterPower(double d) {
        shooter.setPower(d);
    }

    public void setRoller(double d) { outRoller.setPower(d); }

    @Override
    public void stop() {
        shooter.setPower(0);
        outRoller.setPower(0);
    }
}