package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.bcnlib.utils.Drive;
import com.explodingbacon.robot.main.Robot;
import com.explodingbacon.robot.main.OI;

public class DriveCommand extends Command {

    public boolean left = true;

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

        double joyX;
        double joyY;
        if (OI.leftJoy.get()) {
            left = true;
        } else if (OI.rightJoy.get()) {
            left = false;
        }

        if (left) {
            joyX = OI.drive.getX();
            joyY = OI.drive.getY();
        } else {
            joyX = OI.drive.getRawAxis(2);
            joyY = OI.drive.getRawAxis(3);
        }

        //joyX = OI.drive.getX();
        //joyY = OI.drive.getRawAxis(3);

        if (Math.abs(joyX) < 0.08) {
            joyX = 0;
        }
        if (Math.abs(joyY) < 0.08) {
            joyY = 0;
        }

        Robot.driveSubsystem.arcadeDrive(joyX, joyY);
    }

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return false;
    }
}
