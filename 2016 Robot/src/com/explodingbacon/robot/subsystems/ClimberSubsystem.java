package com.explodingbacon.robot.subsystems;

import com.explodingbacon.bcnlib.actuators.DoubleSolenoid;
import com.explodingbacon.bcnlib.actuators.Motor;
import com.explodingbacon.bcnlib.actuators.MotorGroup;
import com.explodingbacon.bcnlib.actuators.Solenoid;
import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.bcnlib.framework.PIDController;
import com.explodingbacon.bcnlib.framework.Subsystem;
import com.explodingbacon.bcnlib.sensors.AbstractEncoder;
import com.explodingbacon.bcnlib.sensors.DigitalInput;
import com.explodingbacon.bcnlib.sensors.MotorEncoder;
import com.explodingbacon.robot.main.Map;
import edu.wpi.first.wpilibj.CANTalon;

import java.util.Arrays;
import java.util.List;

public class ClimberSubsystem extends Subsystem {

    public static MotorGroup climber = (MotorGroup) new MotorGroup(CANTalon.class, Map.CLIMBER_MOTOR_1, Map.CLIMBER_MOTOR_2).setName("Climber");
    private static DoubleSolenoid position = new DoubleSolenoid(Map.CLIMBER_SOLENOID_A, Map.CLIMBER_SOLENOID_B);
    public static Solenoid latches = new Solenoid(Map.CLIMBER_LATCH);
    private static MotorEncoder encoder;

    private static PIDController pid;

    private static DigitalInput down = new DigitalInput(Map.CLIMBER_DOWN_TOUCH);

    private static DigitalInput retracted = new DigitalInput(Map.CLIMBER_SWITCH_RETRACTED);
    public static DigitalInput deployed = new DigitalInput(Map.CLIMBER_SWITCH_DEPLOYED);

    private static final int TOP_POS = 33875; //TODO: finalize
    private static final int CLIMB_POS = 27061;
    private static final int LATCH_POS = 1574; //TODO: finalize
    private static final int BOTTOM_POS = 0;

    private static boolean moveMotors = false;

    public ClimberSubsystem() {
        super();
        encoder = climber.getMotors().get(1).getEncoder();
        encoder.setReversed(true);
        encoder.reset();

        pid = new PIDController(climber, encoder, 0.1, 0, 0, 0.1, 0.3);
        pid.setTarget(BOTTOM_POS);
        //pid.enable();

        position.set(false);
    }

    @Override
    public void enabledInit() {}

    @Override
    public void disabledInit() {}

    /**
     * Sets the position of the Climber.
     */
    public static void setPosition(boolean pos) {
        position.set(pos);
    }

    public static void setLatches(boolean pos) {
        latches.set(pos);
    }

    /**
     * Sends the climber upwards.
     */
    public static void deploy() {
        if (deployed.get() && !retracted.get()) {
            Log.d("Deploying climber");
            if (moveMotors) {
                pid.setTarget(TOP_POS);
            }
        } else {
            Log.d("Tried deploying climber, but solenoids were not in the proper place");
        }
    }

    /**
     * Brings the climber downwards.
     */
    public static void retract() {
        if (deployed.get() && !retracted.get()) {
            Log.d("Retracting climber");
            if (moveMotors) {
                pid.setTarget(BOTTOM_POS);
                while (!pid.isDone()) {
                    if (pid.getCurrentSourceValue() < LATCH_POS) {
                        latches.set(false);
                    }
                    try {
                        Thread.sleep(25);
                    } catch (Exception e) {}
                }
            }
        } else {
            Log.d("Tried retracting climber, but solenoids were not in the proper place");
        }
    }

    /**
     * Checks if the touch sensor is pressed down.
     * @return If the touch sensor is pressed down.
     */
    public static boolean isTouchDown() {
        return down.get();
    }

    /**
     * Gets the MotorGroup for the climber motors.
     * @return The MotorGroup for the climber motors.
     */
    public static MotorGroup getClimber() {
        return climber;
    }

    /**
     * Gets the climber's MotorEncoder.
     * @return The climber's MotorEncoder.
     */
    public static MotorEncoder getEncoder() {
        return encoder;
    }

    @Override
    public void stop() {
        climber.setPower(0);
        //deploy.set(false); TODO: Should we set the solenoid to false or leave it as-is?
    }

    @Override
    public List<Motor> getAllMotors() {
        return Arrays.asList(climber);
    }
}
