package com.explodingbacon.robot.subsystems;

import com.explodingbacon.bcnlib.actuators.DoubleSolenoid;
import com.explodingbacon.bcnlib.actuators.Motor;
import com.explodingbacon.bcnlib.framework.Subsystem;
import com.explodingbacon.robot.main.Map;
import edu.wpi.first.wpilibj.CANTalon;

public class IntakeSubsystem extends Subsystem {

    private static Motor intakeMotor = new Motor(CANTalon.class, Map.INTAKE_MOTOR).setName("Intake");
    private static DoubleSolenoid intakeOut = new DoubleSolenoid(Map.INTAKE_SOLENOID_A, Map.INTAKE_SOLENOID_B);

    public static final boolean TEST_BALLS = false;

    public IntakeSubsystem() {
        super();
        intakeOut.set(true);
    }

    /**
     * Starts intaking.
     */
    public static void intake() {
        intakeMotor.setPower(1);
        ShooterSubsystem.shooterPID.setTarget(ShooterSubsystem.INTAKE_RATE);
        ShooterSubsystem.setIndexerRaw(-1);
    }

    /**
     * Starts outtaking.
     */
    public static void outtake() {
        intakeMotor.setPower(-1);
        ShooterSubsystem.shooterPID.setTarget(ShooterSubsystem.INTAKE_RATE * -1);
        ShooterSubsystem.setIndexerRaw(1);
    }

    /**
     * Stops the motors used for intaking/outtaking.
     */
    public static void stopIntake() {
        intakeMotor.setPower(0);
        ShooterSubsystem.setIndexerRaw(0);
        ShooterSubsystem.shooterPID.setTarget(0);
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

    /**
     * Sets the speed of the Intake.
     * @param d The speed of the Intake.
     */
    public static void setRawSpeed(double d) {
        intakeMotor.setPower(d);
    }

    @Override
    public void stop() {
        intakeMotor.setPower(0);
        intakeOut.set(false);
    }
}
