package frc.robot.period;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.networktables.NetworkTable;
import frc.molib.Console;
import frc.molib.buttons.Button;
import frc.molib.buttons.ButtonManager;
import frc.molib.dashboard.DashboardOptionBase;
import frc.molib.dashboard.DashboardSelector;
import frc.molib.hid.XboxController;
import frc.robot.Robot;
import frc.robot.subsystem.Chassis;
import frc.robot.subsystem.Loader;
import frc.robot.subsystem.Manipulator;
import frc.robot.subsystem.Manipulator.Position;

/** Reads Driver/Operator inputs to control the Robot during the Teleoperated game period. */
public class Teleoperated {
    /** The control style used to drive the Robot. */
    private enum DriveStyle implements DashboardOptionBase {
        /** Uses the left joystick up/down for throttle, left/right for steering. */
        ARCADE("Arcade Drive"),
        /** Uses the left joystick up/down for throttle, right joystick left/right for steering. */
        CHEEZY("Cheezy Drive"),
        /** Uses the left joystick up/down to control the left Drive motors, the right joystick up/down to control the right Drive motors. */
        TANK("Tank Drive");

        public static final DriveStyle DEFAULT = TANK; 

        private final String LABEL;

        private DriveStyle(String label) { LABEL = label; }
        
        /** Returns the title of this selector. */
        public static String getTitle() { return "Drive Style"; }
        public String getLabel() { return LABEL; }
    }

    /** Various speed scales for different skill levels. */
    private enum DriveSpeed implements DashboardOptionBase {
        /** Extremely slow. Used mostly for testing uncertain code. */
        TORTOISE("Tortoise Speed", 0.15, 0.10),
        /** For beginners driving the Robot for the first time. */
        SLOW("Slow Speed", 0.25, 0.15),
        /** Standard driver speed. */
        MEDIUM("Medium Speed", 0.50, 0.20),
        /** For more experienced drivers. */
        FAST("Fast Speed", 0.75, 0.30),
        /** Full power! <i>Please use with caution.</i> */
        TURBO("Turbo Speed", 1.00, 0.50);

        public static final DriveSpeed DEFAULT = SLOW;

        private final String LABEL;
        private final double STANDARD_POWER;
        private final double PRECISION_POWER;

        private DriveSpeed(String label, double standardPower, double precisionPower) {
            LABEL = label + " [" + (standardPower * 100.0) + "%]";
            STANDARD_POWER = standardPower;
            PRECISION_POWER = precisionPower;
        }

        /** Returns the title of this selector. */
        public static String getTitle() { return "Drive Style"; }

        public String getLabel() { return LABEL; }
        public double getStandardPower() { return STANDARD_POWER; }
        public double getPrecisionPower() { return PRECISION_POWER; }
    }

    /** Methods for ramping driver inputs. */
    private enum DriveRamping implements DashboardOptionBase {
        /** Linear input, direct from joystick to Chassis. */
        NONE("No Ramping"),
        /** Inputs are squared for more precise movements at low speeds. */
        SQUARED("Squared Input"),
        /** Inputs are cubed for the most precision at low speeds. */
        CUBED("Cubed Input");

        public static final DriveRamping DEFAULT = NONE;

        private final String LABEL;

        private DriveRamping(String label) { LABEL = label; }

        /** Returns the title of this selector. */
        public static String getTitle() { return "Input Ramping"; }
        public String getLabel() { return LABEL; }
    }

    //Network Tables
    private static final NetworkTable tblTeleoperated = Robot.tblControlPeriods.getSubTable("Teleoperated");

    //Dashboard Objects
    private static final DashboardSelector<DriveStyle> dshDriveStyle = new DashboardSelector<DriveStyle>(tblTeleoperated, DriveStyle.getTitle(), DriveStyle.DEFAULT);
    private static final DashboardSelector<DriveSpeed> dshDriveSpeed = new DashboardSelector<DriveSpeed>(tblTeleoperated, DriveSpeed.getTitle(), DriveSpeed.DEFAULT);
    private static final DashboardSelector<DriveRamping> dshDriveRamping = new DashboardSelector<Teleoperated.DriveRamping>(tblTeleoperated, DriveRamping.getTitle(), DriveRamping.DEFAULT);

    //Buffer Variables for Dashboard Selectors
    private static DriveStyle mSelectedDriveStyle = DriveStyle.DEFAULT;
    private static DriveSpeed mSelectedDriveSpeed = DriveSpeed.DEFAULT;
    private static DriveRamping mSelectedDriveRamping = DriveRamping.DEFAULT;

    //Driver Controllers
    private static final XboxController ctlDriver = new XboxController(0);
    private static final XboxController ctlOperator = new XboxController(1);

    //Driver Buttons
    private static final Button btnDriver_Precision = new Button() { public boolean get() { return ctlDriver.getRightBumperButton(); }};
    private static final Button btnDriver_Score = new Button() { public boolean get() { return ctlDriver.getAButton(); }};

    //Operator Buttons
    private static final Button btnOperator_Intake = new Button() { public boolean get() { return ctlOperator.getLeftBumperButton(); }};
    private static final Button btnOperator_Elevator_ManualUp = new Button() { public boolean get() { return ctlOperator.getPOV() == 0; }};
    private static final Button btnOperator_Elevator_ManualDown = new Button() { public boolean get() { return ctlOperator.getPOV() == 180; }};
    private static final Button btnOperator_Elevator_Bottom = new Button() { public boolean get() { return ctlOperator.getAButton(); }};
    private static final Button btnOperator_Elevator_Trough = new Button() { public boolean get() { return ctlOperator.getBButton(); }};
    private static final Button btnOperator_Elevator_Level2 = new Button() { public boolean get() { return ctlOperator.getXButton(); }};
    private static final Button btnOperator_Elevator_Level3 = new Button() { public boolean get() { return ctlOperator.getYButton(); }};
    
    /** Unused Constructor */
    private Teleoperated() {}

    /** Call once at Robot startup to initialize Dashboard objects. */
    public static void init() {
        Console.printHeader("Teleoperated Initialization");

        Console.logMsg("Initializing Dashboard Selectors...");
        dshDriveStyle.init();
        dshDriveSpeed.init();
        dshDriveRamping.init();

        Console.logMsg("Teleoperated Initialization Complete!");
    }

    /** Call once at the start of Teleoperated to pull currently selected options from the Dashboard. */
    public static void onEnable() {
        Console.printHeader("Teleoperated Enabled");
        
        //Pull Selector values
        mSelectedDriveStyle = dshDriveStyle.getSelected();
        mSelectedDriveSpeed = dshDriveSpeed.getSelected();
        mSelectedDriveRamping = dshDriveRamping.getSelected();

        //Clear ButtonPresses
        ButtonManager.clearFlags();
    }

    /**
     *  Take raw Driver input to calculate real driving power.
     * @param inputValue Raw Joystick values
     * @param scale Current power scaling value
     * @return Processed power value
     */
    private static double processDriveInput(double inputValue, double scale) {
        if(mSelectedDriveRamping == DriveRamping.SQUARED) 
            inputValue = Math.pow(inputValue, 2.0) * Math.signum(inputValue);
        else if(mSelectedDriveRamping == DriveRamping.SQUARED)
            inputValue = Math.pow(inputValue, 3.0);
        
        return inputValue * scale;
    }

    /**
     * Apply power to the Drive motors, controlling them together via a throttle and steering input.
     * 
     * @param throttleValue Raw forward/reverse input to the Chassis
     * @param steeringValue Raw left/right input to the Chassis
     * @param scale Current power scaling value
     */
    private static void setArcadeDrive(double throttleValue, double steeringValue, double scale) {
        throttleValue = processDriveInput(throttleValue, scale);
        steeringValue = processDriveInput(steeringValue, scale);
        Chassis.setDrivePower(MathUtil.clamp(throttleValue + steeringValue, -scale, scale), MathUtil.clamp(throttleValue - steeringValue, -scale, scale));
    }

    /**
     * Apply power to the Drive motors, controlling the left and right side independently.
     * 
     * @param leftPower Raw power input to the left side of the Chassis
     * @param rightPower Raw power input to the right side of the Chassis
     * @param scale Current power scaling value
     */
    private static void setTankDrive(double leftPower, double rightPower, double scale) {
        leftPower = processDriveInput(leftPower, scale);
        rightPower = processDriveInput(rightPower, scale);
        Chassis.setDrivePower(leftPower, rightPower);
    }

    /** Call regularly to read Driver/Operator inputs and control the various Subsystems of the Robot. */
    public static void periodic() {
        //Chassis Driving
        double drivePowerScale = btnDriver_Precision.get() ? mSelectedDriveSpeed.getPrecisionPower() : mSelectedDriveSpeed.getStandardPower();
        
        if(mSelectedDriveStyle == DriveStyle.ARCADE)
            setArcadeDrive(ctlDriver.getLeftY(), ctlDriver.getLeftX(), drivePowerScale);
        else if(mSelectedDriveStyle == DriveStyle.CHEEZY)
            setArcadeDrive(ctlDriver.getLeftY(), ctlDriver.getRightX(), drivePowerScale);
        else if(mSelectedDriveStyle == DriveStyle.TANK)
            setTankDrive(ctlDriver.getLeftY(), ctlDriver.getRightY(), drivePowerScale);

        //Brake mode
        if(btnDriver_Precision.getPressed())
            Chassis.enableBrakeMode();

        if(btnDriver_Precision.getReleased())
            Chassis.enableCoastMode();

        //Loader Control
        if(btnOperator_Intake.get() && Manipulator.isAtBottom() && !Manipulator.isLoaded()) {
            Loader.enable_Intake();
            Manipulator.enable_Outtake();
        } else {
            Loader.disable_Intake();
            Manipulator.disable_Outtake();
        }

        //Manual Elevator Control
        if(btnOperator_Elevator_ManualUp.get()) {
            Manipulator.disable_ElevatorPID();
            Manipulator.raiseElevator();
        } else if(btnOperator_Elevator_ManualDown.get()) {
            Manipulator.disable_ElevatorPID();
            Manipulator.lowerElevator();
        } else {
            Manipulator.disable_Elevator();
        }

        //Automated Elevator Control
        if(btnOperator_Elevator_Bottom.getPressed())
            Manipulator.goToPosition(Position.BOTTOM);
        if(btnOperator_Elevator_Trough.getPressed())
            Manipulator.goToPosition(Position.TROUGH);
        if(btnOperator_Elevator_Level2.getPressed())
            Manipulator.goToPosition(Position.LEVEL2);
        if(btnOperator_Elevator_Level3.getPressed())
            Manipulator.goToPosition(Position.LEVEL3);

        //Manipulator Scoring
        if(btnDriver_Score.get())
            Manipulator.enable_Outtake();
        else   
            Manipulator.disable_Outtake();

        //Subsystem Updates
        Chassis.periodic();
        Loader.periodic();
        Manipulator.periodic();
    }
}
