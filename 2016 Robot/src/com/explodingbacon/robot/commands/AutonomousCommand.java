package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.bcnlib.framework.Mode;
import com.explodingbacon.bcnlib.utils.Utils;
import com.explodingbacon.robot.main.Robot;
import com.explodingbacon.robot.subsystems.DriveSubsystem;
import com.explodingbacon.robot.subsystems.IntakeSubsystem;
import com.explodingbacon.robot.subsystems.ShooterSubsystem;

public class AutonomousCommand extends Command {

    Type type;

    //Auto values used in most of/all the autos
    private static final double NEUTRAL_TO_SHOOT_DISTANCE = 10 * 12; //TODO: tune
    private static final double COURTYARD_TO_CASTLE_ANGLE = 30; //TODO: tune

    //Two ball auto values
    private static final double SPYBOX_TO_SECOND_BALL_DISTANCE = 20 * 12; //TODO: tune
    private static final double SECOND_BALL_TO_SHOOT_DISTANCE = 11 * 12; //TODO: tune
    private static final double COURTYARD_BACKWARDS_TO_CASTLE_ANGLE = 150; //TODO: tune

    //Two ball neutral auto value(s)
    private static final double SHOT_TO_SECOND_BALL_DISTANCE = 11 * 12; //TODO: tune


    @Override
    public void onInit() {
        double millis = System.currentTimeMillis();
        try {
            type = (Type) Robot.autoChooser.getSelected();
            if (type == Type.ONE_BOULDER) { //Drive forward through low bar, turn, shoot, turn to 180 degrees our starting angle, stop touching low bar
                DriveSubsystem.inchDrive(NEUTRAL_TO_SHOOT_DISTANCE);
                ShooterSubsystem.rev(this);
                DriveSubsystem.gyroTurn(COURTYARD_TO_CASTLE_ANGLE);
                ShooterSubsystem.queueVisionShoot();
                Utils.waitFor(() -> !ShooterSubsystem.isVisionShootQueued());
                ShooterSubsystem.stopRev(this);
                /*
                double angleDiff = DriveSubsystem.getADX().getAngle() + COURTYARD_TO_CASTLE_ANGLE; //TODO: check if sign is wrong
                DriveSubsystem.gyroTurn(angleDiff + 180);
                DriveSubsystem.inchDrive(-80); //TODO: tune to get robot touching a defense
                */
            } else if (type == Type.TWO_BOULDER_SPY) { //Shoot ball from spy box, go through low bar, get boulder, return through and shoot second boulder
                ShooterSubsystem.rev(this);
                Thread.sleep(2000); //TODO: tune
                ShooterSubsystem.shootUsingIndexer(this);
                ShooterSubsystem.stopRev(this);
                DriveSubsystem.gyroTurn(90);
                IntakeSubsystem.intake(this);
                DriveSubsystem.inchDrive(SPYBOX_TO_SECOND_BALL_DISTANCE);
                DriveSubsystem.inchDrive(-(SECOND_BALL_TO_SHOOT_DISTANCE));
                IntakeSubsystem.stopIntake(this);
                ShooterSubsystem.rev(this);
                DriveSubsystem.gyroTurn(COURTYARD_BACKWARDS_TO_CASTLE_ANGLE);
                ShooterSubsystem.queueVisionShoot();
                Utils.waitFor(() -> !ShooterSubsystem.isVisionShootQueued());
                ShooterSubsystem.stopRev(this);
            } else if (type == Type.TWO_BOULDER_NEUTRAL) { //Go through low bar, turn, shoot, turn around and go through low bar, intake ball, go through low bar backwards, turn, shoot
                DriveSubsystem.inchDrive(NEUTRAL_TO_SHOOT_DISTANCE);
                ShooterSubsystem.rev(this);
                DriveSubsystem.gyroTurn(COURTYARD_TO_CASTLE_ANGLE);
                ShooterSubsystem.queueVisionShoot();
                Utils.waitFor(() -> !ShooterSubsystem.isVisionShootQueued());
                ShooterSubsystem.stopRev(this);
                double turnAmount = 180 - (DriveSubsystem.getADX().getAngle() + COURTYARD_TO_CASTLE_ANGLE); //TODO: check this math
                DriveSubsystem.gyroTurn(-turnAmount);
                IntakeSubsystem.intake(this);
                DriveSubsystem.inchDrive(SHOT_TO_SECOND_BALL_DISTANCE);
                DriveSubsystem.inchDrive(-(SECOND_BALL_TO_SHOOT_DISTANCE));
                IntakeSubsystem.stopIntake(this);
                ShooterSubsystem.rev(this);
                DriveSubsystem.gyroTurn(COURTYARD_BACKWARDS_TO_CASTLE_ANGLE);
                ShooterSubsystem.queueVisionShoot();
                Utils.waitFor(() -> !ShooterSubsystem.isVisionShootQueued());
                ShooterSubsystem.stopRev(this);

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
        TWO_BOULDER_SPY,
        TWO_BOULDER_NEUTRAL,
        NOTHING
    }
}
