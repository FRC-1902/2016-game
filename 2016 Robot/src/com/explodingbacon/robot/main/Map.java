package com.explodingbacon.robot.main;

public class Map {

    //PWM

    public static final int RIGHT_DRIVE_1 = 0;
    public static final int RIGHT_DRIVE_2 = 1;
    public static final int RIGHT_DRIVE_3 = 2;
    public static final int LEFT_DRIVE_1 = 3;
    public static final int LEFT_DRIVE_2 = 4;
    public static final int LEFT_DRIVE_3 = 5;

    //CAN

    public static final int INTAKE_MOTOR = 0x04; //Final

    public static final int CLIMBER_MOTOR_1 = 0x02; //Final
    public static final int CLIMBER_MOTOR_2 = 0x06; //Final

    public static final int SHOOTER_INDEXER = 0x03; //Final

    public static final int SHOOTER_MOTOR_1 = 0x05; //Final
    public static final int SHOOTER_MOTOR_2 = 0x01; //Final

    //PCM ports

    public static final int LIGHT = 0;

    public static final int SHIFT_SOLENOID_A = 2;
    public static final int SHIFT_SOLENOID_B = 5;

    public static final int INTAKE_SOLENOID_A = 1;
    public static final int INTAKE_SOLENOID_B = 4;

    public static final int CLIMBER_SOLENOID_A = 3;
    public static final int CLIMBER_SOLENOID_B = 6;

    //SENSORS

    public static final int RIGHT_DRIVE_ENCODER_A = 0;
    public static final int RIGHT_DRIVE_ENCODER_B = 1;

    public static final int LEFT_DRIVE_ENCODER_A = 2;
    public static final int LEFT_DRIVE_ENCODER_B = 3;

    public static final int SHOOTER_BALL_TOUCH = 4;

    public static final int CLIMBER_ENCODER_A = 5;
    public static final int CLIMBER_ENCODER_B = 6;

    public static final int CLIMBER_DOWN_TOUCH = 7;
    public static final int CLIMBER_UP_TOUCH = 8;
}
