package com.explodingbacon.robot.stuff;

import com.explodingbacon.bcnlib.framework.PIDSource;

public class CameraTarget implements PIDSource {

    private double distance;

    @Override
    public double getForPID() {
        return distance;
    }

    @Override
    public void reset() {
        //boop
    }

    public void update(double d) {
        distance = d;
    }
}
