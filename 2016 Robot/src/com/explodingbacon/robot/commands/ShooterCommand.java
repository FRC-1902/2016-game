package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.bcnlib.utils.Timer;
import com.explodingbacon.bcnlib.vision.Vision;
import com.explodingbacon.robot.main.Robot;
import com.explodingbacon.robot.main.OI;
import com.explodingbacon.robot.subsystems.Shooter;

public class ShooterCommand extends Command {

    private boolean loggedSpeed = false;
    private boolean holdingTrim = false;
    private boolean sensorWasTrue = true;

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
        boolean sensorTrue = Shooter.getBallSensor().get();
        Shooter.setHasBall(sensorTrue);
        if (sensorTrue && !sensorWasTrue) {
            Timer t = new Timer(0.5, false, () -> {
                if (Shooter.getIndexer().isUsableBy(this)) {
                    Shooter.setIndexerRaw(-1);
                    Log.d("Did backroll");
                    Shooter.getIndexer().setUser(this);
                    Timer t2 = new Timer(.1, false, () -> {
                        Shooter.setIndexerRaw(0);
                        Shooter.getIndexer().setUser(null);
                    });
                    t2.start();
                }
            });
            t.start();
        }
        sensorWasTrue = sensorTrue;

        /*
        if (Shooter.getBallSensor().get() && sensorWasTrue) {
                Timer t = new Timer(.25, false, () -> {
                    Shooter.setHasBall(!Shooter.hasBall());
                });
                t.start();
            sensorWasTrue = false;
        } else if (!Shooter.getBallSensor().get()) {
            sensorWasTrue = true;
        }
        */

        boolean shoot = OI.shoot.getAll();

        if ((shoot || OI.shootNoVision.get()) && Robot.isEnabled()) {
            if (Vision.isInit() && !OI.shootNoVision.get()) {
                if (!Shooter.isVisionShooting()) //Don't queue new vision shots while existing ones are in progress
                Shooter.setVisionShotQueued(true);
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
