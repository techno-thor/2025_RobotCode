package frc.robot.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.networktables.NetworkTable;
import frc.molib.Console;
import frc.robot.Robot;

/** The Loader Subsystem. Handles intaking Coral game pieces from the Coral Station. */
public class Loader {
    //Network Tables
    @SuppressWarnings("unused")
    private static final NetworkTable tblLoader = Robot.tblSubsystems.getSubTable("Loader");

    //Motors
    private static final TalonSRX mtrIntake = new TalonSRX(5);

    //Power Buffer Variables
    private static double mIntakePower = 0.0;

    /** Unused Constructor. */
    private Loader() {}

    /** Call once at robot startup. */
    public static void init() {
        Console.printHeader("Loader Initialization");

        Console.logMsg("Configuring Motors...");
        mtrIntake.setInverted(false);
        mtrIntake.setNeutralMode(NeutralMode.Brake);

        Console.logMsg("Loader Initialization Complete!");
    }

    /** Call regularly to syncronize values between the robot and the Dashboard. */
    public static void syncDashboardValues() {}

    /** Disable the whole Subsystem. Stop all motors. */
    public static void disable() {
        disable_Intake();
    }

    /** Stop the Intake wheels. */
    public static void disable_Intake() { setIntakePower(0.0); }

    /**
     * Apply power to the Intake wheels.
     * @param power Percent Output power to be applied
     */
    public static void setIntakePower(double power) { mIntakePower = power; }
    /** Turn on the Intake with a predetermined power value. */
    public static void enable_Intake() { setIntakePower(1.0); }
    /** Reverse the Intake with a predetermined power value. */
    public static void reverse_Intake() { setIntakePower(-1.0); }

    /** Call periodicallly to apply power to the motors. */
    public static void periodic() {
        mtrIntake.set(ControlMode.PercentOutput, mIntakePower);
    }
}
