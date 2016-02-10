package com.explodingbacon.robot.subsystems;

import com.explodingbacon.bcnlib.actuators.Motor;
import com.explodingbacon.bcnlib.actuators.MotorGroup;
import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.bcnlib.framework.PIDController;
import com.explodingbacon.bcnlib.framework.Subsystem;
import com.explodingbacon.bcnlib.sensors.ADXSensor;
import com.explodingbacon.bcnlib.sensors.Encoder;
import com.explodingbacon.robot.main.Map;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Talon;

import java.lang.reflect.Field;

public class DriveSubsystem extends Subsystem {

    private static Motor leftMotors = new MotorGroup(Talon.class, Map.LEFT_DRIVE_1, Map.LEFT_DRIVE_2, Map.LEFT_DRIVE_3).setName("Left Drive");
    private static Motor rightMotors = new MotorGroup(Talon.class, Map.RIGHT_DRIVE_1, Map.RIGHT_DRIVE_2, Map.RIGHT_DRIVE_3).setName("Right Drive");

    private static Encoder leftEncoder = new Encoder(Map.LEFT_DRIVE_ENCODER_1, Map.LEFT_DRIVE_ENCODER_2);
    private static Encoder rightEncoder = new Encoder(Map.RIGHT_DRIVE_ENCODER_1, Map.RIGHT_DRIVE_ENCODER_2);

    private static ADXSensor adx = new ADXSensor(SPI.Port.kMXP);

    private static boolean driverControlled = true;

    private static double encoderKP = 0.13, encoderMin = 0.3, encoderMax = 0.5;

    private static double gyroKP = 0.01, gyroKI = 0.00025, gyroMin = 0.2, gyroMax = 0.35;

    public static final double GYRO_ANGLE_ERROR_FIX = 3; //The minimum angle error needed before the Robot auto-corrects

    public DriveSubsystem() {
        super();
        leftMotors.setReversed(true);

        Talon t = (Talon) ((MotorGroup)leftMotors).getMotors().get(0).getInternalSpeedController();

        Log.d(getField(t, "loopTime"));
        Log.d(getField(t, "m_maxPwm"));
        Log.d(getField(t, "m_deadbandMaxPwm"));
        Log.d(getField(t, "m_centerPwm"));
        Log.d(getField(t, "m_deadbandMinPwm"));
        Log.d(getField(t, "m_minPwm"));

    }

    public static String getField(Object o, String field) {
        try {
            Field f = PWM.class.getDeclaredField(field);
            f.setAccessible(true);
            return f.get(o).toString();
        } catch (Exception e) {}
        return null;
    }

    /**
     * Gets the MotorGroup for the left drivetrain motors.
     * @return The MotorGroup for the left drivetrain motors.
     */
    public static Motor getLeftMotors() {
        return leftMotors;
    }

    /**
     * Gets the MotorGroup for the right drivetrain motors.
     * @return The MotorGroup for the right drivetrain motors.
     */
    public static Motor getRightMotors() {
        return rightMotors;
    }

    /**
     * Gets the ADXSensor (Gyroscope and Accelerometer)
     * @return The ADXSensor.
     */
    public static ADXSensor getADX() {
        return adx;
    }

    /**
     * Checks if the DriveSubsystem is currently controllable by the driver.
     * @return If the DriveSubsystem is currently controllable by the driver.
     */
    public static boolean isDriverControlled() {
        return driverControlled;
    }

    /**
     * Sets if the DriveSubsystem can be controlled by the driver.
     * @param b If the DriveSubsystem can be controlled by the driver.
     */
    public static void setDriverControlled(boolean b) {
        driverControlled = b;
    }

    /**
     * Tank drive. Typically a driving style that uses two joysticks.
     * @param l The speed of the left motors.
     * @param r The speed of the right motors.
     */
    public static void tankDrive(double l, double r) {
        leftMotors.setPower(l);
        rightMotors.setPower(r);
    }

    /**
     * Arcade drive. Typically a driving style that uses one Joystick.
     * @param x The X axis of the Joystick.
     * @param y The Y axis of the Joystick.
     */
    public static void arcadeDrive(double x, double y) {
        tankDrive(y - x, y + x);
    }

    /**
     * Makes the Robot drive a certain amount of inches.
     * @param inches How many inches to drive.
     */
    public static void inchDrive(double inches) {
        encoderDrive(inchesToEncoder(inches));
    }

    /**
     * Makes the Robot drive a certain amount of encoder clicks.
     * @param distance How many encoder clicks to drive.
     */
    public static void encoderDrive(double distance) {
        leftEncoder.reset();
        rightEncoder.reset();
        PIDController left = new PIDController(leftMotors, leftEncoder, encoderKP, 0, 0, encoderMin, encoderMax);
        PIDController right = new PIDController(rightMotors, rightEncoder, encoderKP, 0, 0, encoderMin, encoderMax).setInverted(true);
        left.setTarget(distance);
        right.setTarget(distance);
        double startAngle = adx.getAngle();
        left.enable();
        right.enable();
        while (!left.isDone() || !right.isDone()) {
            double angleError = adx.getAngle() - startAngle; //TODO: check if the sign on this is wrong or not
            if (Math.abs(angleError) > GYRO_ANGLE_ERROR_FIX) {
                left.disable();
                right.disable();
                gyroTurn(angleError);
                left.enable();
                right.enable();
            }
            try {
                Thread.sleep(25);
            } catch (Exception e) {}
        }
    }

    /**
     * Makes the Robot turn a certain amount of degrees.
     * @param degrees How many degrees to turn.
     */
    public static void gyroTurn(double degrees) {
        adx.calibrate();
        PIDController left = new PIDController(leftMotors, adx, gyroKP, gyroKI, 0, gyroMin, gyroMax);
        PIDController right = new PIDController(rightMotors, adx, gyroKP, gyroKI, 0, gyroMin, gyroMax).setInverted(true);
        left.setTarget(degrees);
        right.setTarget(degrees);
        left.enable();
        right.enable();

        //Wait until both of the PIDs are done
        left.waitUntilDone();
        right.waitUntilDone();

        left.disable();
        right.disable();
    }

    /**
     * Converts inches to drive encoder clicks.
     * @param inches The inches to be converted.
     * @return The encoder clicks equivalent to the inches provided.
     */
    private static double inchesToEncoder(double inches) {
        return inches / (Math.PI * 9) * (9.6 * 256);
    }

    @Override
    public void stop() {
        leftMotors.setPower(0);
        rightMotors.setPower(0);
    }
}