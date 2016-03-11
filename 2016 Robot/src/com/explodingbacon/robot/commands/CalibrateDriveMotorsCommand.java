package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.actuators.Light;
import com.explodingbacon.bcnlib.actuators.Motor;
import com.explodingbacon.bcnlib.controllers.Direction;
import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.robot.main.OI;
import com.explodingbacon.robot.subsystems.Drive;
import com.explodingbacon.robot.subsystems.Shooter;

public class CalibrateDriveMotorsCommand extends Command {

    private Motor left, right;
    private Light light;

    public CalibrateDriveMotorsCommand() {
        left = Drive.getLeft();
        right = Drive.getRight();
        light = Shooter.getLight();
    }

    private void tune(Motor m) {
        try {
            light.enable();
            Thread.sleep(1000);

            //light.blink(0.1);
            m.setPower(1);

            Thread.sleep(2000);

            m.setPower(-1);

            Thread.sleep(2000);

            m.setPower(0);

            Thread.sleep(1000);

            light.stop();
        } catch (Exception e) {
            Log.e("Motor Controller calibration error! (CalibrateDriveMotorsCommand)");
            e.printStackTrace();
        }
    }

    @Override
    public void onInit() {}

    @Override
    public void onLoop() {
        Direction d = OI.manip.getDPad();
        if(d.isLeft()) {
            tune(left);
        } else if (d.isRight()) {
            tune(right);
        }
    }

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return false;
    }
}
