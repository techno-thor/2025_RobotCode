package frc.molib;

import frc.molib.buttons.ButtonManager;
import frc.molib.dashboard.DashboardManager;

/** 
 * Handle the growing list of 'Manager' classes in MOLib
 * 
 * @see ButtonManager
 * @see DashboardManager
 */
public class Managers {
    private Managers() { throw new AssertionError("Utility Class"); }

    /**
     * Calls an update to all 'Manager' classes
     */
    public static void update() {
        ButtonManager.updateValues();
        DashboardManager.updateValues();
    }
}
