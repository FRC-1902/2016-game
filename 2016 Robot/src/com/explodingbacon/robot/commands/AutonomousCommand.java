package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.bcnlib.framework.Mode;
import com.explodingbacon.robot.main.Robot;
import com.explodingbacon.robot.subsystems.DriveSubsystem;
import com.explodingbacon.robot.subsystems.ShooterSubsystem;

public class AutonomousCommand extends Command {

    public AutonomousCommand() {

    }

    @Override
    public void onInit() {
        //Drive forward through low bar, turn, shoot, turn to 180 degrees our starting angle, return through low bar

        //double originAngle = DriveSubsystem.getADX().getAngle();


        DriveSubsystem.inchDrive(12 * 10);
        DriveSubsystem.gyroTurn(45);
        ShooterSubsystem.queueVisionShoot();



        //DriveSubsystem.gyroTurn(7);

        /*
        ShooterSubsystem.shooterPID.setTarget(ShooterSubsystem.calculateRate());

        ShooterSubsystem.shooterPID.waitUntilDone();

        double angleDiff = DriveSubsystem.getADX().getAngle() - originAngle; //TODO: check if sign is wrong
        DriveSubsystem.gyroTurn(angleDiff + 180);
        DriveSubsystem.inchDrive(-100);
        */
    }

    @Override
    public void onLoop() {}

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return (Robot.getMode() == Mode.AUTONOMOUS) && Robot.getEnabled();
    }
}
