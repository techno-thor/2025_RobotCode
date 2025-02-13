package frc.molib.templates;

public interface ControlPeriodBase {
    /** Call once at Robot startup. */
    public void init();

    /** Call once at the start of the Control Period. */
    public void start();

    /** Call regularly while in this Control Period. */
    public void periodic();
}
