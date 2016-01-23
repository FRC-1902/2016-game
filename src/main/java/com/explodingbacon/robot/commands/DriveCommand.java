package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.robot.main.Robot;
import com.explodingbacon.robot.main.OI;

public class DriveCommand extends Command {

    public DriveCommand() {
        requires(Robot.driveSubsystem);
    }

    @Override
    public void onInit() {}

    @Override
    public void onLoop() {
        /*
        double leftY = OI.left.getY();
        double rightY = OI.right.getY();
        leftY = Math.abs(leftY) > 0.05 ? leftY : 0;
        rightY = Math.abs(rightY) > 0.05 ? rightY : 0;
        Robot.driveSubsystem.tankDrive(leftY, rightY);
        */
        Robot.driveSubsystem.arcadeDrive(OI.drive.getX(), OI.drive.getY());
    }

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return false;
    }
}
