package com.explodingbacon.robot.vision;

import com.explodingbacon.bcnlib.framework.InternalSource;
import com.explodingbacon.bcnlib.framework.PIDController;
import com.explodingbacon.bcnlib.utils.CodeThread;
import com.explodingbacon.bcnlib.vision.Camera;
import com.explodingbacon.bcnlib.vision.Contour;
import com.explodingbacon.bcnlib.vision.Image;
import com.explodingbacon.robot.subsystems.DriveSubsystem;
import com.explodingbacon.robot.subsystems.ShooterSubsystem;
import java.awt.*;

public class VisionTargeting extends CodeThread {

    Camera camera;
    PIDController left = null;
    PIDController right = null;
    InternalSource source = null;

    public static final double PIXELS_ERROR_FIX = 30; //The minimum pixel error needed before the Robot auto-corrects

    double kP = 1, kI = 1, min = 0.2, max = 0.35;

    private void init() {
        camera = new Camera(0);
        camera.getImage(); //You seem to have to call this once in order for it to work properly

        source = new InternalSource();
        left = new PIDController(DriveSubsystem.getLeftMotors(), source, kP, kI, 0, min, max);
        right = new PIDController(DriveSubsystem.getRightMotors(), source, kP, kI, 0, min, max).setInverted(true);
    }

    @Override
    public void start() {
        init();
        super.start();
    }

    @Override
    public void run() {
        try {
            Image i = camera.getImage(); //TODO: See if there is a delay from pressing shoot to actually shooting
            int target = i.getWidth() / 2;
            Contour goal = getGoal(i);
            if (goal != null) {
                double midX = goal.getMiddleX();
                double error = Math.abs(target - midX);
                if (error > PIXELS_ERROR_FIX && ShooterSubsystem.shouldShoot()) { //If we are off center by more than an acceptable amount of pixels, then auto-correct if we are shooting
                    left.setTarget(target);
                    right.setTarget(target);
                    left.enable();
                    right.enable();
                    left.waitUntilDone();
                    left.disable();
                    right.disable();
                }
                if (ShooterSubsystem.shouldShoot()) {
                    ShooterSubsystem.setRoller(1);
                    Thread.sleep(500);
                    ShooterSubsystem.setRoller(0);
                    ShooterSubsystem.setShouldShoot(false);
                }
            }
        } catch (Exception e) {
            System.out.println("VisionTargeting Exception!");
            e.printStackTrace();
        }
    }

    /**
     * Gets the Contour for the retroreflective tape around the Castle's high goal.
     * @param i The image the goal is in.
     * @return The Contour for the retroreflective tape around the Castle's high goal.
     */
    public Contour getGoal(Image i) {
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
