package com.explodingbacon.robot.subsystems;

import com.explodingbacon.bcnlib.actuators.FakeMotor;
import com.explodingbacon.bcnlib.actuators.Motor;
import com.explodingbacon.bcnlib.actuators.Solenoid;
import com.explodingbacon.bcnlib.framework.Subsystem;
import com.explodingbacon.robot.main.Map;
import edu.wpi.first.wpilibj.CANTalon;

public class IntakeSubsystem extends Subsystem {

    //TODO: Change this to a real motor
    private static Motor intakeMotor = new FakeMotor().setName("Intake"); /*new Motor(CANTalon.class, Map.INTAKE_MOTOR).setName("Intake")*/;
    private static Solenoid intakeOut = new Solenoid(Map.INTAKE_SOLENOID);

    public IntakeSubsystem() {
        super();
        intakeMotor.setReversed(true);
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

    @Override
    public void stop() {
        intakeMotor.setPower(0);
        intakeOut.set(false);
    }
}
