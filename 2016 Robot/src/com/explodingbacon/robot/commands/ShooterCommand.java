package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.bcnlib.vision.Vision;
import com.explodingbacon.robot.main.Robot;
import com.explodingbacon.robot.main.OI;
import com.explodingbacon.robot.subsystems.Shooter;

public class ShooterCommand extends Command {

    private boolean loggedSpeed = false;
    private boolean shooterHeldSinceVisionShot = false; //TODO: get a better name for this

    @Override
    public void onInit() {}

    @Override
    public void onLoop() {
        if (OI.shooterRevButtons.getAny()) {
            Shooter.rev(this);
        } else {
            Shooter.stopRev(this);
        }

        boolean shoot = OI.shoot.getAll();

        if (!shoot) shooterHeldSinceVisionShot = false;

        if (shoot || OI.shootNoVision.get() && Robot.isEnabled()) {
            if (Vision.isInit() && !OI.shootNoVision.get()) {
                if (!Shooter.isVisionShootQueued() && !shooterHeldSinceVisionShot) {
                    Shooter.queueVisionShoot();
                    shooterHeldSinceVisionShot = true;
                }
            } else {
                if (Shooter.getIndexer().isUsableBy(this)) {
                    Shooter.shootUsingIndexer(this);
                    /*
                    Shooter.setIndexerRaw(1);
                    Shooter.getIndexer().setUser(this);
                    */
                }
            }
            if (!loggedSpeed) {
                Log.d("Shooter Speed: " + Shooter.shooterPID.getCurrentSourceValue());
                loggedSpeed = true;
            }
        } else {
            if (Shooter.getIndexer().isUsableBy(this)) Shooter.getIndexer().setUser(null);
            loggedSpeed = false;
        }

        if (Shooter.hasBall()) {
            Shooter.getLight().enable();
        } else {
            Shooter.getLight().stop();
        }
    }

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return false;
    }
}
