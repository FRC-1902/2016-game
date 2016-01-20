package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.robot.main.Robot;
import com.explodingbacon.robot.main.OI;

public class IntakeCommand extends Command {

    public IntakeCommand() {
        requires(Robot.intakeSubsystem);
    }

    @Override
    public void init() {}

    @Override
    public void loop() {
        boolean out = false;
        if (OI.intakeMotorIn.get()) {
            Robot.intakeSubsystem.setIntakeSpeed(1);

            Robot.shooterSubsystem.setShooterPower(-1);
            Robot.shooterSubsystem.setRoller(-1);

            out = true;
        } else if (OI.intakeMotorOut.get()) {
            Robot.intakeSubsystem.setIntakeSpeed(-1);

            out = true;
        } else {
            Robot.intakeSubsystem.setIntakeSpeed(0);
        }
        Robot.intakeSubsystem.setIntakePosition(out);
    }

    @Override
    public void stop() {}

    @Override
    public boolean isFinished() {
        return false;
    }
}
