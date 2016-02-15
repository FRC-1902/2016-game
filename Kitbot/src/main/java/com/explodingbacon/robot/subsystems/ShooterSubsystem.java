package com.explodingbacon.robot.subsystems;

import com.explodingbacon.bcnlib.actuators.Motor;
import com.explodingbacon.bcnlib.framework.Subsystem;;
import com.explodingbacon.robot.main.Map;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Talon;

public class ShooterSubsystem extends Subsystem {

    public Motor shooter = new Motor(Talon.class, Map.SHOOTER_MOTOR).setName("Shooter");
    private Motor outRoller = new Motor(CANTalon.class, Map.SHOOTER_ROLLER).setName("Shooter Roller");

    public ShooterSubsystem() {
        super();
        outRoller.setReversed(true);
    }

    public void setShooterPower(double d) {
        shooter.setPower(d);
    }

    public void setRoller(double d) {
        outRoller.setPower(d);
    }

    @Override
    public void stop() {
        shooter.setPower(0);
        outRoller.setPower(0);
    }
}