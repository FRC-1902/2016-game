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

    public static final int INTAKE_MOTOR = 0x00;

    public static final int SHOOTER_MOTOR_1 = 0x01;
    public static final int SHOOTER_MOTOR_2= 0x02;

    public static final int CLIMBER_MOTOR_1 = 0x03;
    public static final int CLIMBER_MOTOR_2 = 0x04;

    public static final int SHOOTER_ROLLER = 0x05;

    //SOLENOIDS

    public static final int INTAKE_SOLENOID = 0;

    public static final int CLIMBER_SOLENOID = 1;

    //SENSORS

    public static final int LEFT_DRIVE_ENCODER_1 = 0;
    public static final int LEFT_DRIVE_ENCODER_2 = 1;
    public static final int RIGHT_DRIVE_ENCODER_1 = 2;
    public static final int RIGHT_DRIVE_ENCODER_2 = 3;


    public static final int CLIMBER_DOWN = 4;
    public static final int CLIMBER_UP = 5;
}
