package frc.robot.period;

import frc.molib.buttons.ButtonManager;
import frc.robot.Robot;
import frc.robot.subsystem.Chassis;

public class Disabled {
    private Disabled() {}

    public static void init() {}

    public static void start() {
        Robot.disableSubsystems();
        Chassis.enableCoastMode();
    }
    
    public static void periodic() {}

    public static void end() {
        ButtonManager.clearFlags();
    }
}
