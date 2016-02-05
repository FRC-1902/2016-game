package com.explodingbacon.robot.subsystems;

import com.explodingbacon.bcnlib.actuators.Motor;
import com.explodingbacon.bcnlib.actuators.MotorGroup;
import com.explodingbacon.bcnlib.framework.InternalSource;
import com.explodingbacon.bcnlib.framework.PIDController;
import com.explodingbacon.bcnlib.framework.Subsystem;;
import com.explodingbacon.robot.main.Map;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Talon;

public class ShooterSubsystem extends Subsystem {

    private static Motor shooter = new MotorGroup(Talon.class, Map.SHOOTER_MOTOR_1, Map.SHOOTER_MOTOR_2).setName("Shooter");
    private static Motor outRoller = new Motor(CANTalon.class, Map.SHOOTER_ROLLER).setName("Shooter Roller");

    public static InternalSource target = new InternalSource();
    public static PIDController shooterPID = new PIDController(shooter, target, 1, 0, 0);

    private static boolean shouldShoot = false;

    public ShooterSubsystem() {
        super();
        outRoller.setReversed(true);
    }

    public static void shoot() {
        setShouldShoot(true);
    }

    public static boolean shouldShoot() { return shouldShoot; }

    public static void setShouldShoot(boolean b) { shouldShoot = b; }


    public static void setShooter(double d) { shooter.setPower(d); }

    public static void setRoller(double d) {
        outRoller.setPower(d);
    }

    @Override
    public void stop() {
        shooter.setPower(0);
        outRoller.setPower(0);
    }
}