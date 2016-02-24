package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.bcnlib.vision.Vision;
import com.explodingbacon.robot.main.Robot;
import com.explodingbacon.robot.main.OI;
import com.explodingbacon.robot.subsystems.ShooterSubsystem;

public class ShooterCommand extends Command {

    public ShooterCommand() {

    }
    private boolean loggedSpeed = false;

    @Override
    public void onInit() {}

    @Override
    public void onLoop() {
        if (OI.shooterRevButtons.getAny()) {
            ShooterSubsystem.rev(this);
        } else {
            ShooterSubsystem.stopRev(this);
        }

        if (OI.shoot.getAny() || OI.shootNoVision.get() && Robot.getEnabled()) {
            if (Vision.isInit() && !OI.shootNoVision.get()) {
                if (!ShooterSubsystem.isVisionShootQueued()) ShooterSubsystem.queueVisionShoot();
            } else {
                if (ShooterSubsystem.getIndexer().isUseableBy(this)) {
                    ShooterSubsystem.setIndexerRaw(1);
                    ShooterSubsystem.getIndexer().setUser(this);
                }
            }
            if (!loggedSpeed) {
                Log.d("Shooter Speed: " + ShooterSubsystem.shooterPID.getCurrentSourceValue());
                loggedSpeed = true;
            }
        } else {
            if (ShooterSubsystem.getIndexer().isUseableBy(this)) ShooterSubsystem.getIndexer().setUser(null);
            loggedSpeed = false;
        }
    }

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return false;
    }
}
