package com.explodingbacon.robot.vision;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.bcnlib.framework.Mode;
import com.explodingbacon.bcnlib.utils.Utils;
import com.explodingbacon.bcnlib.vision.Camera;
import com.explodingbacon.bcnlib.vision.Color;
import com.explodingbacon.bcnlib.vision.Contour;
import com.explodingbacon.bcnlib.vision.Image;
import com.explodingbacon.robot.main.OI;
import com.explodingbacon.robot.main.Robot;
import com.explodingbacon.robot.subsystems.Drive;
import com.explodingbacon.robot.subsystems.Shooter;

import java.util.Collections;

public class VisionTargeting extends Command {

    private static Camera camera;
    private static boolean init = false;

    private static double ANGLE_DEADZONE = 0.8;
    private static double CAMERA_PIXELS_OFFSET = 0; //was -11, then -8, now 0 due to not needing this (most likely)

    private static Image i;

    private static final String imgDir = "/home/lvuser/";
    private static final boolean REUSE_IMAGES = true;
    private static final TargetType TARGET_TYPE = TargetType.CLOSEST_TO_MIDDLE;

    @Override
    public void onInit() {
        if (!init) {

            Log.v("Vision Targeting initialized!");
            camera = new Camera(0, true);
            camera.release();

            i = new Image();
            camera.getImage(i);

            init = true;
        }
        camera.open();
    }

    @Override
    public void onLoop() {
        try {
            if (Shooter.isVisionShootQueued()) {

                Log.v("Beginning vision shoot...");
                boolean controllingShooter = false;
                if (Shooter.shooterPID.getTarget() == 0) {
                    Shooter.getShooter().setUser(this); //Forcibly take control of this
                    Shooter.rev(this);
                    controllingShooter = true;
                }

                boolean usedGoal = true;
                boolean abort = false;

                double startMillis = System.currentTimeMillis();
                if (!REUSE_IMAGES) i = new Image();
                camera.getImage(i); //Update our current image
                double imageGetMS = System.currentTimeMillis() - startMillis;

                Log.v("Took " + imageGetMS + "ms to get image.");

                double target = (i.getWidth() / 2) + CAMERA_PIXELS_OFFSET;

                Contour goal = findGoal(i, target);

                //Log.d("Starting difference: " + (goal.getMiddleX() - target));

                Image indicators = i.copy();

                drawIndicators(indicators, target, goal);

                save(indicators, "image_start");

                if (isDriverAborting()) {
                    abort = true;
                    goal = null;
                }

                if (goal != null) {
                    //Log.v("Goal detected.");
                    double goalMid = goal.getMiddleX();
                    double degrees = Utils.getDegreesToTurn(goalMid, target);
                    Log.v(degrees + " degrees away from the goal");
                    if (Math.abs(degrees) > ANGLE_DEADZONE) {
                        if (!Drive.gyroTurn(degrees, 5)) { //TODO: Tweak how long we should wait before giving up on the gyro turn
                            Log.v("Gyro turn timeout reached. Shoot aborting.");
                            abort = true;
                        }
                        Drive.setDriverControlled(false);
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
                    if (!abort) Log.v("Goal not detected!");
                    usedGoal = false;
                }

                if (isDriverAborting()) {
                    abort = true;
                }

                if (!abort) {
                    if (!Shooter.shooterPID.isDone()) {
                        Log.v("Waiting to shoot until the shooter is up to speed.");
                        Shooter.shooterPID.waitUntilDone();
                    }
                    Log.v("Time taken to shoot: " + ((System.currentTimeMillis() - startMillis) / 1000) + " seconds.");
                    Shooter.shootUsingIndexer(this);
                    /*
                    Shooter.getIndexer().setUser(this); //Forcibly take control of the indexer
                    Shooter.setIndexerRaw(1);
                    Thread.sleep(2000);
                    */
                }

                Shooter.setShouldVisionShoot(false);
                //Shooter.getIndexer().setUser(null);
                if (controllingShooter) Shooter.stopRev(this);
                if (!abort) {
                    if (usedGoal) {
                        Log.v("Shot a boulder using Vision Targeting.");
                    } else {
                        Log.v("Shot a boulder without Vision Targeting due to not being able to see a goal!");
                    }
                } else {
                    Log.v("Vision shoot aborted.");
                }
            }
        } catch (Exception e) {
            Log.e("VisionTargeting exception!");
            e.printStackTrace();
        }
    }

    /**
     * Checks if the driver is trying to abort the vision shot.
     *
     * @return If the driver is trying to abort the vision shot.
     */
    public boolean isDriverAborting() {
        boolean aborting = !OI.shoot.getAny() && Robot.getMode() != Mode.AUTONOMOUS;
        if (aborting) {
            Log.d("Driver is aborting the vision shot!");
        }
        return aborting;
    }

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return !Robot.isEnabled();
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
    private static void drawIndicators(Image i, double targetPos, Contour goal) {
        if (goal != null) {
            i.drawContours(Collections.singletonList(goal), Color.RED); //Red outline of the goal
            i.drawRectangle(goal.getBoundingBox(), Color.TEAL); //Blue rectangle of goal bounding box
            i.drawLine(Utils.round(goal.getMiddleX()), Color.GREEN); //Green line of middle of goal
        }
        i.drawLine(Utils.round(targetPos), Color.BLUE); //Blue line of target position
    }

    /**
     * Fake processes an Image and generates the indicator graphics on it. To be used for checking if certain images are
     * being processed properly.
     *
     * @param imageName The name (and possibly directory) of the image.
     * @param normalDir If the image is saved in the normal image directory.
     */
    public static void fakeProcess(String imageName, boolean normalDir) {
        Image i = Image.fromFile((normalDir ? imgDir : "") + imageName);
        double target = (i.getWidth() / 2) + CAMERA_PIXELS_OFFSET;
        Contour goal = findGoal(i, target);
        drawIndicators(i, target, goal);
        i.saveAs(imgDir + imageName + "_processed.png");
    }

    /**
     * Creates a filtered version of the Image. The filter is designed for easily identifying goals.
     *
     * @param i The Image to filter.
     * @return A filtered version of the Image.
     */
    private static Image filter(Image i) {
        return i.colorRange(new Color(230, 230, 230), new Color(255, 255, 255));
    }

    /**
     * Gets the Contour for the retroreflective tape around the Castle's high goal.
     *
     * @param i The image the goal is in.
     * @param target Where we'll want to be moving the goal to.
     * @return The Contour for the retroreflective tape around the Castle's high goal.
     */
    private static Contour findGoal(Image i, double target) {
        Image filtered = filter(i);
        Contour goal = null;
        for (Contour c : filtered.getContours()) {
            if (c.getWidth() < 300 && c.getWidth() > 10 && c.getHeight() < 300) {
                if (goal == null) {
                    goal = c;
                } else {
                    if (TARGET_TYPE == TargetType.CLOSEST_TO_MIDDLE) {
                        double cTargetError = Math.abs(c.getMiddleX() - target);
                        double goalTargetError = Math.abs(goal.getMiddleX() - target);
                        if (cTargetError < goalTargetError) {
                            goal = c;
                        }
                    } else if (TARGET_TYPE == TargetType.BIGGEST){
                        if (c.getArea() > goal.getArea()) {
                            goal = c;
                        }
                    } else {
                        Log.e("Unsupported TargetType \"" + TARGET_TYPE + "\" selected in VisionTargeting!");
                    }
                }
            }
        }

        goal = goal != null ? goal.approxEdges(0.01) : null;

        if (goal != null) {
            Log.d("Goal's width is " + goal.getWidth() + ", height is " + goal.getHeight());
        }

        return goal;
    }

    public enum TargetType {
        BIGGEST,
        CLOSEST_TO_MIDDLE
    }
}
