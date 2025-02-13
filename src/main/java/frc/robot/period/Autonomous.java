package frc.robot.period;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import frc.molib.Console;
import frc.molib.dashboard.DashboardOptionBase;
import frc.molib.dashboard.DashboardSelector;
import frc.robot.Robot;
import frc.robot.subsystem.Chassis;
import frc.robot.subsystem.Loader;
import frc.robot.subsystem.Manipulator;

/** Controls the Robot through pre-programmed sequences during the Autonomous game period */
public class Autonomous {
    private enum StartingPosition implements DashboardOptionBase {
        LEFT("Left"),
        CENTER("Center"),
        RIGHT("Right");

        public static final StartingPosition DEFAULT = CENTER; 

        private final String LABEL;

        private StartingPosition(String label) { LABEL = label; }
        
        /** Returns the title of this selector. */
        public static String getTitle() { return "Starting Position"; }
        public String getLabel() { return LABEL; }
    }

    private enum StartingDelay implements DashboardOptionBase {
        NONE("-No Delay-", 0.0),
        ONE_SECOND("1 Second", 1.0),
        TWO_SECONDS("2 Seconds", 2.0),
        THREE_SECONDS("3 Seconds", 3.0),
        FIVE_SECONDS("5 Seconds", 5.0),
        TEN_SECONDS("10 Seconds", 10.0);

        public static final StartingDelay DEFAULT = NONE; 

        private final String LABEL;
        private final double DELAY;

        private StartingDelay(String label, double delay) { LABEL = label; DELAY = delay; }
        
        /** Returns the title of this selector. */
        public static String getTitle() { return "Starting Delay"; }
        public String getLabel() { return LABEL; }
        public double getTime() { return DELAY; }
    }

    private enum Sequence implements DashboardOptionBase {
        /** <i>Do absolutely nothing.</i> Do not move, do not score. Typically a last resort if enabling the robot may break it. */
        DO_NOTHING("-Do Nothing-"),
        /** Default Option. Just prepare for the match: reset systems, etc. but do not move or score. */
        PREPARE_FOR_MATCH("-Prepare for Match-") {
            @Override public void onEnable() {
                Console.logMsg("Zeroing Elevator...");
                Manipulator.lowerElevator();
            }},
        /** Drive just enough to get the most basic points. */
        JUST_DRIVE("Just Drive") {
            @Override public void periodic() {
                switch(mStage) {
                    case 0:
                        Console.logMsg("Zeroing Elevator. Delaying other actions...");
                        Manipulator.lowerElevator();
                        tmrStage.reset();
                        mStage++;
                    case 1:
                        if(tmrStage.get() >= mSelectedStartingDelay.getTime()) mStage++;
                        break;
                    case 2:
                        Console.logMsg("Driving forward 1ft...");
                        Chassis.goToDistance(12.0);
                        Chassis.resetDistance();
                        tmrStage.reset();
                        mStage++;
                    case 3:
                        if(Chassis.isAtDistance() || tmrStage.get() > 1.0) mStage++;
                        break;
                    case 4:
                        Console.logMsg((Chassis.isAtDistance() ? "Target reached" : "Stage Timed Out") + ". Stopping...");
                        Chassis.disable();
                        Console.logMsg("Sequence Complete.");
                        Console.printSeparator();
                        mStage++;
                    default:
                        Robot.disableSubsystems();
                }
            }};

        private static final Timer tmrStage = new Timer();
        public static final Sequence DEFAULT = PREPARE_FOR_MATCH; 

        private final String LABEL;
        private static int mStage = 0;

        private Sequence(String label) { LABEL = label; }
        
        /** Returns the title of this selector. */
        public static String getTitle() { return "Starting Position"; }
        public String getLabel() { return LABEL; }

        /** 
         * Call once at the start of Autonomous to prepare each Sequence. 
         * <p><i>Override if more actions are necessary at the start of the Sequence.</i></p>
         */
        public void onEnable() {
            tmrStage.restart();
            mStage = 0;
        }

        /** 
         * Call regularly to perform the pre-programmed sequence. 
         * <p><i>Override with specific steps of each sequence. Does nothing by default.</i></p>
         */
        public void periodic() {}
    }

    //Network Tables
    private static final NetworkTable tblAutonomous = Robot.tblControlPeriods.getSubTable("Autonomous");

    //Dashboard Object - Selectors
    private static final DashboardSelector<StartingPosition> dshStartingPosition = new DashboardSelector<StartingPosition>(tblAutonomous, StartingPosition.getTitle(), StartingPosition.DEFAULT);
    private static final DashboardSelector<StartingDelay> dshStartingDelay = new DashboardSelector<StartingDelay>(tblAutonomous, StartingDelay.getTitle(), StartingDelay.DEFAULT);
    private static final DashboardSelector<Sequence> dshSequence = new DashboardSelector<Sequence>(tblAutonomous, Sequence.getTitle(), Sequence.DEFAULT);

    //Buffer Variables for Dashboard Selectors
    private static StartingPosition mSelectedStartingPosition = StartingPosition.DEFAULT;
    private static StartingDelay mSelectedStartingDelay = StartingDelay.DEFAULT;
    private static Sequence mSelectedSequence = Sequence.DEFAULT;

    private static Alliance mAlliance;

    /** Unused Constructor */
    private Autonomous() {}

    /** Call once at Robot startup to initialize Dashboard objects. */
    public static void init() {
        Console.printHeader("Autonomous Initialization");
        
        Console.logMsg("Initializing Dashboard Selectors...");
        dshStartingPosition.init();
        dshStartingDelay.init();
        dshSequence.init();

        Console.logMsg("Autonomous Initialization Complete!");
    }

    /** Call once at the start of Autonomous to pull currently selected options from the Dashboard and prepare the selected Sequence. */
    public static void onEnable() {
        Console.printHeader("Autonomous Enabled");

        //Pull selected options
        mSelectedStartingPosition = dshStartingPosition.getSelected();
        mSelectedStartingDelay = dshStartingDelay.getSelected();
        mSelectedSequence = dshSequence.getSelected();

        try{
            mAlliance = DriverStation.getAlliance().orElseThrow();
        } finally {
            mAlliance = Alliance.Red;
        }

        //Log all selected options
        Console.logMsg("Starting Position: " + mSelectedStartingPosition.getLabel());
        Console.logMsg("Sequence Selected: " + mSelectedSequence.getLabel());
        Console.logMsg("Delay Time: " + mSelectedStartingDelay.getLabel());
        Console.logMsg("Alliance: " + mAlliance.toString());;
        Console.printSeparator();

        //Preemtively disable everything
        Robot.disableSubsystems();

        //Put the Chassis into Brake mode
        Chassis.enableBrakeMode();


        //OnEnable setup for selected Sequence
        mSelectedSequence.onEnable();

        Console.printSeparator();
    }

    /** Call regularly to perform the pre-programmed sequences. */
    public static void periodic() {
        mSelectedSequence.periodic();

        //Subsystem Updates
        Chassis.periodic();
        Loader.periodic();
        Manipulator.periodic();
    }
}
