package com.explodingbacon.robot.subsystems;

import com.explodingbacon.bcnlib.actuators.DoubleSolenoid;
import com.explodingbacon.bcnlib.actuators.Motor;
import com.explodingbacon.bcnlib.actuators.MotorGroup;
import com.explodingbacon.bcnlib.actuators.Solenoid;
import com.explodingbacon.bcnlib.framework.Subsystem;
import com.explodingbacon.bcnlib.sensors.MotorEncoder;
import com.explodingbacon.robot.main.Map;
import edu.wpi.first.wpilibj.CANTalon;

import java.util.Collections;
import java.util.List;

public class Climber extends Subsystem {

    private static MotorGroup cableWinch;
    private static MotorEncoder encoder;
    private static DoubleSolenoid position;
    private static Solenoid shoot;

    public Climber() {
        super();
        cableWinch = (MotorGroup) new MotorGroup(CANTalon.class, Map.CLIMBER_CABLE_WINCH_A, Map.CLIMBER_CABLE_WINCH_B).setName("Climber Winch");

        encoder = cableWinch.getMotors().get(1).getEncoder();
        position = new DoubleSolenoid(Map.CLIMBER_POSITION_A, Map.CLIMBER_POSITION_B);
        shoot = new Solenoid(Map.CLIMBER_SHOOT);
    }

    @Override
    public void enabledInit() {}

    @Override
    public void disabledInit() {}

    /**
     * Sets the position of the Climber.
     *
     * @param b True = up, false = down.
     */
    public static void setPosition(boolean b) {
        position.set(b);
    }

    /**
     * Shoots the Climber.
     */
    public static void shoot() {
        shoot.set(true);
    }

    public static void setSpeed(double d) {
        cableWinch.setPower(d);
    }

    public static MotorEncoder getEncoder() {
        return encoder;
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
