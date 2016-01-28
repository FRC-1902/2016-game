package com.explodingbacon.robot.main;

import com.explodingbacon.bcnlib.utils.CodeThread;
import com.explodingbacon.bcnlib.vision.Camera;
import com.explodingbacon.bcnlib.vision.Image;

import java.awt.*;

public class VisionThread extends CodeThread {

    Camera camera;

    private void init() {
        camera = new Camera(0);
        System.out.println("Camera init!");
    }

    @Override
    public void start() {
        init();
        super.start();
    }

    @Override
    public void run() {
        try {
            if (OI.takePicture.get() && !OI.takePicture.getPrevious()) {
                Image i = camera.getFrame();
                i.drawRectangle(10, 10, 400, 400, new Color(255, 146, 220));
                i.saveAs("please_work.png");
            }
        } catch (Exception e) {
            System.out.println("VisionThread Exception: " + e.getMessage());
        }
    }
}
