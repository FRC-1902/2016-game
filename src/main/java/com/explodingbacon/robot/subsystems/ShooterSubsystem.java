package com.explodingbacon.robot.subsystems;

import com.explodingbacon.bcnlib.actuators.CANTalon;
import com.explodingbacon.bcnlib.actuators.Talon;
import com.explodingbacon.bcnlib.framework.Subsystem;;
import com.explodingbacon.robot.main.Map;

public class ShooterSubsystem extends Subsystem {

    public Talon shooter = new Talon(Map.SHOOTER_MOTOR);
    private CANTalon outRoller = new CANTalon(Map.SHOOTER_ROLLER);

    public ShooterSubsystem() {
        outRoller.setReversed(true);
    }

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