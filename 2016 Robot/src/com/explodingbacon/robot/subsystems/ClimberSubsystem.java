package com.explodingbacon.robot.subsystems;

import com.explodingbacon.bcnlib.actuators.DoubleSolenoid;
import com.explodingbacon.bcnlib.actuators.MotorGroup;
import com.explodingbacon.bcnlib.framework.Subsystem;
import com.explodingbacon.bcnlib.sensors.DigitalInput;
import com.explodingbacon.bcnlib.sensors.MotorEncoder;
import com.explodingbacon.robot.main.Map;
import edu.wpi.first.wpilibj.CANTalon;

public class ClimberSubsystem extends Subsystem { //top is position 33,000, approximately

    private static MotorGroup climber = (MotorGroup) new MotorGroup(CANTalon.class, Map.CLIMBER_MOTOR_1, Map.CLIMBER_MOTOR_2).setName("Climber");
    private static DoubleSolenoid position = new DoubleSolenoid(Map.CLIMBER_SOLENOID_A, Map.CLIMBER_SOLENOID_B);
    private static MotorEncoder encoder;
    private static DigitalInput down = new DigitalInput(Map.CLIMBER_DOWN_TOUCH);

    public ClimberSubsystem() {
        super();
        encoder = climber.getMotors().get(1).getEncoder();
        encoder.setReversed(true);

        position.set(false);
    }

    /**
     * Sets the position of the Climber.
     */
    public static void setPosition(boolean pos) {
        //position.set(pos);
    }

    /**
     * Sends the climber upwards.
     */
    public static void deploy() {
        //TODO: implement
    }

    /**
     * Brings the climber downwards.
     */
    public static void retract() {
        //TODO: implement
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
    public static MotorGroup getClimberMotors() {
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
}
