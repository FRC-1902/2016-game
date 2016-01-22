package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.robot.main.Robot;
import com.explodingbacon.robot.main.OI;

public class ShooterCommand extends Command {

    public ShooterCommand() {
        requires(Robot.shooterSubsystem);
    }

    @Override
    public void onInit() {}

    @Override
    public void onLoop() {
        if (!OI.intakeMotorIn.get()) {
            Robot.shooterSubsystem.setShooterPower(OI.shooterRev.get() ? 1 : 0); //When true, have shooter motors running
            Robot.shooterSubsystem.setRoller(OI.shooterShoot1.get() || OI.shooterShoot2.get() ? 1 : 0); //When true, the ball is actually shot
        }
    }

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return false;
    }
}
