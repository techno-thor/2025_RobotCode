package frc.robot.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix6.configs.FeedbackConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.SoftwareLimitSwitchConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.networktables.NetworkTable;
import frc.molib.Console;
import frc.molib.PIDController;
import frc.molib.dashboard.DashboardValue;
import frc.molib.sensors.DigitalInput;
import frc.robot.Robot;

/** The Manipulator Subsystem. Handles scoring Coral game pieces on the Reef. */
public class Manipulator {
    /** Predetermined positions of the Elevator for scoring/reset. */
    public enum Position {
        BOTTOM(0.0),
        TROUGH(1.0),
        LEVEL2(10.0),
        LEVEL3(20.0),
        LEVEL4(30.0);

        private final double HEIGHT;
        private Position(double height) { HEIGHT = height; }
        public double getHeight() { return HEIGHT; }
    }

    //Network Tables
    private static final NetworkTable tblManipulator = Robot.tblSubsystems.getSubTable("Manipulator");
    private static final NetworkTable tblElevator_Height_PID = tblManipulator.getSubTable("Elevator Height PID");

    //Dashbaord Objects - Sesnor values
    private static final DashboardValue<Double> dshElevator_Height = new DashboardValue<Double>(tblManipulator, "Height");
    private static final DashboardValue<Boolean> dshElevator_AtTop = new DashboardValue<Boolean>(tblManipulator, "At Top");
    private static final DashboardValue<Boolean> dshElevator_AtBottom = new DashboardValue<Boolean>(tblManipulator, "At Bottom");
    private static final DashboardValue<Boolean> dshLoaded = new DashboardValue<Boolean>(tblManipulator, "Loaded");

    //Dashboard Objects - Elevator Height PID values
    private static final DashboardValue<Double> dshElevator_Height_P = new DashboardValue<Double>(tblElevator_Height_PID, "P Value");
    private static final DashboardValue<Double> dshElevator_Height_I = new DashboardValue<Double>(tblElevator_Height_PID, "I Value");
    private static final DashboardValue<Double> dshElevator_Height_D = new DashboardValue<Double>(tblElevator_Height_PID, "D Value");
    private static final DashboardValue<Boolean> dshElevator_Height_OnTarget = new DashboardValue<Boolean>(tblElevator_Height_PID, "On Target");

    //Motors
    private static final TalonFX mtrElevator = new TalonFX(6);
    private static final TalonSRX mtrOuttake = new TalonSRX(7);

    //Sensors
    private static DigitalInput phoElevator_T = new DigitalInput(0);
    private static DigitalInput phoElevator_B = new DigitalInput(1);
    private static DigitalInput phoLoaded = new DigitalInput(2);

    //PID Controllers
    private static final PIDController pidElevator_Height = new PIDController(0.0, 0.0, 0.0);

    //Constants
    private static final double ELEVATOR_GEAR_RATIO = 1.0;
    private static final double ELEVATOR_SPROCKET_CIRCUMFERENCE = 2.0 * Math.PI;

    //Power Buffer Variables
    private static double mElevatorPower = 0.0;
    private static double mOuttakePower = 0.0;

    /** Unused Constructor. */
    private Manipulator() {}

    /** Call once at Robot startup. */
    public static void init() {
        Console.printHeader("Manipulator Initialization");

        Console.logMsg("Configuring Motors...");
        mtrElevator.getConfigurator().apply(new TalonFXConfiguration()
            .withMotorOutput(new MotorOutputConfigs()
                .withInverted(InvertedValue.Clockwise_Positive)
                .withNeutralMode(NeutralModeValue.Brake))
            .withFeedback(new FeedbackConfigs()
                .withSensorToMechanismRatio(ELEVATOR_GEAR_RATIO * ELEVATOR_SPROCKET_CIRCUMFERENCE))
            .withSoftwareLimitSwitch(new SoftwareLimitSwitchConfigs()
                .withForwardSoftLimitThreshold(Position.LEVEL4.getHeight() + 0.25)
                .withForwardSoftLimitEnable(true)
                .withReverseSoftLimitThreshold(Position.BOTTOM.getHeight() - 0.25)
                .withReverseSoftLimitEnable(true)));

        mtrOuttake.setInverted(false);
        mtrOuttake.setNeutralMode(NeutralMode.Coast);

        Console.logMsg("Configuring PIDs...");
        pidElevator_Height.setTolerance(0.5);
        pidElevator_Height.configOutputRange(-0.4, 0.5);
        pidElevator_Height.configAtSetpointTime(0.25);

        Console.logMsg("Initializing Dashboard Values...");
        dshElevator_Height_P.set(pidElevator_Height.getP());
        dshElevator_Height_I.set(pidElevator_Height.getI());
        dshElevator_Height_D.set(pidElevator_Height.getD());

        Console.logMsg("Manipulator Initialization Complete!");
    }

    /** Call regularly to syncronize values between the robot and the Dashboard. */
    public static void syncDashboardValues() {
        //Push Sensor values
        dshElevator_Height.set(getHeight());
        dshElevator_AtTop.set(isAtTop());
        dshElevator_AtBottom.set(isAtBottom());
        dshLoaded.set(isLoaded());

        //Update PID values
        pidElevator_Height.setP(dshElevator_Height_P.get());
        pidElevator_Height.setI(dshElevator_Height_I.get());
        pidElevator_Height.setD(dshElevator_Height_D.get());
        dshElevator_Height_OnTarget.set(pidElevator_Height.atSetpoint());
    }

    /** Disable the whole Subsystem. Stop all motors. */
    public static void disable() {
        disable_PIDs();

        disable_Elevator();
        disable_Outtake();
    }

    //TODO: Finish writing Manipulator Comments
    public static void disable_PIDs() { disable_ElevatorPID(); }

    public static void disable_ElevatorPID() { pidElevator_Height.disable(); }

    public static void disable_Elevator() { setElevatorPower(0.0); }

    public static void disable_Outtake() { setOuttakePower(0.0); }

    public static double getHeight() { return mtrElevator.getPosition().getValueAsDouble(); }
    public static void resetHeight() { mtrElevator.setPosition(0.0); }

    public static boolean isAtTop() { return phoElevator_T.get(); }
    public static boolean isAtBottom() { return phoElevator_B.get(); }
    public static boolean isLoaded() { return phoLoaded.get(); }

    /**
     * Apply power to the Elevator motor(s)
     * @param power Percent Output power to be applied
     */
    public static void setElevatorPower(double power) {
        mElevatorPower = power;
    }

    public static void lowerElevator() { setElevatorPower(-0.10);}
    public static void raiseElevator() { setElevatorPower(0.15);}

    /**
     * Enable PID control of the Elevator.
     * @param height Target height the Elevator will move to
     */
    public static void goToHeight(double height) {
        pidElevator_Height.enable();
        pidElevator_Height.setSetpoint(height);
    }

    /**
     * Enable PID control of the Elevator.
     * @param position Predetermined target height the Elevator will move to
     */
    public static void goToPosition(Position position) { goToHeight(position.getHeight());}

    /**
     * Apply power to the Outtake motor(s)
     * @param power Percent Output power to be applied
     */
    public static void setOuttakePower(double power) { mOuttakePower = power; }
    /** Turn on the Outtake with a predetermined power value. */
    public static void enable_Outtake() { setOuttakePower(1.0); }
    /** Reverse the Outtake with a predetermined power value. */
    public static void reverse_Outtake() { setOuttakePower(-1.0); }

    /** Call periodicallly to calculate PIDs, ensure safety measures, and apply power to the motors. */
    public static void periodic() {
        //Calculate PID Controller input
        if(pidElevator_Height.isEnabled()) {
            mElevatorPower = pidElevator_Height.calculate(getHeight());
        }

        //Safety Measures
        if(isAtTop()) {
            mElevatorPower = MathUtil.clamp(mElevatorPower, -1.0, 0.0);
        }

        if(isAtBottom()) {
            mElevatorPower = MathUtil.clamp(mElevatorPower, 0.0, 1.0);
            resetHeight();
        }

        //Apply power to motors
        mtrElevator.set(mElevatorPower);
        mtrOuttake.set(ControlMode.PercentOutput, mOuttakePower);
    }
}
