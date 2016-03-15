package com.explodingbacon.bcnlib.vision;

import com.explodingbacon.bcnlib.framework.Log;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

/**
 * A wrapper class for OpenCV's VideoCapture object.
 *
 * @author Ryan Shavell
 * @version 2016.3.8
 */

public class Camera {

    private VideoCapture cam;
    private int index = 0;
    private final Object CAMERA_USE = new Object();

    private boolean autoUpdate;
    private Thread updateThread = null;

    public Camera(int i, boolean b) {
        index = i;
        try {
            cam = new VideoCapture(index);
            autoUpdate = b;
            Thread.sleep(1000);
            if (autoUpdate) {
                updateThread = new Thread(() -> {
                    //Log.d("Camera autoupdate thread init");
                    while (true) {
                        synchronized (CAMERA_USE) {
                            if (cam.isOpened()) {
                                if (!cam.grab()) {
                                    Log.d("Camera.grab() returned false!");
                                }
                            }
                            try {
                                Thread.sleep(10);
                            } catch(Exception e) {}
                        }
                    }
                });
                updateThread.start();
            } else {
                cam.release();
            }
        } catch (Exception e) {
            Log.e("Camera init exception!");
            e.printStackTrace();
        }
    }

    /**
     * Checks if this Camera is open.
     *
     * @return If this Camera is open.
     */
    public boolean isOpen() {
        return cam.isOpened();
    }

    /**
     * Checks if this Camera is auto updating its frames.
     *
     * @return If this Camera is auto updating its frames.
     */
    public boolean isAutoUpdating() {
        return autoUpdate;
    }

    /**
     * Updates an Image with the current Image on this Camera.
     *
     * @param i The Image to be updated.
     */
    public void getImage(Image i) {
        Mat m = i.getMat();
        synchronized (CAMERA_USE) {
            if (autoUpdate) {
                cam.retrieve(m);
            } else {
                cam.open(index);
                cam.grab();
                cam.retrieve(m);
                cam.release();
            }
        }
    }

    /**
     * Gets a new Image from this Camera.
     *
     * @return A new Image from this Camera.
     */
    public Image getImage() {
        Image i = new Image();
        getImage(i);
        return i;
    }

    /**
     * Opens this Camera.
     */
    public void open() {
        cam.open(index);
    }

    /**
     * Releases this Camera.
     */
    public void release() {
        cam.release();
    }

    /**
     * Gets the value of an OpenCV property.
     *
     * @param propid The property ID. Should be a variable defined in Videoio.
     * @return The value of an OpenCV property.
     */
    public double getRaw(int propid) {
        return cam.get(propid);
    }

    /**
     * Sets the value of an OpenCV property.
     *
     * @param propid The property ID. Should be a variable defined in Videoio.
     * @param val The value to set the property to.
     * @return If changing the property was successful.
     */
    public boolean setRaw(int propid, double val) {
        return cam.set(propid, val);
    }
}