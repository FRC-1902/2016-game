package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.robot.main.Robot;
import com.explodingbacon.robot.main.OI;
import com.explodingbacon.robot.subsystems.DriveSubsystem;

public class DriveCommand extends Command {

    public boolean left = true;
    public double deadzone = 0.08;

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
        leftY = Math.abs(leftY) > deadzone ? leftY : 0;
        rightY = Math.abs(rightY) > deadzone ? rightY : 0;
        Robot.driveSubsystem.tankDrive(leftY, rightY);
        */

        double joyX;
        double joyY;
        if (OI.drive.leftJoyButton.get()) {
            left = true;
        } else if (OI.drive.rightJoyButton.get()) {
            left = false;
        }

        if (left) {
            joyX = OI.drive.getX();
            joyY = OI.drive.getY();
        } else {
            joyX = OI.drive.getX2();
            joyY = OI.drive.getY2();
        }

        //joyX = OI.drive.getX();
        //joyY = OI.drive.getRawAxis(3);

        joyX = Math.abs(joyX) > deadzone ? joyX : 0;
        joyY = Math.abs(joyY) > deadzone ? joyY : 0;

        DriveSubsystem.arcadeDrive(joyX, joyY);
    }

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return false;
    }
}
