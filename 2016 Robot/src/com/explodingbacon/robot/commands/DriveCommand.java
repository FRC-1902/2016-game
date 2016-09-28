package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.bcnlib.utils.Utils;
import com.explodingbacon.robot.main.OI;
import com.explodingbacon.robot.subsystems.Drive;

public class DriveCommand extends Command {

    public double deadzone = 0.08;
    public boolean reverseWasFalse = true;

    @Override
    public void onInit() {}

    @Override
    public void onLoop() {
        if (Drive.isDriverControlled()) {
            double joyX, joyY;

            joyX = OI.drive.getX();
            joyY = OI.drive.getY() * 0.75;


            joyX = Utils.deadzone(joyX, deadzone);
            joyY = Utils.deadzone(joyY, deadzone);

            Drive.arcadeDrive(joyX, joyY);

            if (OI.lowShift.getAny() /*|| (Math.abs(joyX) > 0.15 && Math.abs(joyY) < 0.1)*/) {
                Drive.shift(true);
            } else {
                Drive.shift(false);
            }

            if(OI.setIntakeFront.get()) Drive.driveReversed = false;
            if(OI.setShootFront.get()) Drive.driveReversed = true;

/*
            if (OI.reverseDirection.get() && reverseWasFalse) {
                Drive.driveReversed = !Drive.driveReversed;
                reverseWasFalse = false;
                Log.d("Reverse??");
            } else if (!OI.reverseDirection.get()) {
                reverseWasFalse = true;
            }
*/
        } else Drive.driveReversed = false;
    }

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return false;
    }
}
