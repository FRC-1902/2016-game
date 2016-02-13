package com.explodingbacon.robot.subsystems;

import com.explodingbacon.bcnlib.actuators.FakeMotor;
import com.explodingbacon.bcnlib.actuators.Motor;
import com.explodingbacon.bcnlib.actuators.MotorGroup;
import com.explodingbacon.bcnlib.framework.PIDController;
import com.explodingbacon.bcnlib.framework.Subsystem;
import com.explodingbacon.bcnlib.sensors.EncoderInterface;
import com.explodingbacon.bcnlib.sensors.MotorEncoder;
import com.explodingbacon.robot.main.Map;
import edu.wpi.first.wpilibj.CANTalon;

public class ShooterSubsystem extends Subsystem {

    //TODO: Make outRoller an actual motor
    private static Motor shooter = new MotorGroup(CANTalon.class, Map.SHOOTER_MOTOR_1, Map.SHOOTER_MOTOR_2).setName("Shooter");
    private static Motor outRoller = /*new Motor(CANTalon.class, Map.SHOOTER_ROLLER).setName("Shooter Roller")*/
            new FakeMotor().setName("Shooter Roller");

    private static MotorEncoder encoder;
    public static PIDController shooterPID;
    private static double kP = 1; //TODO: tune

    private static boolean shouldShoot = false;

    public static final double DEFAULT_SHOOT_RATE = 2000; //TODO: Tune

    public ShooterSubsystem() {
        super();

        encoder = ((MotorGroup)shooter).getMotors().get(0).getEncoder();
        if (encoder == null) {
            System.out.println("Encoder is null! oh no!");
        }

        System.out.println("test");
        encoder.setPidMode(EncoderInterface.RATE);
        shooterPID = new PIDController(shooter, encoder, kP, 0, 0);
        outRoller.setReversed(true);
    }

    /**
     * Tells the Shooter to shoot the boulder.
     */
    public static void shoot() {
        setShouldShoot(true);
    }

    /**
     * Waits until the Shooter shoots the boulder.
     */
    public static void waitForShoot() {
        while (!shouldShoot) {
            try {
                Thread.sleep(25);
            } catch (Exception e) {}
        }
    }

    /**
     * Checks if the Shooter has been told to shoot.
     * @return If the Shooter has been told to shoot.
     */
    public static boolean shouldShoot() { return shouldShoot; }

    /**
     * Sets if the Shooter should shoot the boulder.
     * @param b If the Shooter should shoot the boulder.
     */
    public static void setShouldShoot(boolean b) { shouldShoot = b; }

    /**
     * Sets the speed of the Shooter.
     * @param d The speed of the Shooter.
     */
    public static void setShooter(double d) { shooter.setPower(d); }

    /**
     * Calculates the spin rate required to launch a boulder into a goal that is "distance" away.
     * @param distance How far away the goal is.
     * @return The spin rate required to launch a boulder into a goal that is "distance" away.
     */
    public static double calculateShooterRate(double distance) {
        return DEFAULT_SHOOT_RATE; //TODO: Actually calculate the needed shooter rate
    }

    /**
     * Checks if the current rate of the shooter is acceptable for shooting.
     * @return If the current rate of the shooter is acceptable for shooting.
     */
    public static boolean isRateAcceptable() { //TODO: better name?
        return encoder.getRate() > (shooterPID.getTarget() * 0.75); //TODO: tune
    }

    /**
     * Sets the speed of the Shooter roller. The roller is used to move the ball into the Shooter wheels.
     * @param d The speed of the Shooter roller.
     */
    public static void setRoller(double d) {
        outRoller.setPower(d);
    }

    /**
     * Gets the Encoder on the Shooter.
     * @return Yhe Encoder on the Shooter.
     */
    public static MotorEncoder getEncoder() {
        return encoder;
    }

    @Override
    public void stop() {
        shooterPID.disable();
        shooter.setPower(0);
        outRoller.setPower(0);
    }
}