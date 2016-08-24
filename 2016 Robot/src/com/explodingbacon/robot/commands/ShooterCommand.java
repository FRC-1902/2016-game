package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.robot.main.Robot;
import com.explodingbacon.robot.main.OI;
import com.explodingbacon.robot.subsystems.Shooter;

public class ShooterCommand extends Command {

    private boolean loggedSpeed = false;
    private boolean holdingTrim = false;

    private final int TRIM_AMOUNT = 1000;


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

        if ((shoot || OI.shootNoVision.get()) && Robot.isEnabled()) {
            if (!OI.shootNoVision.get()) {
                //Shooter.doVisionShoot(this);
            } else {
                if (Shooter.getIndexer().isUsableBy(this)) {
                    Shooter.setIndexerRaw(1);
                    Shooter.getIndexer().setUser(this);
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

        if(OI.resetLeftTrim.get()) Shooter.HIGH_OFFSET = 0;
        if(OI.resetRightTrim.get()) Shooter.LOW_OFFSET = 0;

        //TODO: Reverse
        if(!holdingTrim) {
            if (OI.trimLeftUp.get()) Shooter.HIGH_OFFSET += TRIM_AMOUNT;
            if (OI.trimLeftDown.get()) Shooter.HIGH_OFFSET -= TRIM_AMOUNT;
            if (OI.trimRightUp.get()) Shooter.LOW_OFFSET += TRIM_AMOUNT;
            if (OI.trimRightDown.get()) Shooter.LOW_OFFSET -= TRIM_AMOUNT;
        }

        holdingTrim = OI.trimButtons.getAny();
    }

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return false;
    }
}
