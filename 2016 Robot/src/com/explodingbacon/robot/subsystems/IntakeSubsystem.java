package com.explodingbacon.robot.subsystems;

import com.explodingbacon.bcnlib.actuators.DoubleSolenoid;
import com.explodingbacon.bcnlib.actuators.Motor;
import com.explodingbacon.bcnlib.framework.Subsystem;
import com.explodingbacon.robot.main.Map;
import edu.wpi.first.wpilibj.CANTalon;

public class IntakeSubsystem extends Subsystem {

    private static Motor intakeMotor = new Motor(CANTalon.class, Map.INTAKE_MOTOR).setName("Intake");
    private static DoubleSolenoid intakeOut = new DoubleSolenoid(Map.INTAKE_SOLENOID_A, Map.INTAKE_SOLENOID_B);

    public static final boolean TEST_BALLS = true;

    public IntakeSubsystem() {
        super();
        intakeOut.set(true);
    }

    /**
     * Sets the speed of the Intake.
     * @param d The speed of the Intake.
     */
    public static void setSpeed(double d) {
        intakeMotor.setPower(d);
    }

    /**
     * Sets the position of the Intake.
     * @param b The position of the Intake.
     */
    public static void setPosition(boolean b) {
        intakeOut.set(b);
    }

    /**
     * Gets the intake Motor.
     * @return The intake Motor.
     */
    public static Motor getIntake() {
        return intakeMotor;
    }

    @Override
    public void stop() {
        intakeMotor.setPower(0);
        intakeOut.set(false);
    }
}
