package com.explodingbacon.robot.subsystems;

import com.explodingbacon.bcnlib.actuators.DoubleSolenoid;
import com.explodingbacon.bcnlib.actuators.Motor;
import com.explodingbacon.bcnlib.actuators.MotorGroup;
import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.bcnlib.framework.PIDController;
import com.explodingbacon.bcnlib.framework.Subsystem;
import com.explodingbacon.bcnlib.sensors.ADXSensor;
import com.explodingbacon.bcnlib.sensors.AbstractEncoder;
import com.explodingbacon.bcnlib.sensors.Encoder;
import com.explodingbacon.robot.main.KitMap;
import com.explodingbacon.robot.main.Map;
import com.explodingbacon.robot.main.Robot;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.TalonSRX;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.Arrays;
import java.util.List;

public class Drive extends Subsystem {

    private static MotorGroup leftMotors, rightMotors;

    private static DoubleSolenoid shift;

    private static AbstractEncoder leftEncoder;
    private static AbstractEncoder rightEncoder;

    private static ADXSensor adx;

    public static PIDController eLeft, eRight, gLeft, gRight;

    private static boolean driverControlled = true;

    private static double encoderkP = 0.0001, encoderkI = 0, encoderkD = 0, encoderMin = 0.2, encoderMax = 1; //33300=8ft, encoderMax was 0.5
    private static double gyrokP = 0.025, gyrokI = 0.005, gyrokD = 0.03, gyroMin = 0.05, gyroMax = 0.5;

    public static final double GYRO_PID_TOLERANCE = 0.25;
    public static final double ENCODER_ANGLE_TOLERANCE = 1500;

    public Drive() {
        super();

        if (Robot.real) {
            leftMotors = (MotorGroup) new MotorGroup(VictorSP.class, Map.LEFT_DRIVE_1, Map.LEFT_DRIVE_2, Map.LEFT_DRIVE_3).setName("Left Drive");
            rightMotors = (MotorGroup) new MotorGroup(VictorSP.class, Map.RIGHT_DRIVE_1, Map.RIGHT_DRIVE_2, Map.RIGHT_DRIVE_3).setName("Right Drive");

            leftMotors.setReversed(true);
            rightMotors.setReversed(true);

            Log.d("Initialized Drive in Battering Ham mode.");
        } else {
            leftMotors = (MotorGroup) new MotorGroup(TalonSRX.class, KitMap.LEFT_DRIVE).setName("Left Drive");
            rightMotors = (MotorGroup) new MotorGroup(TalonSRX.class, KitMap.RIGHT_DRIVE).setName("Right Drive");

            Log.d("Initialized Drive in KitBot mode.");

            leftMotors.setReversed(true);
            rightMotors.setReversed(true);
        }

        shift = new DoubleSolenoid(Map.SHIFT_SOLENOID_A, Map.SHIFT_SOLENOID_B);

        if (Robot.real) {
            leftEncoder = new Encoder(Map.LEFT_DRIVE_ENCODER_A, Map.LEFT_DRIVE_ENCODER_B);
            rightEncoder = new Encoder(Map.RIGHT_DRIVE_ENCODER_A, Map.RIGHT_DRIVE_ENCODER_B);

            adx = new ADXSensor(SPI.Port.kOnboardCS1, SPI.Port.kOnboardCS0);

            eLeft = new PIDController(leftMotors, leftEncoder, encoderkP, encoderkI, encoderkD, encoderMin, encoderMax);
            eRight = new PIDController(rightMotors, rightEncoder, encoderkP, encoderkI, encoderkD, encoderMin, encoderMax).setInputInverted(true);

            gLeft = new PIDController(leftMotors, adx, gyrokP, gyrokI, gyrokD, gyroMin, gyroMax);
            gRight = new PIDController(rightMotors, adx, gyrokP, gyrokI, gyrokD, gyroMin, gyroMax);
        }

        leftMotors.setLoggingChanges(true);
        rightMotors.setLoggingChanges(true);

        if (Robot.real) {
            gLeft.setFinishedTolerance(GYRO_PID_TOLERANCE);
            gRight.setFinishedTolerance(GYRO_PID_TOLERANCE);

            eLeft.setFinishedTolerance(ENCODER_ANGLE_TOLERANCE);
            eRight.setFinishedTolerance(ENCODER_ANGLE_TOLERANCE);
        }


        SmartDashboard.putNumber("Gyro kP", gyrokP);
        SmartDashboard.putNumber("Gyro kI", gyrokI);
        SmartDashboard.putNumber("Gyro kD", gyrokD);

    }

    @Override
    public void enabledInit() {
        shift(false);
    }

    @Override
    public void disabledInit() {}

    /**
     * Gets the MotorGroup for the gLeft drivetrain motors.
     *
     * @return The MotorGroup for the gLeft drivetrain motors.
     */
    public static MotorGroup getLeft() {
        return leftMotors;
    }

    /**
     * Gets the MotorGroup for the right drivetrain motors.
     *
     * @return The MotorGroup for the right drivetrain motors.
     */
    public static MotorGroup getRight() {
        return rightMotors;
    }

    /**
     * Gets the left drive Encoder.
     *
     * @return The gLeft drive Encoder.
     */
    public static AbstractEncoder getLeftEncoder() {
        return leftEncoder;
    }

    /**
     * Gets the right drive Encoder.
     *
     * @return The right drive Encoder.
     */
    public static AbstractEncoder getRightEncoder() {
        return rightEncoder;
    }

    /**
     * Gets the ADXSensor (Gyroscope and Accelerometer)
     *
     * @return The ADXSensor.
     */
    public static ADXSensor getADX() {
        return adx;
    }

    /**
     * Shifts into either high gear or low gear.
     *
     * @param high If the robot should shift into high gear.
     */
    public static void shift(boolean high) {
        shift.set(high); //TODO: Check if true is for high gear
    }

    /**
     * Checks if the Drive is currently controllable by the driver.
     *
     * @return If the Drive is currently controllable by the driver.
     */
    public static boolean isDriverControlled() {
        return driverControlled;
    }

    /**
     * Sets if the Drive can be controlled by the driver.
     *
     * @param b If the Drive can be controlled by the driver.
     */
    public static void setDriverControlled(boolean b) {
        driverControlled = b;
    }

    /**
     * Tank drive. Typically a driving style that uses two joysticks.
     *
     * @param l The speed of the gLeft motors.
     * @param r The speed of the right motors.
     */
    public static void tankDrive(double l, double r) {
        leftMotors.setPower(l);
        rightMotors.setPower(r);
    }

    /**
     * Arcade drive. Typically a driving style that uses one Joystick.
     *
     * @param x The X axis of the Joystick.
     * @param y The Y axis of the Joystick.
     */
    public static void arcadeDrive(double x, double y) {
        tankDrive(x - y, x + y);
    }

    /**
     * Makes the Robot drive a certain amount of inches.
     *
     * @param inches How many inches to drive.
     */
    public static void inchDrive(double inches) {
        encoderDrive(inchesToEncoder(inches));
    }

    /**
     * Makes the Robot drive a certain amount of inches, then return to it's starting angle.
     *
     * @param inches How many inches to drive.
     */
    public static void inchDriveGyro(double inches) {
        encoderDriveGyro(inchesToEncoder(inches));
    }

    /**
     * Makes the Robot drive a certain amount of encoder clicks, then return to it's starting angle.
     *
     * @param distance How many encoder clicks to drive.
     */
    public static void encoderDriveGyro(double distance) {
        adx.reset();
        encoderDrive(distance);
        gyroTurn(adx.getAngle()); //TODO: check sign
    }

    /**
     * Makes the Robot drive a certain amount of encoder clicks.
     *
     * @param distance How many encoder clicks to drive.
     */
    public static void encoderDrive(double distance) {
        Drive.shift(false);
        Log.t("EncoderDriving " + distance + " clicks");
        leftEncoder.reset();
        rightEncoder.reset();

        eLeft.setTarget(distance);
        eRight.setTarget(distance);
        eLeft.enable();
        eRight.enable();
        while (!eLeft.isDone() || !eRight.isDone()) {
            if(!Robot.isEnabled()) return;
            try {
                Thread.sleep(25);
            } catch (Exception e) {
                break;
            }
        }

        eLeft.disable();
        eRight.disable();
    }

    /**
     * Makes the robot turn a certain amount of degrees.
     *
     * @param degrees How many degrees to turn.
     * @return True.
     */
    public static boolean gyroTurn(double degrees) {
        return gyroTurn(degrees, -1);
    }

    /**
     * Makes the Robot turn a certain amount of degrees. TODO: Figure out 100% which way is right and left and make it match with the right and left on the gyro
     * TODO: The gyro WILL freak out when the Robot is jerked around when going over a defense. Don't rely on this to be accurate when crossing a bumpy defense.
     *
     * @param degrees How many degrees to turn.
     * @param timeout How long to wait before giving up on the gyro turn.
     * @return True is the turn was successful, false if the timeout was reached.
     */
    public static boolean gyroTurn(double degrees, double timeout) {
        Drive.setDriverControlled(false);
        Drive.shift(false);
        adx.reset();
        gLeft.setTarget(degrees);
        gRight.setTarget(degrees);

        Log.d("Enabling PIDs to turn " + degrees + " degrees");

        gLeft.enable();
        gRight.enable();

        boolean result;

        //Wait until both of the PIDs are done
        if (timeout == -1) {
            gLeft.waitUntilDone();
            gRight.waitUntilDone();
            result = true;
        } else {
            result = gLeft.waitUntilDone(timeout);
        }

        Log.d("Done turning " + degrees + " degrees");

        gLeft.disable();
        gRight.disable();
        Drive.setDriverControlled(true);

        return result;
    }

    /**
     * Converts inches to drive encoder clicks.
     *
     * @param inches The inches to be converted.
     * @return The encoder clicks equivalent to the inches provided.
     */
    private static double inchesToEncoder(double inches) {
        return inches * (9.6 * 1024) / (Math.PI * 9);
    }


    /**
     * Gets the highest current rate of rotation on the drive train.
     *
     * @return The highest current rate of rotation on the drive train.
     */
    private static double getHighestAbsoluteRate() {
        return Math.max(Math.abs(leftEncoder.getRate()), Math.abs(rightEncoder.getRate()));
    }

    @Override
    public void stop() {
        leftMotors.setPower(0);
        rightMotors.setPower(0);
    }

    @Override
    public List<Motor> getAllMotors() {
        return Arrays.asList(leftMotors, rightMotors);
    }
}