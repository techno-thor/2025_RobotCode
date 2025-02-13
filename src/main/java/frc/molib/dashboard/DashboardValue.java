package frc.molib.dashboard;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;

/**
 * <p>Creates a typed entry in dashboard</p>
 * @param <DataType> Data type the entry is configured to hold.
 * 
 * @see edu.wpi.first.networktables.NetworkTableEntry
 * @see frc.molib.dashboard.DashTable
 */
@SuppressWarnings("unchecked")
public class DashboardValue<DataType> {
	private final NetworkTableEntry mEntry;

	/**
	 * Constructor
	 * @param parentTable	Parent NetworkTable
	 * @param key			Identifier key
	 */
	public DashboardValue(NetworkTable parentTable, String key) { mEntry = parentTable.getEntry(key); }
	
	/**
	 * Retrieves the entry's value
	 * @return Current stored value
	 */
	public DataType get() { return (DataType) mEntry.getValue().getValue(); }

	/**
	 * Set the value of the entry
	 * @param value New value
	 */
	public void set(DataType value) { mEntry.setValue(value); }

	/**
	 * Removes this entry from its parent DashTable
	 */
	public void delete() { mEntry.unpublish(); }
}
