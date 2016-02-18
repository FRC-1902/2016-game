package com.explodingbacon.robot.subsystems;

import com.explodingbacon.bcnlib.actuators.DoubleSolenoid;
import com.explodingbacon.bcnlib.actuators.Motor;
import com.explodingbacon.bcnlib.actuators.MotorGroup;
import com.explodingbacon.bcnlib.framework.Subsystem;
import com.explodingbacon.bcnlib.sensors.AbstractEncoder;
import com.explodingbacon.bcnlib.sensors.DigitalInput;
import com.explodingbacon.bcnlib.sensors.Encoder;
import com.explodingbacon.robot.main.Map;
import edu.wpi.first.wpilibj.CANTalon;

public class ClimberSubsystem extends Subsystem {

    private static Motor climber = new MotorGroup(CANTalon.class, Map.CLIMBER_MOTOR_1, Map.CLIMBER_MOTOR_2).setName("Climber");
    private static DoubleSolenoid position = new DoubleSolenoid(Map.CLIMBER_SOLENOID_A, Map.CLIMBER_SOLENOID_B);
    private static AbstractEncoder encoder = new Encoder(Map.CLIMBER_ENCODER_A, Map.CLIMBER_ENCODER_B);
    private static DigitalInput down = new DigitalInput(Map.CLIMBER_DOWN_TOUCH);
    private static DigitalInput up = new DigitalInput(Map.CLIMBER_UP_TOUCH);

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
        if (up.get() && !down.get()) { //If climber is completely up
            //TODO: confirm 500 times over that it is safe to move the motors at this point and then program them to move up
        }
    }

    /**
     * Brings the climber downwards.
     */
    public static void retract() {
        if (up.get() && !down.get()) { //If climber is completely up
            //TODO: move climber motors back down
        }
    }


    @Override
    public void stop() {
        climber.setPower(0);
        //deploy.set(false); TODO: Should we set the solenoid to false or leave it as-is?
    }
}
