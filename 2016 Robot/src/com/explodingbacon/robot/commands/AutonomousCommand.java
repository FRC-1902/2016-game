package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.bcnlib.framework.Mode;
import com.explodingbacon.robot.main.Robot;
import com.explodingbacon.robot.subsystems.Drive;
import com.explodingbacon.robot.subsystems.Intake;
import com.explodingbacon.robot.subsystems.Shooter;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutonomousCommand extends Command {

    Type type;

    //Auto values used in most of/all the autos
    private static final double NEUTRAL_TO_CROSS = 13 * 12; //tuned
    private static final double NEUTRAL_TO_WALL_TIME = 5; //TODO: tune
    private static final double WALL_TO_BATTER_TIME = 5; //TODO: tune


    @Override
    public void onInit() {
        double millis = System.currentTimeMillis();
        try {
            type = (Type) Robot.autoChooser.getSelected();
            Defense defense = (Defense) Robot.defenseChooser.getSelected();
            double angleSign =( (Integer)Robot.posChooser.getSelected()) * 1.0;
            Thread.sleep(Math.round(SmartDashboard.getNumber("Auto Delay", 3) * 1000));
            if (type == Type.CROSS) {
                if (defense == Defense.ROCK_WALL) {
                    Drive.inchDrive(NEUTRAL_TO_CROSS + (4 * 12));
                } else {
                    Drive.inchDrive(NEUTRAL_TO_CROSS);
                }
            } else if (type == Type.ONE_BOULDER_NEUTRAL) {
                Drive.tankDrive(1, 1);
                Thread.sleep(Math.round(NEUTRAL_TO_WALL_TIME * 1000));
                Drive.tankDrive(0, 0);
                if (Robot.isAutonomous()) {
                    Drive.gyroTurn(90 * angleSign);

                }
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
        Drive.tankDrive(0, 0);
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

    public enum Defense {
        NORMAL,
        ROCK_WALL,
        CHEVAL,
        PORTCULLIS
    }

    public enum Type {
        CROSS,
        ONE_BOULDER_NEUTRAL,
        NOTHING
    }
}
