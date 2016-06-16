package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.robot.main.OI;
import com.explodingbacon.robot.subsystems.Intake;
import com.explodingbacon.robot.subsystems.Shooter;

import java.util.List;

public class IntakeCommand extends Command {

    boolean currentState = true, wasPressed = false;

    @Override
    public void onInit() {}

    @Override
    public void onLoop() {
        if (OI.intakeMotorIn.get() && !Shooter.hasBall()) {
            Intake.intake(this);
        } else if (OI.intakeMotorOut.get()) {
            Intake.outtake(this);
        } else {
            Intake.stopIntake(this);
        }

        boolean pressed = OI.intakeRetract.getAny();

        if(pressed && !wasPressed) {
            currentState = !currentState;
            Intake.setPosition(currentState);
        }

        wasPressed = pressed;
    }

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return false;
    }
}
