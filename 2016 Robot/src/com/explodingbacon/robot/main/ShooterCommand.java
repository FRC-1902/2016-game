package com.explodingbacon.robot.main;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.bcnlib.framework.Log;

public class ShooterCommand extends Command {

    @Override
    public void onInit() {
    }

    @Override
    public void onLoop() {
        if (OI.rev.get()) {
            //Shooter.getShooter().setPower(0.95);
            Log.i("Encoder rate: " + Shooter.getEncoder().getRate());
            Shooter.rev(this);
        } else {
            //Shooter.getShooter().setPower(0);
            Shooter.stopRev(this);
        }
        if (OI.indexGo.get()) {
            Shooter.getIndexer().setPower(-0.15);
        } else {
            Shooter.getIndexer().setPower(0);
        }
    }

    @Override
    public void onStop() {

    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
