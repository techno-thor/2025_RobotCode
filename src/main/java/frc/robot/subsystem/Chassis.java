package frc.robot.subsystem;

import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import frc.molib.Console;
import frc.molib.PIDController;
import frc.molib.dashboard.DashboardValue;
import frc.robot.Robot;

/** The Chassis Subsystem. Handles driving around the field. */
public class Chassis {
    //Network Tables
    private static final NetworkTable tblChassis = Robot.tblSubsystems.getSubTable("Chassis");
    private static final NetworkTable tblDrive_Angle_PID = tblChassis.getSubTable("Drive Angle PID");
    private static final NetworkTable tblDrive_Distance_PID = tblChassis.getSubTable("Drive Distance PID");

    //Dashboard Objects - Sensor Values
    private static final DashboardValue<Double> dshDrive_Angle = new DashboardValue<Double>(tblChassis, "Drive Angle");
    private static final DashboardValue<Double> dshDrive_Distance = new DashboardValue<Double>(tblChassis, "Drive Distance");

    //Dashboard Objects - Drive Angle PID Values
    private static final DashboardValue<Double> dshDrive_Angle_P = new DashboardValue<Double>(tblDrive_Angle_PID, "P Value");
    private static final DashboardValue<Double> dshDrive_Angle_I = new DashboardValue<Double>(tblDrive_Angle_PID, "I Value");
    private static final DashboardValue<Double> dshDrive_Angle_D = new DashboardValue<Double>(tblDrive_Angle_PID, "D Value");
    private static final DashboardValue<Boolean> dshDrive_Angle_OnTarget = new DashboardValue<Boolean>(tblDrive_Angle_PID, "On Target");

    //Dashboard Objects - Drive Distance PID Values
    private static final DashboardValue<Double> dshDrive_Distance_P = new DashboardValue<Double>(tblDrive_Distance_PID, "P Value");
    private static final DashboardValue<Double> dshDrive_Distance_I = new DashboardValue<Double>(tblDrive_Distance_PID, "I Value");
    private static final DashboardValue<Double> dshDrive_Distance_D = new DashboardValue<Double>(tblDrive_Distance_PID, "D Value");
    private static final DashboardValue<Boolean> dshDrive_Distance_OnTarget = new DashboardValue<Boolean>(tblDrive_Distance_PID, "On Target");

    //Motors
    private static final TalonFX mtrDrive_L1 = new TalonFX(1);
    private static final TalonFX mtrDrive_L2 = new TalonFX(2);
    private static final TalonFX mtrDrive_R1 = new TalonFX(3);
    private static final TalonFX mtrDrive_R2 = new TalonFX(4);

    //Sensors
    private static final ADXRS450_Gyro gyrDrive = new ADXRS450_Gyro();

    //PID Controllers
    private static final PIDController pidDrive_Angle = new PIDController(0.0, 0.0, 0.0);
    private static final PIDController pidDrive_Distance = new PIDController(0.0, 0.0, 0.0);

    //Constants
    private static final double GEAR_RATIO = 1.0 / 6.0;
    private static final double WHEEL_CIRCUMFERENCE = Math.PI * 4.0;
    
    //Power Buffer Variables
    private static double mDrivePower_Left = 0.0;
    private static double mDrivePower_Right = 0.0;

    /** Unused Constructor. */
    private Chassis() {}

    /** Call once at robot startup. */
    public static void init() {
        Console.printHeader("Initializing Chassis");

        Console.logMsg("Configuring Motors...");
        mtrDrive_L1.getConfigurator().apply(new MotorOutputConfigs().withInverted(InvertedValue.Clockwise_Positive));
        mtrDrive_L2.getConfigurator().apply(new MotorOutputConfigs().withInverted(InvertedValue.Clockwise_Positive));
        mtrDrive_R1.getConfigurator().apply(new MotorOutputConfigs().withInverted(InvertedValue.CounterClockwise_Positive));
        mtrDrive_R2.getConfigurator().apply(new MotorOutputConfigs().withInverted(InvertedValue.CounterClockwise_Positive));

        Console.logMsg("Configuring PIDs...");
        pidDrive_Angle.setTolerance(2.0);
        pidDrive_Distance.setTolerance(0.5);

        pidDrive_Angle.configOutputRange(-0.5, 0.5);
        pidDrive_Distance.configOutputRange(-0.5, 0.5);

        pidDrive_Angle.configAtSetpointTime(0.25);
        pidDrive_Distance.configAtSetpointTime(0.25);

        Console.logMsg("Calibrating Gyro...");
        gyrDrive.calibrate();

        Console.logMsg("Resetting Sensor values...");
        resetAngle();
        resetDistance();

        Console.logMsg("Initializing Dashboard values...");
        dshDrive_Angle_P.set(pidDrive_Angle.getP());
        dshDrive_Angle_I.set(pidDrive_Angle.getI());
        dshDrive_Angle_I.set(pidDrive_Angle.getI());

        dshDrive_Distance_P.set(pidDrive_Distance.getP());
        dshDrive_Distance_I.set(pidDrive_Distance.getI());
        dshDrive_Distance_I.set(pidDrive_Distance.getI());

        Console.logMsg("Chassis Initialization Complete!");
    }

    /** Call regularly to syncronize values between the robot and the Dashboard. */
    public static void syncDashboardValues() {
        //Push Sensor values
        dshDrive_Angle.set(getAngle());
        dshDrive_Distance.set(getDistance());

        //Update PID values
        pidDrive_Angle.setP(dshDrive_Angle_P.get());
        pidDrive_Angle.setI(dshDrive_Angle_I.get());
        pidDrive_Angle.setD(dshDrive_Angle_D.get());
        dshDrive_Angle_OnTarget.set(isAtAngle());
        
        pidDrive_Distance.setP(dshDrive_Distance_P.get());
        pidDrive_Distance.setI(dshDrive_Distance_I.get());
        pidDrive_Distance.setD(dshDrive_Distance_D.get());
        dshDrive_Distance_OnTarget.set(isAtDistance());
    }

    /** Disable the whole Subsystem. Disable all PID control, stop all motors. */
    public static void disable() {
        disable_PIDs();
        disable_Drive();
    }

    //TODO: Finish writing Chassis Comments
    public static void disable_PIDs() { disable_DriveAnglePID(); disable_DriveDistancePID();}
    public static void disable_DriveAnglePID() { pidDrive_Angle.disable(); }
    public static void disable_DriveDistancePID() { pidDrive_Distance.disable(); }

    public static void disable_Drive() { setDrivePower(0.0, 0.0); }

    public static double getAngle() { return gyrDrive.getAngle(); }
    public static double getDistance() { return mtrDrive_L1.getPosition().getValueAsDouble() * GEAR_RATIO * WHEEL_CIRCUMFERENCE; }

    public static void resetAngle() { gyrDrive.reset(); }
    public static void resetDistance() { mtrDrive_L1.setPosition(0.0); }

    /**
     * Change how the Drive motors behave when input is neutral or zero.
     * <p><i>Do not call frequently, it will slow down the system.</i></p>
     * @param mode Brake actively resists motion when neutral, Coast lets the Chassis roll freely
     */
    public static void setNeutralMode(NeutralModeValue mode) {
        mtrDrive_L1.setNeutralMode(mode);
        mtrDrive_L2.setNeutralMode(mode);
        mtrDrive_R1.setNeutralMode(mode);
        mtrDrive_R2.setNeutralMode(mode);
    }

    /**
     * Set the Drive motors into Brake mode which will actively resits movement when neutral.
     * <p><i>Do not call frequently, it will slow down the system.</i></p>
     */
    public static void enableBrakeMode() { setNeutralMode(NeutralModeValue.Brake); }

    /**
     * Set the Drive motors into Coast mode which will allow the Chassis to roll freely when neutral.
     * <p><i>Do not call frequently, it will slow down the system.</i></p>
     */
    public static void enableCoastMode() { setNeutralMode(NeutralModeValue.Coast); }

    /**
     * Apply power to each side of the Chassis independently.
     * @param leftPower Power to the left side of the Chassis
     * @param rightPower Power to the right side of the Chassis
     */
    public static void setDrivePower(double leftPower, double rightPower) {
        mDrivePower_Left = leftPower;
        mDrivePower_Right = rightPower;
    }

    /**
     * Enable the Drive Angle PID.
     * @param angle Target angle the Chassis will turn to
     */
    public static void goToAngle(double angle) {
        pidDrive_Angle.enable();
        pidDrive_Angle.setSetpoint(angle);
    }

    /**
     * Enable the Drive Distance PID.
     * @param distance  Target distance the Chassis will drive to
     */
    public static void goToDistance(double distance) {
        pidDrive_Distance.enable();
        pidDrive_Distance.setSetpoint(distance);
    }

    /**
     * Read sensor input to determine if the Chassis is at the desired angle.
     * @return True if the Drive Angle PID has been within tolerance for the required period
     */
    public static boolean isAtAngle() { return pidDrive_Angle.atSetpoint(); }

    /**
     * Read sensor input to determine if the Chassis is at the desired distance.
     * @return True if the Drive Distance PID has been within tolerance for the required period
     */
    public static boolean isAtDistance() { return pidDrive_Distance.atSetpoint(); }

    /** Call periodicallly to calculate PIDs and apply power to the motors. */
    public static void periodic() {
        //Calculate PID Controller input
        if(pidDrive_Angle.isEnabled()) {
            double pidPower = pidDrive_Angle.calculate(getAngle());
            setDrivePower(pidPower, -pidPower);
        } else if(pidDrive_Distance.isEnabled()) { 
            double pidPower = pidDrive_Distance.calculate(getDistance());
            setDrivePower(pidPower, pidPower);
        }

        //Apply power to motors
        mtrDrive_L1.set(mDrivePower_Left);
        mtrDrive_L2.set(mDrivePower_Left);
        mtrDrive_R1.set(mDrivePower_Right);
        mtrDrive_R2.set(mDrivePower_Right);
    }
}
