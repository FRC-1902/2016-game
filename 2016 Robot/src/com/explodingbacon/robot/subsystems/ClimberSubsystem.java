package com.explodingbacon.robot.subsystems;

import com.explodingbacon.bcnlib.actuators.Motor;
import com.explodingbacon.bcnlib.actuators.MotorGroup;
import com.explodingbacon.bcnlib.actuators.Solenoid;
import com.explodingbacon.bcnlib.framework.Subsystem;
import com.explodingbacon.bcnlib.sensors.TouchSensor;
import com.explodingbacon.robot.main.Map;
import edu.wpi.first.wpilibj.CANTalon;

public class ClimberSubsystem extends Subsystem {

    public static Motor climber = new MotorGroup(CANTalon.class, Map.CLIMBER_MOTOR_1, Map.CLIMBER_MOTOR_2).setName("Climber");
    private static Solenoid deploy = new Solenoid(Map.CLIMBER_SOLENOID);
    private static TouchSensor down = new TouchSensor(Map.CLIMBER_DOWN_TOUCH);
    private static TouchSensor up = new TouchSensor(Map.CLIMBER_UP_TOUCH);

    /**
     * Deploys the Climber.
     */
    public static void deploy() {
        deploy.set(true);
        while (! (up.get() && !down.get()) ) {
            try {
                Thread.sleep(25);
            } catch (Exception e) {}
        }
        //TODO: write code to deploy climber after confirming that we are safe to deploy at this point
    }


    @Override
    public void stop() {
        climber.setPower(0);
        //deploy.set(false); TODO: Should we set the solenoid to false or leave it as-is?
    }
}
