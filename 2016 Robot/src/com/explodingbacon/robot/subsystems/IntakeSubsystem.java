package com.explodingbacon.robot.subsystems;

import com.explodingbacon.bcnlib.actuators.Motor;
import com.explodingbacon.bcnlib.framework.Subsystem;
import com.explodingbacon.robot.main.Map;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;

public class IntakeSubsystem extends Subsystem {

    private static Motor intakeMotor = new Motor(Talon.class, Map.INTAKE_MOTOR).setName("Intake");
    private static Solenoid intakeOut = new Solenoid(Map.INTAKE_SOLENOID);

    public IntakeSubsystem() {
        super();
        intakeMotor.setReversed(true);
    }

    public static void setSpeed(double d) {
        intakeMotor.setPower(d);
    }

    public static void setPosition(boolean b) {
        intakeOut.set(b);
    }

    @Override
    public void stop() {
        intakeMotor.setPower(0);
        intakeOut.set(false);
    }
}
