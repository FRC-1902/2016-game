package com.explodingbacon.robot.vision;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.bcnlib.utils.Utils;
import com.explodingbacon.bcnlib.vision.Camera;
import com.explodingbacon.bcnlib.vision.Color;
import com.explodingbacon.bcnlib.vision.Contour;
import com.explodingbacon.bcnlib.vision.Image;
import com.explodingbacon.robot.main.Robot;
import com.explodingbacon.robot.subsystems.DriveSubsystem;
import com.explodingbacon.robot.subsystems.ShooterSubsystem;
import org.opencv.videoio.Videoio;

import java.util.Arrays;

public class VisionTargeting extends Command {

    private static Camera camera;
    private static boolean init = false;

    private static double ANGLE_DEADZONE = 0.8;
    private static double CAMERA_PIXELS_OFFSET = 0; //was -11, then -8

    private String imgDir = "/home/lvuser/";

    @Override
    public void onInit() {
        if (!init) {

            Log.v("VisionTargeting initialized!");
            camera = new Camera(0, true);

            init = true;

        }
    }

    @Override
    public void onLoop() {
        try {
            if (ShooterSubsystem.isVisionShootQueued()) {
                Log.v("Beginning vision shoot...");
                boolean controllingShooter = false;
                if (ShooterSubsystem.shooterPID.getTarget() == 0) {
                    ShooterSubsystem.getShooter().setUser(this); //Forcibly take control of this
                    ShooterSubsystem.rev(this);
                    controllingShooter = true;
                }
                boolean usedGoal = true;

                double startMillis = System.currentTimeMillis();
                Image i = camera.getImage();
                double imageGetMS = System.currentTimeMillis() - startMillis;

                //Log.v("Took " + imageGetMS + "ms to get image.");

                double target = (i.getWidth() / 2) + CAMERA_PIXELS_OFFSET;

                Contour goal = findGoal(i, target);

                //Log.d("Starting difference: " + (goal.getMiddleX() - target));

                drawIndicators(i, target, goal);

                save(i, "image_start");

                if (goal != null) {
                    //Log.v("Goal detected.");
                    double goalMid = goal.getMiddleX();
                    double degrees = Utils.getDegreesToTurn(goalMid, target);
                    Log.v(degrees + " degrees away from the goal");
                    if (Math.abs(degrees) > ANGLE_DEADZONE) {
                        DriveSubsystem.gyroTurn(degrees, 5); //TODO: Tweak how long we should wait before giving up on the gyro turn
                        /*
                        Image end = camera.getImage();
                        Contour endGoal = findGoal(end, target);
                        if (endGoal != null) drawIndicators(end, target, endGoal);
                        save(end, "image_end");
                        */
                    } else {
                        Log.v("Already lined up with the goal, not moving.");
                    }
                } else {
                    Log.v("Goal not detected!");
                    usedGoal = false;
                }
                if (!ShooterSubsystem.shooterPID.isDone()) {
                    Log.v("Waiting to shoot until the shooter is up to speed.");
                    ShooterSubsystem.shooterPID.waitUntilDone();
                }
                Log.v("Time taken to shoot: " + ((System.currentTimeMillis() - startMillis) / 1000) + " seconds");
                ShooterSubsystem.getIndexer().setUser(this);
                ShooterSubsystem.setIndexerRaw(1);
                Thread.sleep(2000);
                ShooterSubsystem.setShouldVisionShoot(false);

                ShooterSubsystem.getIndexer().setUser(null);
                if (controllingShooter) {
                    ShooterSubsystem.stopRev(this);
                    ShooterSubsystem.getShooter().setUser(null);
                }
                if (!usedGoal) {
                    Log.v("Shot a ball blindly due to not being able to see a goal!");
                } else {
                    Log.v("Shot a ball via VisionTargeting!");
                }
            }
        } catch (Exception e) {
            Log.e("VisionTargeting exception!");
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return !Robot.getEnabled();
    }

    /**
     * Saves the Image.
     *
     * @param i The Image to save.
     * @param name The name of the file to save it as.
     */
    private void save(Image i, String name) {
        i.saveAs(imgDir + name + ".png");
    }

    /**
     * Draws vision indicators onto an Image.
     *
     * @param i The Image for the indicators to be drawn onto.
     * @param targetPos The X coordinate of the position the target should wind up in.
     * @param goal The goal that is being targeted.
     */
    private void drawIndicators(Image i, double targetPos, Contour goal) {
        if (goal != null) {
            i.drawContours(Arrays.asList(goal), Color.RED); //Red outline of the goal
            i.drawRectangle(goal.getBoundingBox(), Color.TEAL); //Blue rectangle of goal bounding box
            i.drawLine(Utils.round(goal.getMiddleX()), Color.GREEN); //Green line of middle of goal
        }
        i.drawLine(Utils.round(targetPos), Color.BLUE); //Blue line of target position
    }

    /**
     * Creates a filtered version of the Image. The filter is designed for easily identifying goals.
     *
     * @param i The Image to filter.
     * @return A filtered version of the Image.
     */
    private Image filter(Image i) {
        return i.colorRange(new Color(230, 230, 230), new Color(255, 255, 255));
    }

    /**
     * Gets the Contour for the retroreflective tape around the Castle's high goal.
     *
     * @param i The image the goal is in.
     * @param target Where we'll want to be moving the goal to.
     * @return The Contour for the retroreflective tape around the Castle's high goal.
     */
    private Contour findGoal(Image i, double target) {
        Image filtered = filter(i);
        Contour goal = null;
        for (Contour c : filtered.getContours()) {
            if (c.getWidth() < 300 && c.getWidth() > 10 && c.getHeight() < 300) {
                if (goal == null) {
                    goal = c;
                } else {
                    /*
                    double cTargetError = Math.abs(c.getMiddleX() - target);
                    double goalTargetError = Math.abs(goal.getMiddleX() - target);
                    if (cTargetError < goalTargetError) {
                        goal = c;
                    }*/
                    if (c.getArea() > goal.getArea()) {
                        goal = c;
                    }
                }
            }
        }

        goal = goal != null ? goal.approxEdges(0.01) : null;

        if (goal != null) {
            Log.d("Goal width is " + goal.getWidth() + ", height is " + goal.getHeight());
        }

        return goal;
    }
}
