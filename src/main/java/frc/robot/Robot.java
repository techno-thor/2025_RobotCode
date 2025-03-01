// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import frc.molib.Console;
import frc.molib.Managers;
import frc.robot.period.Autonomous;
import frc.robot.period.Disabled;
import frc.robot.period.Teleoperated;
import frc.robot.period.Test;
import frc.robot.subsystem.Chassis;
import frc.robot.subsystem.Loader;
import frc.robot.subsystem.Manipulator;

/**
 * The methods in this class are called automatically corresponding to each mode, as described in
 * the TimedRobot documentation. If you change the name of this class or the package after creating
 * this project, you must also update the Main.java file in the project.
 */
public class Robot extends TimedRobot {
    //Network Tables
    public static final NetworkTable tblMain = NetworkTableInstance.getDefault().getTable("Big MO");
    public static final NetworkTable tblControlPeriods = tblMain.getSubTable("Control Periods");
    public static final NetworkTable tblSubsystems = tblMain.getSubTable("Subsystems");

    //Driver Camera
    private static UsbCamera camDriver;

    /** Disable all Subsystems; Stop all movement. */
    public static void disableSubsystems() {
        Chassis.disable();
        Loader.disable();
        Manipulator.disable();
    }

    @Override public void robotInit() {
        Console.printHeader("Robot Initialization");

        Console.logMsg("Waiting for NetworkTables Connection...");
        Timer tmrNetworkTable = new Timer();
        tmrNetworkTable.restart();
        while(!NetworkTableInstance.getDefault().isConnected() && tmrNetworkTable.get() < 15.0);
        if(!NetworkTableInstance.getDefault().isConnected())
            Console.logErr("NetworkTables failed to connect! Dashboard objects may not work as intended!");

        //Try to start the Driver Camera
        try {
            Console.logMsg("Initializing Driver Camera...");
            camDriver = CameraServer.startAutomaticCapture("Driver Camera", 0);
            camDriver.setFPS(15);
            camDriver.setResolution(128, 80);
            camDriver.setBrightness(50);
        } finally {
            Console.logErr("Camera not found!");
        }

        //Initialize Control Periods
        Test.init();
        Autonomous.init();
        Teleoperated.init();
        Disabled.init();

        //Initialize Subsystems
        Chassis.init();
        Loader.init();
        Manipulator.init();

        Console.printSeparator();
        Console.logMsg("Robot Initialization Complete!");
    }

    @Override public void robotPeriodic() {
        Managers.update();

        Chassis.syncDashboardValues();
        Loader.syncDashboardValues();
        Manipulator.syncDashboardValues();
    }

    @Override public void testInit() { Test.start(); }
    @Override public void testPeriodic() { Test.periodic(); }

    @Override public void autonomousInit() { Autonomous.start(); }
    @Override public void autonomousPeriodic() { Autonomous.periodic(); }

    @Override public void teleopInit() { Teleoperated.start(); }
    @Override public void teleopPeriodic() { Teleoperated.periodic(); }

    @Override public void disabledInit() { Disabled.start(); }
    @Override public void disabledPeriodic() { Disabled.periodic(); }
}
