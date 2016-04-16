package com.explodingbacon.robot.vision;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.bcnlib.utils.Timer;
import com.explodingbacon.bcnlib.utils.Utils;
import com.explodingbacon.bcnlib.vision.*;
import com.explodingbacon.robot.main.Robot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VisionTargeting extends Command {

    private static Camera camera;
    private static boolean init = false;
    private static Image goalSample;
    private static boolean left, middle, right, goalVisible;

    private static final Object FRAMES_USE = new Object();
    private static List<Image> frames = new ArrayList<>();

    private static final Object IMAGE_USE = new Object();

    private static final double TARGET_CENTER = 0;
    private static final double ACCEPTABLE_ERROR = 10;
    private static final double MINIMUM_SIMILARITY = 2; //TODO: tune

    private static final String imgDir = "/home/lvuser/";

    private static final TargetType TARGET_TYPE = TargetType.SHAPE;

    private static int imgPerSecond = 0;
    private Timer t;

    @Override
    public void onInit() {
        if (!init) {
            t = new Timer(1, () -> {
                //System.out.println("Images per second: " + imgPerSecond);
                imgPerSecond = 0;
            });
            t.start();
            camera = new Camera(0, true);
            camera.onEachFrame((img) -> {
                if (img != null) {
                    synchronized (FRAMES_USE) {
                        frames.add(img);
                    }
                    imgPerSecond++;
                }
            }); //TODO: make sure the roborio can process the images fast enough to not lag or fall behind

            goalSample = Image.fromFile(imgDir + "goal_sample.png").inRange(new Color(244, 244, 244), new Color(255, 255, 255));

            init = true;
            Log.v("Vision Targeting initialized!");
        }

        try {
            Thread.sleep(500);
        } catch (Exception e) {}
    }

    @Override
    public void onLoop() { //TODO: make sure this doesn't cause concurrent modification exceptions
        if (frames.size() > 0) {
            Image i;
            synchronized (FRAMES_USE) {
                i = frames.get(0);
            }
            if (i != null) {
                onImage(i);
                if (frames.size() > 0) frames.remove(0);
            }
            //ImageServer.getInstance().setImage(i); //TODO: if this is too slow or not working, comment this out
            //Log.d("Frames in queue: " + frames.size());
            //TODO: make sure this can loop fast enough to keep up with the frames being passed in
        }
    }

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return false; //TODO: change back
        //return !Robot.isEnabled();
    }

    /**
     * Runs every time the Camera image updates.
     *
     * @param i The new Image.
     */
    public static void onImage(Image i) {
        if (i != null) {
            Contour goal = findGoal(i);
            if (goal != null) {
                setGoalVisible(true);
                double target = (i.getWidth() / 2) + TARGET_CENTER;
                double pos = goal.getMiddleX();
                double diff = pos - target;
                if (diff < -ACCEPTABLE_ERROR) { //If the goal is to the left of the target area
                    setAimValues(false, false, true);
                } else if (diff > ACCEPTABLE_ERROR) {//If the goal is to the right of the target area
                    setAimValues(true, false, false);
                } else { //If we are within the target area
                    setAimValues(false, true, false);
                }
            } else {
                setGoalVisible(false);
                setAimValues(false, false, false);
            }
            //System.out.println("Can see goal? " + goalVisible);
        }
    }

    /**
     * Sets if the goal if visible to the Robot.
     *
     * @param b If the goal if visible to the Robot.
     */
    private static void setGoalVisible(boolean b) {
        goalVisible = b;
        SmartDashboard.putBoolean("goal", b);
    }

    /**
     * Checks if the goal is visible.
     *
     * @return If the goal is visible.
     */
    public static boolean isGoalVisible() {
        return goalVisible;
    }

    /**
     * Sets the values for the aiming indicators on the SmartDashboard.
     *
     * @param l The left inidicator value.
     * @param m The middle inidicator value.
     * @param r The right inidicator value.
     */
    private static void setAimValues(boolean l, boolean m, boolean r) {
        left = l;
        middle = m;
        right = r;
        SmartDashboard.putBoolean("left", l);
        SmartDashboard.putBoolean("middle", m);
        SmartDashboard.putBoolean("right", r);
    }

    /**
     * Checks if the Robot should turn left to line up with the goal.
     *
     * @return If the Robot should turn left to line up with the goal.
     */
    public static boolean shouldGoLeft() {
        return left;
    }

    /**
     * Checks if the Robot should turn right to line up with the goal.
     *
     * @return If the Robot should turn right to line up with the goal.
     */
    public static boolean shouldGoRight() {
        return right;
    }

    /**
     * Checks if the Robot is lined up with the goal.
     *
     * @return If the Robot is lined up with the goal.
     */
    public static boolean isLinedUp() {
        return middle && goalVisible;
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
