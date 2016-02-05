package com.explodingbacon.robot.main;

import com.explodingbacon.bcnlib.framework.PIDController;
import com.explodingbacon.bcnlib.utils.CodeThread;
import com.explodingbacon.bcnlib.vision.Camera;
import com.explodingbacon.bcnlib.vision.Contour;
import com.explodingbacon.bcnlib.vision.Image;
import com.explodingbacon.robot.stuff.CameraTarget;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import java.awt.List;
import java.util.*;

public class VisionThread extends CodeThread {

    Camera camera;
    PIDController left = null;
    PIDController right = null;
    CameraTarget targetPID = null;

    private void init() {
        camera = new Camera(0);
        camera.getImage(); //Kill crusty image
        targetPID = new CameraTarget();
        PIDController left = new PIDController(Robot.driveSubsystem.leftMotor, targetPID, 1, 1, 1);
        PIDController right = new PIDController(Robot.driveSubsystem.rightMotor, targetPID, 1, 1, 1);
        right.setInverted(true);

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
            if (!OI.targetLock.getPrevious() && OI.targetLock.get()) {
                left.enable();
                right.enable();
            } else {
                left.disable();
                right.disable();
            }
            if (OI.targetLock.get()) {
                left.enable();
                Image i = camera.getImage();
                Contour tar = getTarget(i);
                if (tar != null) {
                    int mid = i.getWidth() / 2;
                    double midTar = tar.getMiddleX();
                    targetPID.update(mid - midTar);
                }
            }
        } catch (Exception e) {
            System.out.println("VisionThread Exception!");
            e.printStackTrace();
        }
    }

    public Contour getTarget(Image i) {

        Image filtered = i.colorRange(new Color(230, 230, 230), new Color(255, 255, 255));

        Contour biggest = null;
        for (Contour c : filtered.getContours()) {
            if (biggest == null) {
                biggest = c;
            } else {
                if (c.getArea() > biggest.getArea()) {
                    biggest = c;
                }
            }
        }

       biggest = biggest.approxEdges(0.01);

        return biggest;
    }
}
