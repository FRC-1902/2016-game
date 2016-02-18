package com.explodingbacon.robot.subsystems;

import com.explodingbacon.bcnlib.actuators.Light;
import com.explodingbacon.bcnlib.actuators.Motor;
import com.explodingbacon.bcnlib.actuators.MotorGroup;
import com.explodingbacon.bcnlib.actuators.Solenoid;
import com.explodingbacon.bcnlib.framework.PIDController;
import com.explodingbacon.bcnlib.framework.Subsystem;
import com.explodingbacon.bcnlib.sensors.AbstractEncoder;
import com.explodingbacon.bcnlib.sensors.DigitalInput;
import com.explodingbacon.robot.main.Map;
import edu.wpi.first.wpilibj.CANTalon;

public class ShooterSubsystem extends Subsystem {

    public static Motor shooter = new MotorGroup(CANTalon.class, Map.SHOOTER_MOTOR_1, Map.SHOOTER_MOTOR_2).setName("Shooter");
    private static Motor indexer = new Motor(CANTalon.class, Map.SHOOTER_INDEXER).setName("Shooter Indexer");

    private static Light light = new Light(new Solenoid(Map.LIGHT));

    private static AbstractEncoder encoder;
    public static PIDController shooterPID;

    public static final int INTAKE_RATE = 10000;
    public static final int DEFAULT_SHOOT_RATE = -38000; //TODO: Tune

    private static DigitalInput hasBall = new DigitalInput(Map.SHOOTER_BALL_TOUCH);

    private static boolean shouldShoot = false;

    public ShooterSubsystem() {
        super();

        encoder = ((MotorGroup)shooter).getMotors().get(0).getEncoder();

        encoder.setPIDMode(AbstractEncoder.PIDMode.RATE);
        shooterPID = new PIDController(shooter, encoder, 0.00016, 0, 0, 0.3, 0.9);
        indexer.setReversed(true);

        shooter.setFiltered(100); //TODO: Remove?
        shooterPID.enable();
        shooterPID.setInverted(true);
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
    private static void setShooter(double d) {
        shooter.setPower(d);
    }

    /**
     * Checks if the Shooter has a ball in it.
     * @return If the Shooter has a ball in it.
     */
    public static boolean hasBall() {
        return hasBall.get();
    }

    /**
     * Calculates the spin rate required to launch a boulder into a goal that is "distance" away.
     * @param distance How far away the goal is.
     * @return The spin rate required to launch a boulder into a goal that is "distance" away.
     */
    public static double calculateShooterRate(double distance) {
        return DEFAULT_SHOOT_RATE; //TODO: Actually calculate the needed shooter rate
    }

    /**
     * Checks if the current rate of the shooter is acceptable for shooting.
     * @return If the current rate of the shooter is acceptable for shooting.
     */
    public static boolean isRateAcceptable() {
        return encoder.getRate() > (shooterPID.getTarget() * 0.95); //TODO: tune
    }

    /**
     * Sets the speed of the Shooter indexer. The indexer is used to move the ball into the Shooter wheels.
     * @param d The speed of the Shooter indexer.
     */
    public static void setIndexer(double d) {
        indexer.setPower(d);
    }

    /**
     * Gets the Encoder on the Shooter.
     * @return Yhe Encoder on the Shooter.
     */
    public static AbstractEncoder getEncoder() {
        return encoder;
    }

    /**
     * Gets the indicator light.
     * @return The indicator light.
     */
    public static Light getLight() {
        return light;
    }

    @Override
    public void stop() {
        shooterPID.disable();
        shooter.setPower(0);
        indexer.setPower(0);
    }
}