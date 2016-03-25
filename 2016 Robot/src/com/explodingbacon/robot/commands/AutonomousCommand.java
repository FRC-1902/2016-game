package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.bcnlib.framework.Mode;
import com.explodingbacon.robot.main.Robot;
import com.explodingbacon.robot.subsystems.Drive;
import com.explodingbacon.robot.subsystems.Intake;
import com.explodingbacon.robot.subsystems.Shooter;
import com.explodingbacon.robot.vision.VisionConfig;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutonomousCommand extends Command {


    Type type;

    //Auto values used in most of/all the autos
    private static final double NEUTRAL_TO_SHOOT_DISTANCE = 13 * 12; //tuned

    //Two ball auto values
    private static final double SPYBOX_TO_SECOND_BALL_DISTANCE = 20 * 12; //TODO: tune
    private static final double SECOND_BALL_TO_SHOOT_DISTANCE = 11 * 12; //TODO: tune
    private static final double COURTYARD_BACKWARDS_TO_CASTLE_ANGLE = 150; //TODO: tune

    //Two ball neutral auto value(s)
    private static final double SHOT_TO_SECOND_BALL_DISTANCE = 11 * 12; //TODO: tune


    //TODO: Add setting for rock wall since we need to go farther to get over that

    @Override
    public void onInit() {
        double millis = System.currentTimeMillis();
        try {
            type = (Type) Robot.autoChooser.getSelected();
            double angleToShoot =((Integer)Robot.posChooser.getSelected()) * 1.0;
            Thread.sleep(Math.round(SmartDashboard.getNumber("Auto Delay", 3) * 1000));
            if (type == Type.CROSS || type == Type.ONE_BOULDER_NEUTRAL) { //Drive forward, turn, shoot, turn to back to original angle, back into defense ramp
                Drive.inchDrive(NEUTRAL_TO_SHOOT_DISTANCE);
                if (type == Type.ONE_BOULDER_NEUTRAL) {
                    if (angleToShoot == 45) Drive.inchDrive(7 * 12); //If we're first pos
                    Thread.sleep(500);
                    if (Robot.isAutonomous()) {
                        Shooter.rev(this, Shooter.BAD_BALL_SHOOT_RATE);
                        if (angleToShoot != 0) Drive.gyroTurn(angleToShoot, 6);
                        if (Robot.isAutonomous()) {
                            Shooter.queueSafeVisionShoot();
                            Shooter.waitForVisionShoot();
                            if (Robot.isAutonomous()) {
                                Drive.gyroTurn(-Drive.getADX().getAngle());
                                Drive.inchDrive(-80); //TODO: tune to get robot touching a defense
                            }
                        }
                        Shooter.stopRev(this);
                    }
                }
            } else if (type == Type.ONE_BOULDER_SPY || type == Type.ONE_BOULDER_SPY_NOCROSS) {
                Shooter.rev(this);
                Shooter.waitForRev();
                Shooter.shootUsingIndexer(this);
                Shooter.stopRev(this);
                if (type == Type.ONE_BOULDER_SPY) {
                    Drive.gyroTurn(91.5);
                    Drive.inchDrive(23 * 12);
                    Thread.sleep(500);
                    Drive.inchDrive(-(15 * 12));
                }
            } else if (type == Type.TWO_BOULDER_SPY) { //Shoot ball from spy box, go through low bar, get boulder, return through and shoot second boulder
                Shooter.rev(this);
                Shooter.waitForRev();
                Shooter.shootUsingIndexer(this);
                Shooter.stopRev(this);
                Drive.gyroTurn(90);
                Intake.intake(this);
                Drive.inchDrive(SPYBOX_TO_SECOND_BALL_DISTANCE);
                Drive.inchDrive(-(SECOND_BALL_TO_SHOOT_DISTANCE));
                Intake.stopIntake(this);
                Shooter.rev(this);
                Drive.gyroTurn(COURTYARD_BACKWARDS_TO_CASTLE_ANGLE);
                Shooter.queueSafeVisionShoot();
                Shooter.waitForVisionShoot();
                Shooter.stopRev(this);
            } else if (type == Type.TWO_BOULDER_NEUTRAL) { //Go through low bar, turn, shoot, turn around and go through low bar, intake ball, go through low bar backwards, turn, shoot
                Drive.inchDrive(NEUTRAL_TO_SHOOT_DISTANCE);
                Shooter.rev(this);
                Drive.gyroTurn(angleToShoot);
                Shooter.queueVisionShoot();
                Shooter.waitForVisionShoot();
                Shooter.stopRev(this);
                double turnAmount = 180 - (Drive.getADX().getAngle() + angleToShoot); //TODO: check this math
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
            Drive.setDriverControlled(true);
        } catch (Exception e) {
            Log.e("AutonomousCommand exception!");
            e.printStackTrace();
        }
        Log.i("Autonomous complete. Time elapsed: " + ((System.currentTimeMillis() - millis) / 1000) + " seconds");
    }

    @Override
    public void onLoop() {
    }

    @Override
    public void onStop() {
    }

    @Override
    public boolean isFinished() {
        return (Robot.getMode() == Mode.AUTONOMOUS) && Robot.isEnabled();
    }

    public enum Type {
        CROSS,
        ONE_BOULDER_NEUTRAL,
        ONE_BOULDER_SPY,
        ONE_BOULDER_SPY_NOCROSS,
        TWO_BOULDER_NEUTRAL,
        TWO_BOULDER_SPY,
        NOTHING
    }
}
