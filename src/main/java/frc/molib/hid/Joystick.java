package frc.molib.hid;

import edu.wpi.first.wpilibj.Joystick.AxisType;
import edu.wpi.first.wpilibj.Joystick.ButtonType;

/**
 * A re-write of {@link edu.wpi.first.wpilibj.Joystick} 
 * that provides additional functionality such as:
 * built-in deadzones on axes and the option to invert the Y-axis of the joysticks.
 * <p><i>Re-written because the getY() method was final.</i></p>
 */
public class Joystick extends GenericHID {
	private boolean mIsYAxisInverted = false;
	
	/**
	 * Construct an instance of a Joystick.
	 * @param port The port on the Driver Station that the controller is assigned to.
	 */
	public Joystick(int port) { super(port); }

    /**
	 * Configure whether the Y-Axis value should be inverted.
	 * @param isInverted A value of false means up is a negative value
	 */
	public void configYAxisInverted(boolean isInverted) { mIsYAxisInverted = isInverted; }

	/**
	 * Read the value of the axis with the deadzone applied.
	 *
	 * @param axis The axis to read, using the AxisType enumeration
	 * @return The value of the axis
	 */
	public double getRawAxis(edu.wpi.first.wpilibj.Joystick.AxisType axis) { 
		return getRawAxis(axis.value); 
	}

    /**
     * Read the Y axis value of this Joystick.
     * 
     * @return The axis value.
     */
    public double getY() { 
        if(mIsYAxisInverted) return -getRawAxis(AxisType.kY);
        else return getRawAxis(AxisType.kY);
    }

    /**
     * Read the X axis value of this Joystick.
     * 
     * @return The axis value.
     */
    public double getX() {
        return getRawAxis(AxisType.kX);
    }

    /**
     * Read the Twist axis value of this Joystick.
     * 
     * @return The axis value.
     */
    public double getTwist() {
        return getRawAxis(AxisType.kTwist);
    }

    /**
     * Read the throttle value of this Joystick.
     * 
     * @return The throttle value.
     */
    public double getThrottle() {
        return getRawAxis(AxisType.kThrottle);
    }

    /**
     * Read the state of the trigger of this Joystick
     * 
     * @return The state of the trigger.
     */
    public boolean getTrigger() {
        return getRawButton(ButtonType.kTrigger.value);
    }

    /**
     * Whether the trigger was pressed since the last check.
     */
    public boolean getTriggerPressed() {
        return getRawButtonPressed(ButtonType.kTrigger.value);
    }

    /**
     * Whether the trigger was released since the last check.
     */
    public boolean getTriggerReleased() {
        return getRawButtonReleased(ButtonType.kTrigger.value);
    }
}
