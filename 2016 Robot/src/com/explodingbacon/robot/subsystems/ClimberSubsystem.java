package com.explodingbacon.robot.subsystems;

import com.explodingbacon.bcnlib.actuators.*;
import com.explodingbacon.bcnlib.framework.Subsystem;
import com.explodingbacon.robot.main.Map;
import edu.wpi.first.wpilibj.CANTalon;
import java.util.Collections;
import java.util.List;

public class ClimberSubsystem extends Subsystem {

    private static SolenoidInterface bottom = new DoubleSolenoid(Map.MANTIS_1_A, Map.MANTIS_1_B);
    private static SolenoidInterface top = new Solenoid(Map.MANTIS_2);
    private static MotorGroup cableWinch = (MotorGroup) new MotorGroup(CANTalon.class, Map.CLIMBER_CABLE_WINCH_A, Map.CLIMBER_CABLE_WINCH_B).setName("Climber Winch");

    @Override
    public void enabledInit() {}

    @Override
    public void disabledInit() {}

    /**
     * Sets the climber into transport mode. Used for: Driving around, Sally port.
     */
    public static void transportMode() {
        setStates(true, false, false);
    }

    /**
     * Sets the climber into up mode. Used for: Drawbridge.
     */
    public static void upMode() {
        setStates(true, true, false);
    }

    /**
     * Sets the climber into hook mode. Used for: Getting the climber hooks onto the bar on the castle.
     */
    public static void hookMode() {
        setStates(true, true, true);
    }

    /**
     * Sets the climber into down mode. Used for: Going under the Low Bar.
     */
    public static void downMode() {
        setStates(false, false, false);
    }

    /**
     * Sets the climber into climbing mode. Used for: Pulling the robot up the castle.
     */
    public static void climb() {
        setStates(false, false, true);
        //cableWinch.setPower(1); //TODO: check this
    }

    /**
     * Sets the states of the bottom solenoid, top solenoid, and hooks.
     *
     * @param b The state of the bottom solenoid.
     * @param t The state of the top solenoid.
     * @param h The state of the hooks.
     */
    private static void setStates(boolean b, boolean t, boolean h) {
        bottom.set(b);
        top.set(t);
        setHooks(h);
    }

    /**
     * Sets the state of the hooks.
     *
     * @param b The state of the hooks.
     */
    private static void setHooks(boolean b) {
        //TODO: actually set the hooks
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
