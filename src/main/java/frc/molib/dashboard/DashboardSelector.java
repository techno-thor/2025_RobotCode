package frc.molib.dashboard;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilderImpl;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

/**
 * <p>Creates an option selector in dashboard</p>
 * 
 * @param <OptionEnum> Enumeration tied to the list of options.
 */
public class DashboardSelector<OptionEnum extends Enum<OptionEnum> & DashboardOptionBase> {

	private final NetworkTable TABLE;
	private final String KEY;
	private final OptionEnum DEFAULT_OPTION;

	private final SendableChooser<OptionEnum> chsSendable = new SendableChooser<OptionEnum>();

	/**
	 * Constructor
	 * @param parentTable 	Parent NetworkTable
	 * @param key			Identifier key
	 * @param defaultOption	Default selected option
	 */
	public DashboardSelector(NetworkTable parentTable, String key, OptionEnum defaultOption) {
		TABLE = parentTable;
		KEY = key;
		DEFAULT_OPTION = defaultOption;

		for(OptionEnum enumValue : defaultOption.getDeclaringClass().getEnumConstants())
			chsSendable.addOption(enumValue.getLabel(), enumValue);
		chsSendable.setDefaultOption(defaultOption.getLabel(), defaultOption);

		DashboardManager.addSelector(this);
	}

	/**
	 * Must be run at the start, <i>but after NetworkTables has connected,</i> for it to appear in NetworkTables
	 */
	public void init() {
		NetworkTable tblData = TABLE.getSubTable(KEY);
		SendableBuilderImpl builder = new SendableBuilderImpl();
		builder.setTable(tblData);
		SendableRegistry.publish(chsSendable, builder);
		builder.startListeners();
		tblData.getEntry(".name").setString(KEY);
	}

	/**
	 * Retrieves the currently selected option in dashboard.
	 * @return Selected object. If one is not selected, returns the default option
	 */
	public OptionEnum getSelected() { 
		if(chsSendable.getSelected() == null)
			return DEFAULT_OPTION;
		return chsSendable.getSelected(); 
	}

	public void update() {
		SendableRegistry.update(chsSendable);
	}
}
