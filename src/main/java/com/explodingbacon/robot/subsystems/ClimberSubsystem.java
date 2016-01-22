package com.explodingbacon.robot.subsystems;

import com.explodingbacon.bcnlib.actuators.Talon;
import com.explodingbacon.bcnlib.framework.Subsystem;
import com.explodingbacon.bcnlib.utils.MotorGroup;
import com.explodingbacon.robot.main.Map;

public class ClimberSubsystem extends Subsystem {

    public MotorGroup climber = new MotorGroup(Talon.class, Map.CLIMBER_MOTOR_1, Map.CLIMBER_MOTOR_2);

    @Override
    public void stop() {
        climber.setPower(0);
    }
}
