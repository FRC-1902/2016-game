package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.robot.main.Robot;
import com.explodingbacon.robot.main.OI;
import com.explodingbacon.robot.subsystems.ShooterSubsystem;

public class ShooterCommand extends Command {

    public ShooterCommand() {
        requires(Robot.shooterSubsystem);
    }
    private boolean loggedSpeed = false;

    @Override
    public void onInit() {}

    @Override
    public void onLoop() {
        /*
        if (OI.shoot.getAny()) {
            ShooterSubsystem.queueVisionShoot();
        }
        */

        if (!OI.intakeMotorIn.get()) {

            if (OI.shooterRev.get()) {
                ShooterSubsystem.rev();

            } else {
                ShooterSubsystem.stopRev();
            }

            if(!OI.testingBall.get()) {
                if (OI.shoot.getAny()) {
                    ShooterSubsystem.setIndexerRaw(1);
                    if(!loggedSpeed)
                        Log.d("Shooter Speed: " + ShooterSubsystem.shooterPID.getCurrentSourceValue());
                    loggedSpeed = true;
                } else {
                    ShooterSubsystem.setIndexerRaw(0);
                    loggedSpeed = false;
                }
            }
        }
    }

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return false;
    }
}
