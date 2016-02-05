package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.robot.subsystems.DriveSubsystem;
import com.explodingbacon.robot.subsystems.ShooterSubsystem;

public class AutonomousCommand extends Command{

    @Override
    public void onInit() {
        DriveSubsystem.inchDrive(100);
        DriveSubsystem.gyroTurn(45);
        ShooterSubsystem.shoot();
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
