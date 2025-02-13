package frc.molib.templates;

/** 
 * A Template for all Subsystem classes to ensure basic functionality.
 * <p>A project for another day.</p>
 */
public interface SubsystemBase {
    /** Call once at Robot startup. */
    public void init();

    /** Call regularly to syncronize values between the robot and the Dashboard. */
    public void syncDashboardValues();

    /** Disable the whole Subsystem. Stop all motors. */
    public void disable();

    /** Call periodicallly to calculate PIDs, ensure safety measures, and apply power to motors. */
    public void periodic();
}
