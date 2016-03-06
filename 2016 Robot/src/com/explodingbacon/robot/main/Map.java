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

    public static final int INTAKE_MOTOR = 15;

    public static final int CLIMBER_CABLE_WINCH_A = 13;
    public static final int CLIMBER_CABLE_WINCH_B = 14; //has encoder

    public static final int SHOOTER_INDEXER = 12;

    public static final int SHOOTER_MOTOR_1 = 10;
    public static final int SHOOTER_MOTOR_2 = 11; //has encoder

    //Relay

    public static final int SPOTLIGHT = 0;

    //PCM ports

    public static final int LIGHT = 0;

    public static final int SHIFT_SOLENOID_A = 2;
    public static final int SHIFT_SOLENOID_B = 5;

    public static final int INTAKE_SOLENOID_A = 4;
    public static final int INTAKE_SOLENOID_B = 1;

    public static final int MANTIS_1_A = 6;
    public static final int MANTIS_1_B = 3;

    public static final int MANTIS_2 = 7;

    //SENSORS

    public static final int RIGHT_DRIVE_ENCODER_A = 0;
    public static final int RIGHT_DRIVE_ENCODER_B = 1;

    public static final int LEFT_DRIVE_ENCODER_A = 3;
    public static final int LEFT_DRIVE_ENCODER_B = 2;

    public static final int SHOOTER_BALL_TOUCH = 4;

    public static final int CLIMBER_DOWN_TOUCH = 5;

    public static final int CLIMBER_SWITCH_RETRACTED = 6;
    public static final int CLIMBER_SWITCH_DEPLOYED = 7;
}
