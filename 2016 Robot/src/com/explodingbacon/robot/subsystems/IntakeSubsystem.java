package com.explodingbacon.robot.subsystems;

import com.explodingbacon.bcnlib.actuators.DoubleSolenoid;
import com.explodingbacon.bcnlib.actuators.Motor;
import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.bcnlib.framework.Subsystem;
import com.explodingbacon.robot.main.Map;
import edu.wpi.first.wpilibj.CANTalon;
import java.util.Arrays;
import java.util.List;

public class IntakeSubsystem extends Subsystem {

    private static Motor intakeMotor = new Motor(CANTalon.class, Map.INTAKE_MOTOR).setName("Intake");
    private static DoubleSolenoid intakeOut = new DoubleSolenoid(Map.INTAKE_SOLENOID_A, Map.INTAKE_SOLENOID_B);

    public static final boolean TEST_BALLS = false;

    public IntakeSubsystem() {
        super();
        intakeOut.set(true);

        intakeMotor.setStopOnNoUser();
    }

    /**
     * Starts intaking.
     */
    public static void intake(Command c) {
        if (intakeMotor.isUseableBy(c) && ShooterSubsystem.getIndexer().isUseableBy(c) && ShooterSubsystem.getShooter().isUseableBy(c)) {
            intakeMotor.setPower(1);
            ShooterSubsystem.shooterPID.setTarget(ShooterSubsystem.INTAKE_RATE);
            ShooterSubsystem.setIndexerRaw(-1);
            setUsingAll(c);
        }
    }

    /**
     * Starts outtaking.
     */
    public static void outtake(Command c) {
        if (intakeMotor.isUseableBy(c) && ShooterSubsystem.getShooter().isUseableBy(c)) {
            intakeMotor.setPower(-1);
            ShooterSubsystem.shooterPID.setTarget(ShooterSubsystem.INTAKE_RATE * -0.75);

            intakeMotor.setUser(c);
            ShooterSubsystem.getShooter().setUser(c);
        }
    }

    /**
     * Stops using the intake-related Motors.
     */
    public static void stopIntake(Command c) {
        if (intakeMotor.isUseableBy(c)) intakeMotor.setUser(null);
        if (ShooterSubsystem.getShooter().isUseableBy(c)) ShooterSubsystem.getShooter().setUser(null);
        if (ShooterSubsystem.getIndexer().isUseableBy(c)) ShooterSubsystem.getIndexer().setUser(null);
    }

    /**
     * Changes the status of the Intake's use of intake-related Motors.
     * @param c
     */
    private static void setUsingAll(Command c) {
        intakeMotor.setUser(c);
        ShooterSubsystem.getShooter().setUser(c);
        ShooterSubsystem.getIndexer().setUser(c);
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

    @Override
    public List<Motor> getAllMotors() {
        return Arrays.asList(intakeMotor);
    }
}
