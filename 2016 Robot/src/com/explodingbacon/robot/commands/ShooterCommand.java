package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.robot.main.Robot;
import com.explodingbacon.robot.main.OI;
import com.explodingbacon.robot.subsystems.ShooterSubsystem;

public class ShooterCommand extends Command {

    public ShooterCommand() {
        requires(Robot.shooterSubsystem);
    }

    @Override
    public void onInit() {}

    @Override
    public void onLoop() {
        if (!OI.intakeMotorIn.get()) {
            if (OI.shooterRev.get()) {
                if (!ShooterSubsystem.shooterPID.isEnabled()) ShooterSubsystem.shooterPID.enable();
            } else {
                ShooterSubsystem.shooterPID.disable();
            }
            if (OI.shoot.getAny()) {
                ShooterSubsystem.shoot();
            }
        }
    }

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return false;
    }
}
