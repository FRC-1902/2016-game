package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.bcnlib.framework.Mode;
import com.explodingbacon.robot.main.Robot;
import com.explodingbacon.robot.subsystems.DriveSubsystem;
import com.explodingbacon.robot.subsystems.IntakeSubsystem;
import com.explodingbacon.robot.subsystems.ShooterSubsystem;

public class AutonomousCommand extends Command {

    Type type;

    @Override
    public void onInit() {
        double millis = System.currentTimeMillis();
        try {
            type = (Type) Robot.autoChooser.getSelected();
            if (type == Type.ONE_BOULDER) { //Drive forward through low bar, turn, shoot, turn to 180 degrees our starting angle, stop touching low bar
                //double originAngle = DriveSubsystem.getADX().getAngle();
                DriveSubsystem.inchDrive(12 * 10); //TODO: tune to go through low bar and into a good shooting position
                DriveSubsystem.gyroTurn(45); //TODO: tune to point at the castle
                ShooterSubsystem.queueVisionShoot();
                /*
                Utils.waitFor(() -> !ShooterSubsystem.isVisionShootQueued());
                double angleDiff = DriveSubsystem.getADX().getAngle() - originAngle; //TODO: check if sign is wrong
                DriveSubsystem.gyroTurn(angleDiff + 180);
                DriveSubsystem.inchDrive(-80); //TODO: tune to get robot touching a defense
                */
            } else if (type == Type.TWO_BOULDER) { //Shoot ball from spy box, go through low bar, get boulder, return through and shoot second boulder
                ShooterSubsystem.rev(this);
                Thread.sleep(2000); //TODO: tune
                ShooterSubsystem.shootUsingIndexer(this);
                ShooterSubsystem.stopRev(this);
                DriveSubsystem.gyroTurn(90);
                IntakeSubsystem.intake(this);
                DriveSubsystem.inchDrive(12 * 10); //TODO: tune to go through low bar and get boulder without breaking the auto plane line thing
                DriveSubsystem.inchDrive(-(11 * 10)); //TODO: tune to go through low bar and into a good shooting position
                DriveSubsystem.gyroTurn(120); //TODO: tune to be pointing at the castle
                ShooterSubsystem.queueVisionShoot();
            } else if (type == Type.NOTHING) {
                Log.i("Doing nothing in auto!");
            } else if (type == null) {
                Log.e("Autonomous type is null!");
            } else {
                Log.e("Autonomous type \"" + type.toString() + "\" does not have any code!");
            }
        } catch (Exception e) {
            Log.e("AutonomousCommand exception!");
            e.printStackTrace();
        }
        Log.i("Auto code complete. Time elapsed since init: " + ((System.currentTimeMillis() - millis) / 1000) + " seconds");
    }

    @Override
    public void onLoop() {}

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return (Robot.getMode() == Mode.AUTONOMOUS) && Robot.isEnabled();
    }

    public enum Type {
        ONE_BOULDER,
        TWO_BOULDER,
        NOTHING
    }
}
