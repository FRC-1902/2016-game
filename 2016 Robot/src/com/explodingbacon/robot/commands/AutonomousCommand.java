package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.bcnlib.framework.Mode;
import com.explodingbacon.robot.main.Robot;
import com.explodingbacon.robot.subsystems.Drive;
import com.explodingbacon.robot.subsystems.Intake;
import com.explodingbacon.robot.subsystems.Shooter;

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
                Drive.inchDrive(NEUTRAL_TO_SHOOT_DISTANCE);
                if (Robot.isAutonomous()) {
                    Shooter.rev(this);
                    Drive.gyroTurn(COURTYARD_TO_CASTLE_ANGLE, 6); //TODO: tune this timeout time
                    if (Robot.isAutonomous()) {
                        Shooter.queueVisionShoot();
                        Shooter.waitForVisionShoot();
                    }
                    Shooter.stopRev(this);
                /*
                double angleDiff = Drive.getADX().getAngle() + COURTYARD_TO_CASTLE_ANGLE; //TODO: check if sign is wrong
                Drive.gyroTurn(angleDiff + 180);
                Drive.inchDrive(-80); //TODO: tune to get robot touching a defense
                */
                }
            } else if (type == Type.TWO_BOULDER_SPY) { //Shoot ball from spy box, go through low bar, get boulder, return through and shoot second boulder
                Shooter.rev(this);
                Thread.sleep(2000); //TODO: tune
                Shooter.shootUsingIndexer(this);
                Shooter.stopRev(this);
                Drive.gyroTurn(90);
                Intake.intake(this);
                Drive.inchDrive(SPYBOX_TO_SECOND_BALL_DISTANCE);
                Drive.inchDrive(-(SECOND_BALL_TO_SHOOT_DISTANCE));
                Intake.stopIntake(this);
                Shooter.rev(this);
                Drive.gyroTurn(COURTYARD_BACKWARDS_TO_CASTLE_ANGLE);
                Shooter.queueVisionShoot();
                Shooter.waitForVisionShoot();
                Shooter.stopRev(this);
            } else if (type == Type.TWO_BOULDER_NEUTRAL) { //Go through low bar, turn, shoot, turn around and go through low bar, intake ball, go through low bar backwards, turn, shoot
                Drive.inchDrive(NEUTRAL_TO_SHOOT_DISTANCE);
                Shooter.rev(this);
                Drive.gyroTurn(COURTYARD_TO_CASTLE_ANGLE);
                Shooter.queueVisionShoot();
                Shooter.waitForVisionShoot();
                Shooter.stopRev(this);
                double turnAmount = 180 - (Drive.getADX().getAngle() + COURTYARD_TO_CASTLE_ANGLE); //TODO: check this math
                Drive.gyroTurn(-turnAmount);
                Intake.intake(this);
                Drive.inchDrive(SHOT_TO_SECOND_BALL_DISTANCE);
                Drive.inchDrive(-(SECOND_BALL_TO_SHOOT_DISTANCE));
                Intake.stopIntake(this);
                Shooter.rev(this);
                Drive.gyroTurn(COURTYARD_BACKWARDS_TO_CASTLE_ANGLE);
                Shooter.queueVisionShoot();
                Shooter.waitForVisionShoot();
                Shooter.stopRev(this);

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
