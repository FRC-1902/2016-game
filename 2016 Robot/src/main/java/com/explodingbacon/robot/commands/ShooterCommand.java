package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.bcnlib.vision.Vision;
import com.explodingbacon.robot.main.Robot;
import com.explodingbacon.robot.main.OI;
import com.explodingbacon.robot.subsystems.ShooterSubsystem;

public class ShooterCommand extends Command {

    private boolean loggedSpeed = false;
    private boolean shooterHeldSinceVisionShot = false; //TODO: get a better name for this

    @Override
    public void onInit() {
        Log.d("Shooter Command Init!");
    }

    @Override
    public void onLoop() {
        if (OI.shooterRevButtons.getAny()) {
            ShooterSubsystem.rev(this);
        } else {
            ShooterSubsystem.stopRev(this);
        }

        boolean shoot = OI.shoot.getAll();

        if (!shoot) shooterHeldSinceVisionShot = false;

        if (shoot || OI.shootNoVision.get() && Robot.isEnabled()) {
            if (Vision.isInit() && !OI.shootNoVision.get()) {
                if (!ShooterSubsystem.isVisionShootQueued() && !shooterHeldSinceVisionShot) {
                    ShooterSubsystem.queueVisionShoot();
                    shooterHeldSinceVisionShot = true;
                }
            } else {
                if (ShooterSubsystem.getIndexer().isUsableBy(this)) {
                    ShooterSubsystem.shootUsingIndexer(this);
                    /*
                    ShooterSubsystem.setIndexerRaw(1);
                    ShooterSubsystem.getIndexer().setUser(this);
                    */
                }
            }
            if (!loggedSpeed) {
                Log.d("Shooter Speed: " + ShooterSubsystem.shooterPID.getCurrentSourceValue());
                loggedSpeed = true;
            }
        } else {
            if (ShooterSubsystem.getIndexer().isUsableBy(this)) ShooterSubsystem.getIndexer().setUser(null);
            loggedSpeed = false;
        }

        if (ShooterSubsystem.hasBall()) {
            ShooterSubsystem.getLight().enable();
        } else {
            ShooterSubsystem.getLight().stop();
        }
    }

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return false;
    }
}
