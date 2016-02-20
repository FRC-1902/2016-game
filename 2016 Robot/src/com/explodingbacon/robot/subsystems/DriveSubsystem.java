package com.explodingbacon.robot.subsystems;

import com.explodingbacon.bcnlib.actuators.DoubleSolenoid;
import com.explodingbacon.bcnlib.actuators.MotorGroup;
import com.explodingbacon.bcnlib.framework.PIDController;
import com.explodingbacon.bcnlib.framework.Subsystem;
import com.explodingbacon.bcnlib.sensors.ADXSensor;
import com.explodingbacon.bcnlib.sensors.AbstractEncoder;
import com.explodingbacon.bcnlib.sensors.Encoder;
import com.explodingbacon.robot.main.Map;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Talon;

public class DriveSubsystem extends Subsystem {

    private static MotorGroup leftMotors = (MotorGroup) new MotorGroup(Talon.class, Map.LEFT_DRIVE_1, Map.LEFT_DRIVE_2, Map.LEFT_DRIVE_3).setName("Left Drive");
    private static MotorGroup rightMotors = (MotorGroup) new MotorGroup(Talon.class, Map.RIGHT_DRIVE_1, Map.RIGHT_DRIVE_2, Map.RIGHT_DRIVE_3).setName("Right Drive");

    private static DoubleSolenoid shift = new DoubleSolenoid(Map.SHIFT_SOLENOID_A, Map.SHIFT_SOLENOID_B);

    private static AbstractEncoder leftEncoder = new Encoder(Map.LEFT_DRIVE_ENCODER_A, Map.LEFT_DRIVE_ENCODER_B);
    private static AbstractEncoder rightEncoder = new Encoder(Map.RIGHT_DRIVE_ENCODER_A, Map.RIGHT_DRIVE_ENCODER_B);

    private static ADXSensor adx = new ADXSensor(SPI.Port.kOnboardCS1, SPI.Port.kOnboardCS0);

    private static boolean driverControlled = true;

    private static double encoderkP = 0.13, encoderkI = 0, encoderkD = 0, encoderMin = 0.3, encoderMax = 0.5;

    private static double gyrokP = 0.01, gyrokI = 0.00025, gyrokD = 0, gyroMin = 0.2, gyroMax = 0.35;

    public static final double GYRO_ANGLE_ERROR_FIX = 3; //The minimum angle error needed before the Robot auto-corrects

    public static final double LOW_GEAR_SHIFT_RATE = 1000; //TODO: Change this to be about the rate of maximum low gear speed

    public DriveSubsystem() {
        super();
        leftMotors.setReversed(true);
        rightMotors.setReversed(true);

        shift(true);


        /*
        Talon t = (Talon) ((MotorGroup)leftMotors).getMotors().get(0).getInternalSpeedController();

        Log.d(getField(t, "loopTime"));
        Log.d(getField(t, "m_maxPwm"));
        Log.d(getField(t, "m_deadbandMaxPwm"));
        Log.d(getField(t, "m_centerPwm"));
        Log.d(getField(t, "m_deadbandMinPwm"));
        Log.d(getField(t, "m_minPwm"));*/

    }

    /*
    public static String getField(Object o, String field) {
        try {
            Field f = PWM.class.getDeclaredField(field);
            f.setAccessible(true);
            return f.get(o).toString();
        } catch (Exception e) {}
        return null;
    }*/

    /**
     * Gets the MotorGroup for the left drivetrain motors.
     * @return The MotorGroup for the left drivetrain motors.
     */
    public static MotorGroup getLeft() {
        return leftMotors;
    }

    /**
     * Gets the MotorGroup for the right drivetrain motors.
     * @return The MotorGroup for the right drivetrain motors.
     */
    public static MotorGroup getRight() {
        return rightMotors;
    }

    /**
     * Gets the left drive Encoder.
     * @return The left drive Encoder.
     */
    public static AbstractEncoder getLeftEncoder() {
        return leftEncoder;
    }

    /**
     * Gets the right drive Encoder.
     * @return The right drive Encoder.
     */
    public static AbstractEncoder getRightEncoder() {
        return rightEncoder;
    }

    /**
     * Gets the ADXSensor (Gyroscope and Accelerometer)
     * @return The ADXSensor.
     */
    public static ADXSensor getADX() {
        return adx;
    }

    /**
     * Shifts into either high gear or low gear.
     * @param high If the robot should shift into high gear.
     */
    public static void shift(boolean high) {
        shift.set(high); //TODO: Check if true is for high gear
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
        tankDrive(x - y, x + y);
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
    public static void encoderDrive(double distance) { //TODO: uncomment
        leftEncoder.reset();
        rightEncoder.reset();
        PIDController left = new PIDController(leftMotors, leftEncoder, encoderkP, encoderkI, encoderkD, encoderMin, encoderMax);
        PIDController right = new PIDController(rightMotors, rightEncoder, encoderkP, encoderkI, encoderkD, encoderMin, encoderMax).setInverted(true);
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
    public static void gyroTurn(double degrees) { //TODO: uncomment
        adx.reset();
        PIDController left = new PIDController(leftMotors, adx, gyrokP, gyrokI, gyrokD, gyroMin, gyroMax);
        PIDController right = new PIDController(rightMotors, adx, gyrokP, gyrokI, gyrokD, gyroMin, gyroMax).setInverted(true);
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

    private static double previousRate = 0;
    private static boolean resistanceShiftedToLow = false;

    /**
     * Makes the robot shift into low gear if it is encountering resistance to the point that it cannot move.
     * It will shift back into high gear once it reaches full speed while in low gear.
     */
    public static void shiftIfResistance() {
        double rate = getHighestAbsoluteRate();
        if (previousRate > 10) { //TODO: Decide what the "we were moving before" rate is
            if (rate < 5) { //TODO: Decide what "we are at a standstill" rate is
                shift(false);
                resistanceShiftedToLow = true;
            } else if (resistanceShiftedToLow && rate >= LOW_GEAR_SHIFT_RATE) {
                shift(true);
                resistanceShiftedToLow = false;
            }
        }
        previousRate = rate;
    }

    private static double getHighestAbsoluteRate() {
        return Math.max(Math.abs(leftEncoder.getRate()), Math.abs(rightEncoder.getRate()));
    }

    @Override
    public void stop() {
        leftMotors.setPower(0);
        rightMotors.setPower(0);
    }
}