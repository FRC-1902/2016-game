package com.explodingbacon.robot.main;

public class HamletUtils {
    public double getDistanceFromPx(int sizeInPx) {
        return 10.25*Math.tan((62.5/1280)*sizeInPx);
    }
}
