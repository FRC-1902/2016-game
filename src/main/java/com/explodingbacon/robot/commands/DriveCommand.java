package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.robot.main.Robot;
import com.explodingbacon.robot.main.OI;

public class DriveCommand extends Command {

    public DriveCommand() {
        requires(Robot.driveSubsystem);
    }

    @Override
    public void init() {}

    @Override
    public void loop() {
        Robot.driveSubsystem.tankDrive(OI.left.getY(), OI.right.getY());
    }

    @Override
    public void stop() {}

    @Override
    public boolean isFinished() {
        return false;
    }
}
