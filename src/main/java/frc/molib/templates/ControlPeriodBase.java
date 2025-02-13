package frc.molib.templates;

/** 
 * A Template for all Control Period classes to ensure basic functionality. 
 * <p>A project for another day.</p>
 */
public interface ControlPeriodBase {
    /** Call once at Robot startup. */
    public void init();

    /** Call once at the start of the Control Period. */
    public void onEnable();

    /** Call regularly while in this Control Period. */
    public void periodic();
}
