package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.robot.main.Robot;
import com.explodingbacon.robot.main.OI;
import com.explodingbacon.robot.subsystems.ShooterSubsystem;

public class ShooterCommand extends Command {

    public ShooterCommand() {
        requires(Robot.shooterSubsystem);
    }

    @Override
    public void onInit() {}

    @Override
    public void onLoop() {
        /*
        if (OI.shoot.getAny()) {
            ShooterSubsystem.shoot();
        }
        */

        if (!OI.intakeMotorIn.get()) {

            if (OI.shooterRev.get()) {
                //ShooterSubsystem.setShooter(0.5);

                ShooterSubsystem.shooterPID.setTarget(ShooterSubsystem.DEFAULT_SHOOT_RATE);

                if (ShooterSubsystem.shooterPID.isDone()) {
                    OI.manip.rumble(0.1f, 0.1f);
                } else {
                    OI.manip.rumble(0, 0);
                }

            } else {
                //ShooterSubsystem.setShooter(0);

                ShooterSubsystem.shooterPID.setTarget(0);
                OI.manip.rumble(0, 0);
            }

            if (OI.shoot.getAny()) {
                ShooterSubsystem.setIndexer(1);
            } else {
                ShooterSubsystem.setIndexer(0);
            }
        }
        /*
        if (ShooterSubsystem.isRateAcceptable()) { //TODO: tweak this rate to be the correct rate to shoot into goal
            OI.manip.rumble(0.1f, 0.1f);
        } else {
            OI.manip.rumble(0, 0);
        }*/

    }

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return false;
    }
}
