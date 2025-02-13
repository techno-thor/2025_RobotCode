package frc.molib.dashboard;

/** The base to be used for any {@link frc.molib.dashboard.DashboardSelector} to ensure all necessary methods are implemented. */
public interface DashboardOptionBase {
    /**
     * <p>Gets the label of each option to be displayed in the Dashboard dropdown.</p>
     * <p><i>Must be overriden with whatever method is used to generate the label</i></p>
     * @return The label as it will appear in the Dashboard dropdown
     */
    public String getLabel();
}
