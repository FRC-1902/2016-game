package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.bcnlib.framework.PIDController;
import com.explodingbacon.bcnlib.utils.Utils;
import com.explodingbacon.robot.main.OI;
import com.explodingbacon.robot.subsystems.Drive;

public class DriveCommand extends Command {

    public double deadzone = 0.08;

    public double angleStart = 0;
    public boolean buttonWasTrue = false;
    public double previousRate = 0;

    public boolean wasShiftingLow = false;

    public double gyroKP = 1, gyroKI = 1, min = 0.1, max = 1;

    public PIDController left = new PIDController(null, Drive.getADX(), gyroKP, gyroKI, 0, min, max);
    public PIDController right = new PIDController(null, Drive.getADX(), gyroKP, gyroKI, 0, min, max).setInputInverted(true);

    public DriveCommand() {
    }

    @Override
    public void onInit() {}

    @Override
    public void onLoop() {
        if (Drive.isDriverControlled()) {
            double joyX, joyY;

            joyX = OI.drive.getX();
            joyY = OI.drive.getY();


            joyX = Utils.deadzone(joyX, deadzone);
            joyY = Utils.deadzone(joyY, deadzone);

            Drive.arcadeDrive(joyX, joyY);

            if (OI.lowShift.getAny() /*|| (Math.abs(joyX) > 0.15 && Math.abs(joyY) < 0.1)*/) {
                Drive.shift(true);
            } else {
                Drive.shift(false);
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
