package com.explodingbacon.robot.vision;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.bcnlib.utils.Timer;
import com.explodingbacon.bcnlib.utils.Utils;
import com.explodingbacon.bcnlib.vision.*;
import com.explodingbacon.robot.main.Robot;
import com.explodingbacon.robot.subsystems.Drive;
import com.explodingbacon.robot.subsystems.Shooter;

import java.util.Collections;

public class VisionTargeting extends Command {

    private static Camera camera;
    private static boolean init = false;
    private static Image goalSample;

    private static final double TARGET_CENTER = 0;
    private static final double ACCEPTABLE_ERROR = 2;
    private static final double MINIMUM_SIMILARITY = 2; //TODO: tune

    private static final String imgDir = "/home/lvuser/";

    private static final TargetType TARGET_TYPE = TargetType.SHAPE;

    private Timer t;

    @Override
    public void onInit() {
        if (!init) {
            goalSample = Image.fromFile(imgDir + "goal_sample.png").inRange(new Color(244, 244, 244), new Color(255, 255, 255));

            camera = new Camera(0, true);
            camera.onEachFrame((img) -> { //TODO: make sure the roborio can process the images fast enough to not lag or fall behind
                if (img != null) {
                    if (Shooter.isVisionShotQueued() && !Shooter.isVisionShooting() && Robot.isEnabled()) {
                        //TODO: Worry about what happens if Robot is disabled/switches modes in the middle of this? Should we be worried?
                        Shooter.setVisionShotQueued(false);
                        Shooter.setVisionShooting(true);
                        Contour goal = findGoal(img);
                        double target = (img.getWidth() / 2) + TARGET_CENTER;

                        //Start: Optional image saving code
                        Image visuals = img.copy();
                        drawIndicators(visuals, target, goal);
                        save(visuals, "goal_gui");
                        visuals.release();
                        //End: Optional image saving code
                        Utils.runInOwnThread(() -> {
                            if (goal != null) {
                                double angle = Utils.getDegreesToTurn(goal.getMiddleX(), target);
                                Log.v("Found a goal! X is " + goal.getMiddleX() + ", Y is " + goal.getMiddleY() + ", angle is " + angle + ".");
                                if (angle > ACCEPTABLE_ERROR) {
                                    Log.v("Angle error is larger than acceptable error (" + ACCEPTABLE_ERROR + "), turning to adjust.");
                                    Drive.gyroTurn(angle);
                                } else {
                                    Log.v("Angle is within acceptable error. Not turning.");
                                }
                            } else {
                                Log.v("Unable to find goal! Shooting blind.");
                            }
                            Shooter.rev(this);
                            Shooter.waitForRev();
                            Shooter.shootUsingIndexer(this);
                            Shooter.stopRev(this);
                            Shooter.setVisionShooting(false);
                        });
                    }
                    img.release();
                }
            });

            init = true;
            Log.v("Vision Targeting initialized!");
        }

        try {
            Thread.sleep(500);
        } catch (Exception e) {}
    }

    @Override
    public void onLoop() {}

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return false;
    }

    /**
     * Saves the Image to the VisionTargeting directory.
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
     * @return The Contour for the retroreflective tape around the Castle's high goal.
     */
    private static Contour findGoal(Image i) {
        Image filtered = filter(i);
        Contour goal = null;
        for (Contour c : filtered.getContours()) {
            if (c.getWidth() < 300 && c.getWidth() > 20 && c.getHeight() < 300 && c.getHeight() > 20) { //TODO: These 20's used to be 10's. If things are bad, go back to 10's
                if (goal == null) {
                    boolean good;
                    if (TARGET_TYPE == TargetType.SHAPE) {
                        good = c.compareTo(goalSample) > MINIMUM_SIMILARITY;
                    } else {
                        good = true;
                    }
                    if (good) goal = c;
                } else {
                    if (TARGET_TYPE == TargetType.BIGGEST) {
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
                //Log.d("Final goal shape rating: " + goal.compareTo(goalSample));
            }
        }

        return goal;
    }

    /**
     * Checks if a goal is in a sample image. Used to check if the current detection method works on a sample image.
     *
     * @param imgPath The path to the Image file.
     * @return If a goal is in the image or not.
     */
    public boolean hasGoal(String imgPath) {
        return findGoal(Image.fromFile(imgPath)) != null;
    }


    public enum TargetType {
        BIGGEST,
        SHAPE
    }
}
