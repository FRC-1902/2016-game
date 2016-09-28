package com.explodingbacon.robot.subsystems;

import com.explodingbacon.bcnlib.actuators.*;
import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.bcnlib.framework.PIDController;
import com.explodingbacon.bcnlib.framework.Subsystem;
import com.explodingbacon.bcnlib.sensors.AbstractEncoder;
import com.explodingbacon.bcnlib.sensors.DigitalInput;
import com.explodingbacon.bcnlib.sensors.MotorEncoder;
import com.explodingbacon.robot.main.Map;
import com.explodingbacon.robot.main.OI;
import edu.wpi.first.wpilibj.CANTalon;
import java.util.Arrays;
import java.util.List;

public class Shooter extends Subsystem {

    private static MotorGroup shooter;
    private static Motor indexer;
    private static DigitalInput ballSensor;
    private static boolean hasBall = false;

    private static MotorEncoder encoder;
    public static PIDController shooterPID;

    private static boolean visionShotQueued = false, visionShooting = false;

    public static int LOW_OFFSET = 0;
    public static int HIGH_OFFSET = 0;

    public static final int LOW_GOAL_RATE = -26500 / 2;
    public static final int HIGH_GOAL_RATE = -111000;//-55000 / 2; //Used to be 50000



    public Shooter() {
        super();

        shooter = (MotorGroup) new MotorGroup(CANTalon.class, Map.SHOOTER_MOTOR_1, Map.SHOOTER_MOTOR_2).setName("Shooter");
        shooter.setInverts(true, false);

        indexer = new Motor(CANTalon.class, Map.SHOOTER_INDEXER).setName("Shooter Indexer");
        indexer.setStopOnNoUser();
        indexer.setReversed(true);

        encoder = shooter.getMotors().get(1).getEncoder();
        if (encoder == null) Log.e("Shooter encoder is null");
        encoder.setPIDMode(AbstractEncoder.PIDMode.RATE);
        encoder.setReversed(false);

        shooterPID = new PIDController(shooter, encoder, 0.00002 / 2, 0.00000085 / 1, 0.00001 / 3, 0.1, 1); //0.00002, 0.0000008, 0.00001
        shooterPID.setFinishedTolerance(800); //formerly 1500
        shooterPID.setInputInverted(false);

        shooter.onNoUser(() -> shooterPID.setTarget(0));

        ballSensor = new DigitalInput(Map.SHOOTER_BALL_SENSOR);
    }

    @Override
    public void enabledInit() {
        Shooter.shooterPID.enable();
    }

    @Override
    public void disabledInit() {
        Shooter.shooterPID.setTarget(0);
    }

    /**
     * Revs up the Shooter.
     *
     * @param c The Command calling this function.
     */
    public static void rev(Command c) {
        rev(c, calculateRate());
    }

    /**
     * Revs up the Shooter to a certain speed.
     *
     * @param c The Command calling this function.
     * @param speed The speed the shooter should rev up to.
     */
    public static void rev(Command c, double speed) {
        if (shooter.claim(c)) {
                shooterPID.setTarget(speed);

                shooterPID.setExtraCode(() -> {
                    if (shooterPID.isDone()) {
                        OI.manip.rumble(0.2f, 0.2f);
                    } else {
                        OI.manip.rumble(0, 0);
                    }
                });
        }
    }

    /**
     * Makes the Thread wait until the shooter is up to speed.
     */
    public static void waitForRev() {
        shooterPID.waitUntilDone();
    }

    /**
     * Stops revving the Shooter.
     */
    public static void stopRev(Command c) {
        if (shooter.isUsableBy(c)) {
            //setSpotlight(false);
            shooterPID.setTarget(0);
            shooterPID.setExtraCode(null);

            shooter.setUser(null);
        }
    }

    /**
     * Calculates the rate needed to shoot the current ball.
     *
     * @return The rate needed to shoot the current ball.
     */
    public static double calculateRate() {
        if (OI.shooterRevLow.get()) return LOW_GOAL_RATE + LOW_OFFSET;
        return HIGH_GOAL_RATE + HIGH_OFFSET;
    }

    /**
     * Checks if the Robot has a Vision Shot queued.
     *
     * @return If the Robot has a Vision Shot queued.
     */
    public static boolean isVisionShotQueued() {
        return visionShotQueued;
    }

    /**
     * Sets if the Robot should be currently trying to shoot using Vision Targeting.
     *
     * @param b If the Robot should be currently trying to shoot using Vision Targeting.
     */
    public static void setVisionShotQueued(boolean b) {
        visionShotQueued = b;
    }

    /**
     * Checks if the Robot is in the middle of Vision Shooting.
     *
     * @return If the Robot is in the middle of Vision Shooting.
     */
    public static boolean isVisionShooting() {
        return visionShooting;
    }

    /**
     * Sets if the Robot is in the middle of Vision Shooting.
     *
     * @param b If the Robot is in the middle of Vision Shooting.
     */
    public static void setVisionShooting(boolean b) {
        visionShooting = b;
    }

    public static DigitalInput getBallSensor() {
        return ballSensor;
    }

    public static void setHasBall(boolean b) {
        hasBall = b;
    }

    /**
     * Checks if the Shooter has a ball in it.
     *
     * @return If the Shooter has a ball in it.
     */
    public static boolean hasBall() {
        return hasBall;
    }

    /**
     * Uses the indexer to push the ball into the shooter wheels, causing the ball to be shot.
     *
     * @param c The Command that is doing this action.
     */
    public static void shootUsingIndexer(Command c) {
        if (indexer.claim(c)) {
            setIndexerRaw(1);
            Log.i("Shot at " + calculateRate());
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
            }
            indexer.setUser(null);
        }
    }

    /**
     * Sets the speed of the Shooter indexer. The indexer is used to move the ball into the Shooter wheels.
     *
     * @param d The speed of the Shooter indexer.
     */
    public static void setIndexerRaw(double d) {
        indexer.setPower(d);
    }

    /**
     * Gets the MotorGroup for the shooter Motors.
     *
     * @return The MotorGroup for the shooter Motors.
     */
    public static MotorGroup getShooter() {
        return shooter;
    }

    /**
     * Gets the indexer Motor.
     *
     * @return The indexer Motor.
     */
    public static Motor getIndexer() {
        return indexer;
    }

    /**
     * Gets the Encoder on the Shooter.
     *
     * @return Yhe Encoder on the Shooter.
     */
    public static MotorEncoder getEncoder() {
        return encoder;
    }


    @Override
    public void stop() {
        //shooterPID.disable();
        shooter.setPower(0);
        indexer.setPower(0);
    }

    @Override
    public List<Motor> getAllMotors() {
        return Arrays.asList(shooter, indexer);
    }
}