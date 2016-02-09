package com.explodingbacon.robot.subsystems;

import com.explodingbacon.bcnlib.actuators.Motor;
import com.explodingbacon.bcnlib.actuators.MotorGroup;
import com.explodingbacon.bcnlib.framework.PIDController;
import com.explodingbacon.bcnlib.framework.Subsystem;
import com.explodingbacon.bcnlib.sensors.Encoder;
import com.explodingbacon.bcnlib.sensors.EncoderInterface;
import com.explodingbacon.robot.main.Map;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Talon;

public class ShooterSubsystem extends Subsystem {

    private static Motor shooter = new MotorGroup(Talon.class, Map.SHOOTER_MOTOR_1, Map.SHOOTER_MOTOR_2).setName("Shooter");
    private static Motor outRoller = new Motor(CANTalon.class, Map.SHOOTER_ROLLER).setName("Shooter Roller");

    private static Encoder encoder = new Encoder(Map.SHOOTER_ENCODER_1, Map.SHOOTER_ENCODER_2);
    public static PIDController shooterPID;
    private static double kP = 1; //TODO: tune

    private static boolean shouldShoot = false;

    public ShooterSubsystem() {
        super();
        encoder.setPidMode(EncoderInterface.RATE);
        shooterPID = new PIDController(shooter, encoder, kP, 0, 0);
        outRoller.setReversed(true);
    }

    /**
     * Tells the Shooter to shoot the boulder.
     */
    public static void shoot() {
        setShouldShoot(true);
    }

    /**
     * Waits until the Shooter shoots the boulder.
     */
    public static void waitForShoot() {
        while (!shouldShoot) {
            try {
                Thread.sleep(25);
            } catch (Exception e) {}
        }
    }

    /**
     * Checks if the Shooter has been told to shoot.
     * @return If the Shooter has been told to shoot.
     */
    public static boolean shouldShoot() { return shouldShoot; }

    /**
     * Sets if the Shooter should shoot the boulder.
     * @param b If the Shooter should shoot the boulder.
     */
    public static void setShouldShoot(boolean b) { shouldShoot = b; }

    /**
     * Sets the speed of the Shooter.
     * @param d The speed of the Shooter.
     */
    public static void setShooter(double d) { shooter.setPower(d); }

    /**
     * Sets the speed of the Shooter roller. The roller is used to move the ball into the Shooter wheels.
     * @param d The speed of the Shooter roller.
     */
    public static void setRoller(double d) {
        outRoller.setPower(d);
    }

    /**
     * Gets the Encoder on the Shooter.
     * @return Yhe Encoder on the Shooter.
     */
    public static Encoder getEncoder() {
        return encoder;
    }

    @Override
    public void stop() {
        shooterPID.disable();
        shooter.setPower(0);
        outRoller.setPower(0);
    }
}