package frc.molib.dashboard;

import java.util.Vector;

@SuppressWarnings({"rawtypes"})
public class DashboardManager {
    private static Vector<DashboardSelector> mSelectors = new Vector<DashboardSelector>();

    public static void addSelector(DashboardSelector selector) { mSelectors.add(selector); }

    public static void removeSelector(DashboardSelector selector) { mSelectors.remove(selector); }

    public static void initSelectors() { for(DashboardSelector dshTemp : mSelectors) dshTemp.init(); }

    public static void removeAll() { mSelectors.clear(); }

    public static void updateValues() {
        for(DashboardSelector dshTemp : mSelectors) dshTemp.update();
    }
}
