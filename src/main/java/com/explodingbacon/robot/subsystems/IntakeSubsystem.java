package com.explodingbacon.robot.subsystems;

import com.explodingbacon.bcnlib.actuators.Talon;
import com.explodingbacon.bcnlib.framework.Subsystem;
import com.explodingbacon.robot.main.Map;
import edu.wpi.first.wpilibj.Solenoid;

public class IntakeSubsystem extends Subsystem {

    private Talon intakeMotor = new Talon(Map.INTAKE_MOTOR);
    private Solenoid intakeOut = new Solenoid(Map.INTAKE_SOLENOID);

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
