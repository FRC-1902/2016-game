package com.explodingbacon.robot.subsystems;

import com.explodingbacon.bcnlib.actuators.Motor;
import com.explodingbacon.bcnlib.framework.Subsystem;
import com.explodingbacon.robot.main.Map;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;

public class IntakeSubsystem extends Subsystem {

    private Motor intakeMotor = new Motor(Talon.class, Map.INTAKE_MOTOR);
    private Solenoid intakeOut = new Solenoid(Map.INTAKE_SOLENOID);

    public IntakeSubsystem() {
        super();
        intakeMotor.setReversed(true);
    }

    public void setIntakeSpeed(double d) {
        intakeMotor.setPower(d);
    }

    public void setIntakePosition(boolean b) {
        intakeOut.set(b);
    }

    @Override
    public void stop() {
        intakeMotor.setPower(0);
        intakeOut.set(false);
    }
}
