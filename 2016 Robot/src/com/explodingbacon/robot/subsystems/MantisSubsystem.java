package com.explodingbacon.robot.subsystems;

import com.explodingbacon.bcnlib.actuators.*;
import com.explodingbacon.bcnlib.framework.Subsystem;
import com.explodingbacon.robot.main.Map;

import java.util.ArrayList;
import java.util.List;

public class MantisSubsystem extends Subsystem {

    private static SolenoidInterface bottom = new DoubleSolenoid(Map.MANTIS_1_A, Map.MANTIS_1_B);
    private static SolenoidInterface top = new Solenoid(Map.MANTIS_2);

    @Override
    public void enabledInit() {}

    @Override
    public void disabledInit() {}

    /**
     * Sets the climber into transport mode. Used for: Driving around, Sally port.
     */
    public static void transportMode() {
        setStates(true, false);
    }

    /**
     * Sets the climber into up mode. Used for: Drawbridge.
     */
    public static void upMode() {
        setStates(true, true);
    }


    /**
     * Sets the climber into down mode. Used for: Going under the Low Bar.
     */
    public static void downMode() {
        setStates(false, false);
    }

    /**
     * Sets the states of the bottom solenoid and top solenoid.
     *
     * @param b The state of the bottom solenoid.
     * @param t The state of the top solenoid.
     */
    private static void setStates(boolean b, boolean t) {
        bottom.set(b);
        top.set(t);
    }

    @Override
    public void stop() {
    }

    @Override
    public List<Motor> getAllMotors() {
        return new ArrayList<>();
    }
}
