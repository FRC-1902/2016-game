package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.robot.main.Robot;
import com.explodingbacon.robot.subsystems.DriveSubsystem;
import com.explodingbacon.robot.subsystems.ShooterSubsystem;

public class AutonomousCommand extends Command {

    public AutonomousCommand() {
        requires(Robot.driveSubsystem);
    }

    @Override
    public void onInit() {
        //Drive forward through low bar, turn, shoot, go back to original angle, turn around, return through low bar
        double originAngle = DriveSubsystem.getADX().getAngle();
        DriveSubsystem.inchDrive(100);
        DriveSubsystem.gyroTurn(45);
        ShooterSubsystem.shoot();
        ShooterSubsystem.waitForShoot();
        double angleDiff = DriveSubsystem.getADX().getAngle() - originAngle; //TODO: check if sign is wrong
        DriveSubsystem.gyroTurn(angleDiff + 180);
        DriveSubsystem.inchDrive(-100);
    }

    @Override
    public void onLoop() {}

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return true;
    }
}
