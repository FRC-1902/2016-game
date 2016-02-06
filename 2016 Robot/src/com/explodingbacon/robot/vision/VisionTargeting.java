package com.explodingbacon.robot.vision;

import com.explodingbacon.bcnlib.framework.InternalSource;
import com.explodingbacon.bcnlib.framework.PIDController;
import com.explodingbacon.bcnlib.utils.CodeThread;
import com.explodingbacon.bcnlib.vision.Camera;
import com.explodingbacon.bcnlib.vision.Contour;
import com.explodingbacon.bcnlib.vision.Image;
import com.explodingbacon.bcnlib.vision.Vision;
import com.explodingbacon.robot.main.OI;
import com.explodingbacon.robot.subsystems.DriveSubsystem;
import com.explodingbacon.robot.subsystems.ShooterSubsystem;
import edu.wpi.first.wpilibj.vision.USBCamera;
import java.awt.*;

public class VisionTargeting extends CodeThread {

    private Camera camera;
    private USBCamera usb;
    private PIDController left = null;
    private PIDController right = null;
    private InternalSource source = null;

    public static final double PIXELS_ERROR_FIX = 50; //The minimum pixel error needed before the Robot auto-corrects

    double kP = 1, kI = 1, min = 0.2, max = 0.35;

    private void init() {
        if (!Vision.isInitialized()) Vision.init();
        camera = new Camera(0);
        camera.getImage(); //You seem to have to call this once in order for it to work properly

        usb = new USBCamera();
        usb.setExposureManual(100); //TODO: see if this function even works

        source = new InternalSource();
        left = new PIDController(DriveSubsystem.getLeftMotors(), source, kP, kI, 0, min, max);
        right = new PIDController(DriveSubsystem.getRightMotors(), source, kP, kI, 0, min, max).setInverted(true);
    }

    @Override
    public void start() {
        init();
        super.start();
    }

    //TODO: Track hits and misses and save it to a file

    @Override
    public void code() {
        try {
            Contour goal = null;
            if (camera.isOpen()) {
                Image i = camera.getImage(); //TODO: See if there is a delay from pressing shoot to actually shooting
                int target = i.getWidth() / 2;
                goal = getGoal(i);
                if (goal != null) {
                    double midX = goal.getMiddleX();
                    double error = Math.abs(target - midX);
                    if (error > PIXELS_ERROR_FIX) { //If we are off center by more than an acceptable amount of pixels, then auto-correct if we are shooting
                        OI.drive.rumble(0, 0);
                        if (ShooterSubsystem.shouldShoot()) {
                            DriveSubsystem.setDriverControlled(false);
                            source.update(midX);
                            left.setTarget(target);
                            right.setTarget(target);
                            left.enable();
                            right.enable();
                            while (!left.isDone()) { //TODO: Decide if it is safe to just wait for left or if we have to wait for right too
                                midX = goal.getMiddleX();
                                source.update(midX);
                                Thread.sleep(25);
                            }
                            left.disable();
                            right.disable();
                            DriveSubsystem.setDriverControlled(true);
                        }
                    } else { //We are lined up with the goal
                        OI.drive.rumble(0.1f, 0.1f);
                    }
                }
            }
            //The robot will shoot the ball regardless of if it can see the target, but requires the shooter motors to be active
            if (ShooterSubsystem.shouldShoot() && ShooterSubsystem.getEncoder().getRate() > 5) { //TODO: tweak this rate to be accurate for "shooter motors are moving at an okay speed"
                ShooterSubsystem.setRoller(1);
                Thread.sleep(500);
                ShooterSubsystem.setRoller(0);
                ShooterSubsystem.setShouldShoot(false);
                if (!camera.isOpen()) {
                    System.out.println("[VISION] Shooting blind due to the camera not working!");
                } else if (goal == null) {
                    System.out.println("[VISION] Shooting a ball despite not being able to see a goal!");
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
