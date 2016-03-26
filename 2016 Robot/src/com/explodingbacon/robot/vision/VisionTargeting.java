package com.explodingbacon.robot.vision;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.bcnlib.framework.Mode;
import com.explodingbacon.bcnlib.utils.Utils;
import com.explodingbacon.bcnlib.vision.*;
import com.explodingbacon.robot.main.Map;
import com.explodingbacon.robot.main.OI;
import com.explodingbacon.robot.main.Robot;
import com.explodingbacon.robot.subsystems.Drive;
import com.explodingbacon.robot.subsystems.Shooter;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.Collections;

public class VisionTargeting extends Command {

    private static Camera camera;
    private static boolean init = false;

    private static final double ANGLE_DEADZONE = 0.8;
    private static final double CAMERA_PIXELS_OFFSET = 0;
    private static final double MINIMUM_SIMILARITY = 2; //TODO: tune

    private static Image goalSample;

    private static VisionConfig config = null;

    private static final String imgDir = "/home/lvuser/";

    private static final TargetType TARGET_TYPE = TargetType.SHAPE;

    @Override
    public void onInit() {
        if (!init) {
            camera = new Camera(0, true);
            // Log.d("Made camera!");

            /*
            camera.setUpdatingEnabled(false);
            boolean b = camera.setExposure(EXPOSURE_DEFAULT);
            camera.setUpdatingEnabled(true);
            */

            goalSample = Image.fromFile(imgDir + "goal_sample.png").inRange(new Color(244, 244, 244), new Color(255, 255, 255));
            //Log.d("Goal sample gotten!");

            Log.v("Vision Targeting initialized!");

            init = true;
        }

        try {
            Thread.sleep(500);
        } catch (Exception e) {}
    }

    @Override
    public void onLoop() {
        try {
            if (Shooter.isVisionShootQueued()) {
                config = config == null ? new VisionConfig() : config;
                Log.v("Beginning vision shoot...");
                boolean controllingShooter = false;
                if (Shooter.shooterPID.getTarget() == 0) {
                    Shooter.getShooter().setUser(this); //Forcibly take control of this
                    Shooter.rev(this);
                    controllingShooter = true;
                }

                boolean foundGoal = false;
                boolean abort = false;
                double startMillis = System.currentTimeMillis();

                double imgMillis = System.currentTimeMillis();

                Image i = camera.getImage();
                double imageGetMS = System.currentTimeMillis() - imgMillis;

                Log.v("Took " + imageGetMS + "ms to get image.");

                double target = (i.getWidth() / 2) + CAMERA_PIXELS_OFFSET;

                Contour goal = findGoal(i, target);

                //Log.d("Starting difference: " + (goal.getMiddleX() - target));
                    /*
                    save(i, "image_raw");
                    Image indicators = i.copy();
                    drawIndicators(indicators, target, goal);
                    save(indicators, "image_target");
                    Log.d("Saved images!");
                    */

                if (isDriverAborting()) {
                    abort = true;
                    goal = null;
                }

                if (goal != null) {
                    foundGoal = true;
                    //Log.v("Goal detected.");
                    double goalMid = goal.getMiddleX();
                    double degrees = Utils.getDegreesToTurn(goalMid, target);
                    Log.d("Calculated degrees to turn");
                    Log.v(degrees + " degrees away from the goal");
                    if (Math.abs(degrees) > ANGLE_DEADZONE) {
                        /*
                        if (!Drive.gyroTurn(degrees, 6)) { //TODO: Tweak how long we should wait before giving up on the gyro turn
                            Log.v("Gyro turn timeout reached. Shoot aborting.");
                            Drive.setDriverControlled(true);
                            abort = true;
                        }
                        */
                        Log.v("Angle is incorrect, turning not implemented yet :(");
                    } else {
                        Log.v("Already lined up with the goal, not moving.");
                    }
                } else {
                    if (!abort) Log.v("Goal not detected!");
                }
                if (isDriverAborting()) {
                    abort = true;
                }

                if (isDriverAborting()) {
                    abort = true;
                }

                boolean success = false;
                if (!abort) {
                    if (foundGoal || (!config.goalRequired)) {
                        if (!Shooter.shooterPID.isDone()) {
                            Log.v("Waiting to shoot until the shooter is up to speed.");
                            Shooter.shooterPID.waitUntilDone();
                        }
                        Log.v("Time taken to shoot: " + ((System.currentTimeMillis() - startMillis) / 1000) + " seconds.");
                        Shooter.shootUsingIndexer(this);
                        success = true;
                    }
                }

                Shooter.setShouldVisionShoot(false);
                config = null;
                //Shooter.getIndexer().setUser(null);
                if (controllingShooter) Shooter.stopRev(this);
                if (!abort) {
                    if (success) {
                        if (foundGoal) Log.v("Shot a boulder using Vision Targeting.");
                        else Log.v("Shot a boulder without Vision Targeting due to not being able to see a goal!");
                    } else {
                        Log.v("No goal detected and config requires one, not shooting.");
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
        boolean aborting = OI.shootAbort.get() && Robot.getMode() != Mode.AUTONOMOUS;
        if (aborting) {
            Log.d("Driver is aborting the vision shot!");
        }
        return aborting;
    }

    public static void setVisionConfig(VisionConfig c) {
        config = c;
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

    private static final Color min = new Color(28, 220, 187), max = new Color(255, 255, 255); //max = new Color(206, 255, 240)

    /**
     * Creates a filtered version of the Image. The filter is designed for easily identifying goals.
     *
     * @param i The Image to filter.
     * @return A filtered version of the Image.
     */
    private static Image filter(Image i) {
        return i.inRange(min, max);
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
            if (c.getWidth() < 300 && c.getWidth() > 20 && c.getHeight() < 300 && c.getHeight() > 20) { //TODO: These 20's used to be 10's. If things are bad, go back to 10's
                if (goal == null) {
                    boolean good = false;
                    if (TARGET_TYPE == TargetType.SHAPE) {
                        good = c.compareTo(goalSample) > MINIMUM_SIMILARITY;
                    } else {
                        good = true;
                    }
                    if (good) goal = c;
                } else {
                    if (TARGET_TYPE == TargetType.CLOSEST_TO_TARGET) {
                        double cTargetError = Math.abs(c.getMiddleX() - target);
                        double goalTargetError = Math.abs(goal.getMiddleX() - target);
                        if (cTargetError < goalTargetError) {
                            goal = c;
                        }
                    } else if (TARGET_TYPE == TargetType.CLOSEST_TO_BOTTOM) {
                        double height = i.getHeight();
                        double cTargetError = Math.abs(c.getMiddleY() - height);
                        double goalTargetError = Math.abs(goal.getMiddleY() - height);
                        if (cTargetError < goalTargetError) {
                            goal = c;
                        }

                    } else if (TARGET_TYPE == TargetType.BIGGEST) {
                        if (c.getArea() > goal.getArea()) {
                            goal = c;
                        }
                    } else if (TARGET_TYPE == TargetType.SHAPE) {
                        double comp = c.compareTo(goalSample);
                        if (comp > MINIMUM_SIMILARITY  && comp > goal.compareTo(goalSample)) {
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
            if (TARGET_TYPE == TargetType.SHAPE) {
                Log.d("Goal shape rating: " + goal.compareTo(goalSample));
            }
            //Log.d("Goal's width is " + goal.getWidth() + ", height is " + goal.getHeight());
        }

        return goal;
    }

    public enum TargetType {
        BIGGEST,
        CLOSEST_TO_TARGET,
        CLOSEST_TO_BOTTOM,
        SHAPE
    }
}
