package com.explodingbacon.robot.vision;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.bcnlib.utils.Utils;
import com.explodingbacon.bcnlib.vision.Camera;
import com.explodingbacon.bcnlib.vision.Color;
import com.explodingbacon.bcnlib.vision.Contour;
import com.explodingbacon.bcnlib.vision.Image;
import com.explodingbacon.robot.subsystems.DriveSubsystem;
import com.explodingbacon.robot.subsystems.ShooterSubsystem;
import edu.wpi.first.wpilibj.vision.USBCamera;

public class VisionTargeting extends Command {

    private static Camera camera;
    private static USBCamera usb;
    private static boolean init = false;

    private String imgDir = "/home/lvuser/";
    private int picPairs = 0;

    public static final double PIXELS_ERROR_FIX = 50; //The minimum pixel error needed before the Robot auto-corrects

    @Override
    public void onInit() {
        if (!init) {

            Log.v("Init!");
            camera = new Camera(0);
            camera.getImage(); //You seem to have to call this once in order for it to work properly


            //usb = new USBCamera();
            //usb.setExposureManual(1); //TODO: see if this changes the appearance of the camera feed at all
            init = true;

        }
    }

    @Override
    public void onLoop() {
        try {
            if (ShooterSubsystem.isVisionShootQueued()) {
                Log.v("Found a queued shot.");
                //ShooterSubsystem.rev(this);
                boolean usedGoal = true;
                while (true) { //TODO: make this break out eventually

                    //TODO: loop at fps

                    Image i = camera.getImage();
                    Contour goal = findGoal(i);
                    save(i, "image");
                    save(filter(i), "filtered");
                    if (goal != null) {
                        Log.v("Goal detected.");
                        picPairs++;
                        double target = i.getWidth() / 2;
                        double goalMid = goal.getMiddleX();
                        double error = Math.abs(target - goalMid);
                        double degrees = Utils.getDegreesToTurn(goalMid, target);
                        Log.v("I'm " + degrees + " away from the goal");
                        if (degrees > DriveSubsystem.GYRO_ANGLE_ERROR_FIX) {
                            Log.v("Turning.");
                            DriveSubsystem.gyroTurn(degrees);
                        } else {
                            Log.v("Lined up with the goal.");
                            break;
                        }
                    } else {
                        usedGoal = false;
                        break;
                    }
                }
                //ShooterSubsystem.shooterPID.waitUntilDone();
                ShooterSubsystem.getIndexer().setUser(this);
                ShooterSubsystem.setIndexerRaw(1);
                Thread.sleep(3000);
                ShooterSubsystem.setIndexerRaw(0);
                ShooterSubsystem.setShouldVisionShoot(false);
                if (!camera.isOpen()) {
                    Log.v("Shot ball blindly due to the camera not working!");
                } else if (!usedGoal) {
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
        return false;
    }

    /**
     * Saves the Image.
     * @param i The Image to save.
     * @param name The name of the file to save it as.
     */
    private void save(Image i, String name) {
        i.saveAs(imgDir + picPairs + "_" + name + ".png");
    }

    /**
     * Creates a filtered version of the Image. The filter is designed for easily identifying goals.
     * @param i The Image to filter.
     * @return A filtered version of the Image.
     */
    private Image filter(Image i) {
        return i.colorRange(new Color(230, 230, 230), new Color(255, 255, 255));
    }

    /**
     * Gets the Contour for the retroreflective tape around the Castle's high goal.
     * @param i The image the goal is in.
     * @return The Contour for the retroreflective tape around the Castle's high goal.
     */
    private Contour findGoal(Image i) {
        Image filtered = filter(i);
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

        biggest = biggest != null ? biggest.approxEdges(0.01) : null;

        return biggest;
    }
}
