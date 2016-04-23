package com.explodingbacon.robot.subsystems;

import com.explodingbacon.bcnlib.actuators.*;
import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.bcnlib.framework.PIDController;
import com.explodingbacon.bcnlib.framework.Subsystem;
import com.explodingbacon.bcnlib.sensors.AbstractEncoder;
import com.explodingbacon.bcnlib.sensors.DigitalInput;
import com.explodingbacon.bcnlib.sensors.MotorEncoder;
import com.explodingbacon.robot.main.KitMap;
import com.explodingbacon.robot.main.Map;
import com.explodingbacon.robot.main.OI;
import com.explodingbacon.robot.main.Robot;
import com.explodingbacon.robot.vision.VisionTargeting;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.TalonSRX;

import java.util.Arrays;
import java.util.List;

public class Shooter extends Subsystem {

    private static MotorGroup shooter;
    private static Motor indexer;
    private static DigitalInput hasBall = null;

    private static MotorEncoder encoder;
    public static PIDController shooterPID;

    public static final int INTAKE_RATE = -25000;

    public static final int LOW_GOAL_RATE = 26500;
    public static final int HIGH_GOAL_RATE = 55000; //Used to be 50000

    public static int LOW_OFFSET = 0;
    public static int HIGH_OFFSET = 0;

    private static boolean doingVisionShoot = false;

    public Shooter() {
        super();

        if (Robot.real) {
            shooter = (MotorGroup) new MotorGroup(CANTalon.class, Map.SHOOTER_MOTOR_1, Map.SHOOTER_MOTOR_2).setName("Shooter");
        } else {
            shooter = (MotorGroup) new MotorGroup(TalonSRX.class, KitMap.WINCH_A, KitMap.WINCH_B).setName("Shooter");
        }

        if (Robot.real) {
            indexer = new Motor(CANTalon.class, Map.SHOOTER_INDEXER).setName("Shooter Indexer");
        } else {
            indexer = new Motor(Talon.class, KitMap.INDEXER);
        }

        indexer.setStopOnNoUser();
        indexer.setReversed(true);

        if (Robot.real) {
            encoder = shooter.getMotors().get(1).getEncoder();
            encoder.setPIDMode(AbstractEncoder.PIDMode.RATE);
            encoder.setReversed(true);

            shooterPID = new PIDController(shooter, encoder, 0.00002, 0.00000085, 0.00001, 0.1, 0.9); //0.00002, 0.0000008, 0.00001
            shooterPID.setFinishedTolerance(1500);
            shooterPID.setInputInverted(false); //Changed from true

            shooter.onNoUser(() -> shooterPID.setTarget(0));

            hasBall = new DigitalInput(Map.SHOOTER_BALL_TOUCH);
        } else {
            shooter.onNoUser(() -> shooter.setPower(0));
        }
    }

    @Override
    public void enabledInit() {
        if (Robot.real) {
            Shooter.shooterPID.enable();
        }
    }

    @Override
    public void disabledInit() {
        if (Robot.real) {
            Shooter.shooterPID.setTarget(0);
        } else {
            shooter.setPower(0);
        }
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
            if (Robot.real) {
                shooterPID.setTarget(speed);

                shooterPID.setExtraCode(() -> {
                    if (shooterPID.isDone()) {
                        OI.manip.rumble(0.1f, 0.1f);
                    } else {
                        OI.manip.rumble(0, 0);
                    }
                });
            } else {
                shooter.setPower(.7);
            }
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

            if (Robot.real) {
                shooterPID.setTarget(0);
                shooterPID.setExtraCode(null);
            } else {
                shooter.setPower(0);
            }

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

    private static final double slowTurnSpeed = 0.3;

    /**
     * Uses Vision Targeting to make the Robot turn and shoot a high goal.
     *
     * @param c The Command asking for this action to be performed.
     */
    public static void doVisionShoot(Command c) {
        doingVisionShoot = true;
        boolean didLeft = false;
        boolean didRight = false;
        if (VisionTargeting.isGoalVisible()) {
            boolean abort = false;
            while (!VisionTargeting.isLinedUp()) { //TODO: see if we can get away with being lazy and just doing motor speeds instead of using a rate PID
                if (VisionTargeting.shouldGoLeft()) {
                    didLeft = true;
                    Drive.tankDrive(slowTurnSpeed, -slowTurnSpeed); //Turn left
                } else if (VisionTargeting.shouldGoRight()) {
                    didRight = true;
                    Drive.tankDrive(-slowTurnSpeed, slowTurnSpeed); //Turn right
                } else {
                    Log.e("Shooter.doVisionShoot() made it to an else statement that should not happen!");
                }
                if (didLeft && didRight) {
                    Log.w("Drive train oscillating when doing Vision Shoot!");
                    didLeft = false;
                    didRight = false;
                }
                if (!VisionTargeting.isGoalVisible()) {
                    abort = true;
                    break;
                }
            }
            if (!abort) {
                Drive.tankDrive(0, 0);
                waitForRev(); //Should not wait at all if already at target
                shootUsingIndexer(c);
            } else {
                Log.v("Vision Shoot aborted!");
            }
        } else {
            Log.i("Did not do vision shoot due to not being able to see the goal!");
            //TODO: make the robot turn and look for the goal based off it's defense position instead of giving up
        }
        doingVisionShoot = false;
    }

    /**
     * Checks if the Robot is currently shooting using Vision Targeting.
     *
     * @return If the Robot is currently shooting using Vision Targeting.
     */
    public static boolean isVisionShooting() {
        return doingVisionShoot;
    }

    /**
     * Checks if the Shooter has a ball in it.
     *
     * @return If the Shooter has a ball in it.
     */
    public static boolean hasBall() {
        if (Robot.real) {
            return hasBall.get();
        } else {
            return false;
        }
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