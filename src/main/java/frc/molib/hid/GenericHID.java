package frc.molib.hid;

import frc.molib.Utility;

public class GenericHID extends edu.wpi.first.wpilibj.GenericHID {
    private double mDeadzoneThreshold = 0.1;
	
	/**
	 * Construct an instance of an Xbox Controller.
	 * @param port The port on the Driver Station that the controller is assigned to.
	 */
	public GenericHID(int port) { super(port); }
	
	/**
	 * Configure how much of the Stick range should be ignored to avoid accidental small inputs.
	 * <p><i>Values are then scaled to include full useable range of -1.0 to 1.0</i><p>
	 * @param value	Stick axis range to be ignored [Absolute Value]
	 */
	public void configDeadzoneThreshold(double value) { mDeadzoneThreshold = Math.abs(value); }

	/**
	 * Enable the rumble function of the controller.
	 * @param value Power of the vibration
	 */
	public void setRumble(double value) { setRumble(value, value); }
	
	/**
	 * Enable the rumble function to each side of the controller.
	 * @param leftValue 	Power of the left vibration
	 * @param rightValue	Power of the right vibration
	 */
	public void setRumble(double leftValue, double rightValue) {
		super.setRumble(RumbleType.kLeftRumble, leftValue);
		super.setRumble(RumbleType.kRightRumble, rightValue);
	}

	/**
   * Get the value of the axis with the deadzone applied.
   *
   * @param axis The axis to read, starting at 0.
   * @return The value of the axis.
   */
	@Override
	public double getRawAxis(int axis) { 
		return Utility.deadenAxis(super.getRawAxis(axis), mDeadzoneThreshold); 
	}
}
