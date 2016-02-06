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
                if (!ShooterSubsystem.shooterPID.isEnabled()) {
                    //TODO: When the shooter PID is enabled, set its target rate to be a rate generated based off of the robot's distance from the goal
                    ShooterSubsystem.shooterPID.enable();
                }
            } else {
                ShooterSubsystem.shooterPID.disable();
            }
            if (OI.shoot.getAny()) {
                ShooterSubsystem.shoot();
            }
        }

        if (Math.abs(ShooterSubsystem.getEncoder().getRate()) > 5) { //TODO: tweak this rate to be the correct rate to shoot into goal
            OI.manip.rumble(0.1f, 0.1f);
        } else {
            OI.manip.rumble(0, 0);
        }
    }

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return false;
    }
}
