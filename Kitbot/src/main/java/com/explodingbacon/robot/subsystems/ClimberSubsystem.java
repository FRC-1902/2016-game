package com.explodingbacon.robot.subsystems;

import com.explodingbacon.bcnlib.actuators.Motor;
import com.explodingbacon.bcnlib.framework.Subsystem;
import com.explodingbacon.bcnlib.utils.MotorGroup;
import com.explodingbacon.robot.main.Map;
import edu.wpi.first.wpilibj.Talon;

public class ClimberSubsystem extends Subsystem {

    public Motor climber = new MotorGroup(Talon.class, Map.CLIMBER_MOTOR_1, Map.CLIMBER_MOTOR_2).setName("Climber");

    @Override
    public void stop() {
        climber.setPower(0);
    }
}
