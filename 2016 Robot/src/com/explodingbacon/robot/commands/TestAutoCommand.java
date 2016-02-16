package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.bcnlib.framework.Log;

public class TestAutoCommand extends Command {

    @Override
    public void onInit() {
        Log.d("Auto init");
        try {
            Thread.sleep(2000);
            Log.d("Auto waited 2 seconds");
        } catch (Exception e) {}
    }

    @Override
    public void onLoop() {}

    @Override
    public void onStop() {
        Log.d("Auto done");
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
