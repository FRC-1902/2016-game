package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.bcnlib.framework.PIDController;
import com.explodingbacon.bcnlib.utils.Utils;
import com.explodingbacon.robot.main.Robot;
import com.explodingbacon.robot.main.OI;
import com.explodingbacon.robot.subsystems.DriveSubsystem;

public class DriveCommand extends Command {

    public double deadzone = 0.08;

    public double angleStart = 0;
    public boolean buttonWasTrue = false;
    public double previousRate = 0;

    public boolean wasShiftingLow = false;

    public double gyroKP = 1, gyroKI = 1, min = 0.1, max = 1;

    public PIDController left = new PIDController(null, DriveSubsystem.getADX(), gyroKP, gyroKI, 0, min, max);
    public PIDController right = new PIDController(null, DriveSubsystem.getADX(), gyroKP, gyroKI, 0, min, max).setInputInverted(true);

    public DriveCommand() {
    }

    @Override
    public void onInit() {}

    @Override
    public void onLoop() {
        if (DriveSubsystem.isDriverControlled()) {
            double leftTurn = 0, rightTurn = 0;
            /*
            if (OI.gyroForward.get()) { //TODO: uncomment this
                ADXSensor adx = DriveSubsystem.getADX();
                if (!buttonWasTrue) angleStart = adx.getAngle();
                double angleError = adx.getAngle() - angleStart; //TODO: Check if the sign is wrong
                if (Math.abs(angleError) > DriveSubsystem.GYRO_ANGLE_TOLERANCE) {
                    if (!left.isEnabled()) {
                        left.setTarget(angleStart);
                        left.enable();
                    }
                    if (!right.isEnabled()) {
                        right.setTarget(angleStart);
                        right.enable();
                    }

                    leftTurn = left.getMotorPower() / 3;
                    rightTurn = right.getMotorPower() / 3;

                }

                buttonWasTrue = true;
            } else {
                buttonWasTrue = false;
            }
            */

            //DriveSubsystem.shiftIfResistance();

            double joyX;
            double joyY;

            joyX = OI.drive.getX() + leftTurn;
            joyY = OI.drive.getY() + rightTurn;

            //joyX = OI.drive.getX();
            //joyY = OI.drive.getRawAxis(3);

            joyX = Utils.deadzone(joyX, deadzone);
            joyY = Utils.deadzone(joyY, deadzone);

            DriveSubsystem.arcadeDrive(joyX, joyY);

            if (OI.lowShift.getAny() /*|| (Math.abs(joyX) > 0.15 && Math.abs(joyY) < 0.1)*/) {
                DriveSubsystem.shift(true);
            } else {
                DriveSubsystem.shift(false);
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
