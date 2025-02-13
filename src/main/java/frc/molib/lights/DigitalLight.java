package frc.molib.lights;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;

/**
 * Interface for controlling simple lights that can be controlled through a pneumatics module
 */
public class DigitalLight {
	private final Solenoid solController;
	
	/**
	 * Constructor using the default PCM ID
	 * @param moduleType The module type to use
	 * @param channel The channel on the PCM to control
	 */
	public DigitalLight(final PneumaticsModuleType moduleType, int channel) { solController = new Solenoid(moduleType, channel); }
	public void turnOn() { solController.set(true); }
	public void turnOff() { solController.set(false); }
	public void toggle() { solController.set(!solController.get()); }
}