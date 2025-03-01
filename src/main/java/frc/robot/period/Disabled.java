package frc.robot.period;

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
}
