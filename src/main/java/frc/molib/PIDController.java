package frc.molib;

import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.math.MathUtil;

/**
 * A simple class extending {@link edu.wpi.first.math.controller.PIDController}
 * that adds the ability to superficially enable/disable the controller, as well as clamp the output of the calculation.
 * <p><i>Upcoming: timer to ensure target is in range for an appropriate amount of time and not just passing through.</i></p>
 */
public class PIDController extends edu.wpi.first.math.controller.PIDController {
	private boolean mEnabled = false;
	private double mAtSetpointTime = 0.0;

	private double mMinOutputLimit = Double.NEGATIVE_INFINITY;
	private double mMaxOutputLimit = Double.POSITIVE_INFINITY;

	private Timer tmrAtSetpoint = new Timer();

	/**
	 * Allocates a PIDController with the given constants for Kp, Ki, and Kd 
	 * and a default period of 0.02 seconds.
	 * @param Kp The proportional coefficient
	 * @param Ki The integral coefficient
	 * @param Kd The derivative coefficient
	 */
	public PIDController(double Kp, double Ki, double Kd) { 
		super(Kp, Ki, Kd); 
		tmrAtSetpoint.start();
	}

	public boolean isEnabled() { return mEnabled; }
	public void enable() { mEnabled = true; }
	public void disable() { mEnabled = false; }

	/**
	 * Configure the time requirement for being on target.
	 * @param time Time requirement in seconds
	 */
	public void configAtSetpointTime(double time) {
		mAtSetpointTime = time;
	}

	/**
	 * Configure the range of the output from the PIDController
	 * @param min The minimum output value
	 * @param max The maximum output value
	 */
	public void configOutputRange(double min, double max) {
		mMinOutputLimit = min;
		mMaxOutputLimit = max;
	}

	/**
	 * Returns the next output of the PID controller, 
	 * clamped between the minimum and maximum outputs
	 *
	 * @param measurement The current measurement of the process variable.
	 * @return The next controller output.
	 */
	@Override 
	public double calculate(double measurement) { 
		return MathUtil.clamp(super.calculate(measurement), mMinOutputLimit, mMaxOutputLimit); 
	}

	/**
	 * Returns true if the error has been within the tolerance of the setpoint
	 * for the configured time requirement
	 *
	 * <p>This will return false until at least one input value has been computed.
	 *
	 * @return Whether the error is within the acceptable bounds.
	 */
	@Override
	public boolean atSetpoint() {
		if(!super.atSetpoint()) tmrAtSetpoint.reset();
		return super.atSetpoint() && (tmrAtSetpoint.get() >= mAtSetpointTime);
	}

	@Override 
	public void initSendable(SendableBuilder builder) {
		builder.setSmartDashboardType("MOLib PIDController");
		builder.setSafeState(() -> mEnabled = false);
		builder.addDoubleProperty("P", this::getP, this::setP);
		builder.addDoubleProperty("I", this::getI, this::setI);
		builder.addDoubleProperty("D", this::getD, this::setD);
		builder.addBooleanProperty("Enabled", this::isEnabled, value -> mEnabled = value);
		builder.addDoubleProperty("Setpoint", this::getSetpoint, this::setSetpoint);
		builder.addBooleanProperty("On Target", this::atSetpoint, null);
	}
}