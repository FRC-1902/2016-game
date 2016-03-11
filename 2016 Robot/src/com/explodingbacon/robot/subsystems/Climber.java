package com.explodingbacon.robot.subsystems;

import com.explodingbacon.bcnlib.actuators.Motor;
import com.explodingbacon.bcnlib.actuators.MotorGroup;
import com.explodingbacon.bcnlib.actuators.Solenoid;
import com.explodingbacon.bcnlib.framework.Subsystem;
import com.explodingbacon.bcnlib.sensors.MotorEncoder;
import com.explodingbacon.bcnlib.utils.Utils;
import com.explodingbacon.robot.main.Map;
import edu.wpi.first.wpilibj.CANTalon;
import java.util.Collections;
import java.util.List;

public class Climber extends Subsystem {

    private static MotorGroup cableWinch = (MotorGroup) new MotorGroup(CANTalon.class, Map.CLIMBER_CABLE_WINCH_A, Map.CLIMBER_CABLE_WINCH_B).setName("Climber Winch");
    private static MotorEncoder encoder;
    private static Solenoid deploy = new Solenoid(Map.CLIMBER_DEPLOY);

    private static final int STOP_CLIMBING_POSITION = 9001; //TODO: tune

    public Climber() {
        encoder = cableWinch.getMotors().get(1).getEncoder();
    }

    @Override
    public void enabledInit() {}

    @Override
    public void disabledInit() {}

    /**
     * Deploys the Climber.
     */
    public static void deploy() {
        deploy.set(true);
    }

    /**
     * Makes the Robot climb the tower.
     */
    public static void climb() {
        encoder.reset();
        cableWinch.setPower(0.5);
        Utils.waitFor(() -> encoder.get() > STOP_CLIMBING_POSITION);
        cableWinch.setPower(0);
    }

    @Override
    public void stop() {
        cableWinch.setPower(0);
    }

    @Override
    public List<Motor> getAllMotors() {
        return Collections.singletonList(cableWinch);
    }
}
