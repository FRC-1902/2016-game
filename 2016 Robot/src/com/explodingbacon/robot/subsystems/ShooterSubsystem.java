package com.explodingbacon.robot.subsystems;

import com.explodingbacon.bcnlib.actuators.*;
import com.explodingbacon.bcnlib.framework.PIDController;
import com.explodingbacon.bcnlib.framework.Subsystem;
import com.explodingbacon.bcnlib.sensors.AbstractEncoder;
import com.explodingbacon.bcnlib.sensors.DigitalInput;
import com.explodingbacon.bcnlib.sensors.MotorEncoder;
import com.explodingbacon.robot.main.Map;
import edu.wpi.first.wpilibj.CANTalon;

public class ShooterSubsystem extends Subsystem {

    private static MotorGroup shooter = (MotorGroup) new MotorGroup(CANTalon.class, Map.SHOOTER_MOTOR_1, Map.SHOOTER_MOTOR_2).setName("Shooter");
    private static Motor indexer = new Motor(CANTalon.class, Map.SHOOTER_INDEXER).setName("Shooter Indexer");

    private static Light light = new Light(new Solenoid(Map.LIGHT));
    private static Motor spotlight = new Motor(Relay.class, Map.SPOTLIGHT);

    private static MotorEncoder encoder;
    public static PIDController shooterPID;

    public static final int INTAKE_RATE = -25000;
    public static final int DEFAULT_SHOOT_RATE = 48000; //TODO: Tune

    private static DigitalInput hasBall = new DigitalInput(Map.SHOOTER_BALL_TOUCH);

    private static boolean shouldShoot = false;

    private static double currentBallWatts = -1;

    public ShooterSubsystem() {
        super();

        indexer.setReversed(true);
        shooter.setFiltered(100); //TODO: Remove?

        encoder = shooter.getMotors().get(1).getEncoder();
        encoder.setPIDMode(AbstractEncoder.PIDMode.RATE);
        encoder.setReversed(true);

        shooterPID = new PIDController(shooter, encoder, 0.0001, 0.0002, 0.0005, 0.3, 0.9); //i=.0002 d=.001
        shooterPID.setInverted(false); //Changed from true
        shooterPID.enable();
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
    public static void setShooter(double d) {
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
     * Gets the watts needed to move the current ball.
     * @return The watts needed to move the current ball.
     */
    public static double getBallWatts() {
        return currentBallWatts;
    }

    /**
     * Saves the watts needed to move the current ball, which is later used for shooting that ball.
     * @param w The watts needed to move the current ball, which is later used for shooting that ball.
     */
    public static void setBallWatts(double w) {
        currentBallWatts = w;
    }

    /**
     * Calculates the rate needed to shoot the current ball based off the previously recorded watts needed to move the ball.
     * @return The rate needed to shoot the current ball based off the previously recorded watts needed to move the ball.
     */
    public static double calculateRateFromWatts() {
        if (currentBallWatts == -1 || !IntakeSubsystem.TEST_BALLS) {
            return DEFAULT_SHOOT_RATE;
        } else {
            return DEFAULT_SHOOT_RATE; //TODO: formula for watts -> rate
        }
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
     * Gets the MotorGroup for the shooter Motors.
     * @return The MotorGroup for the shooter Motors.
     */
    public static MotorGroup getShooter() {
        return shooter;
    }

    /**
     * Gets the indexer Motor.
     * @return The indexer Motor.
     */
    public static Motor getIndexer() {
        return indexer;
    }

    /**
     * Gets the Encoder on the Shooter.
     * @return Yhe Encoder on the Shooter.
     */
    public static MotorEncoder getEncoder() {
        return encoder;
    }

    /**
     * Gets the indicator light.
     * @return The indicator light.
     */
    public static Light getLight() {
        return light;
    }

    /**
     * Sets the status of the spotlight.
     * @param b The status of the spotlight.
     */
    public static void setSpotlight(boolean b) {
        spotlight.setPower(b ? 1 : 0);
    }

    @Override
    public void stop() {
        //shooterPID.disable();
        shooter.setPower(0);
        indexer.setPower(0);
    }
}