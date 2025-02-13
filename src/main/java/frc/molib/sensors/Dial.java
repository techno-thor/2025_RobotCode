package frc.molib.sensors;

import edu.wpi.first.wpilibj.AnalogPotentiometer;

/**
 * An interface for a potentiometer. Can return distinct positions rather than a voltage or percentage
 */
public class Dial {
	private AnalogPotentiometer potSource;
	private final int mPositions;
	
	/**
	 * Constructor
	 * @param channel The analog channel this dial is plugged into
	 * @param maxPosition The number of positions the dial should detect
	 */
	public Dial(int channel, int positions) {
		potSource = new AnalogPotentiometer(channel);
		mPositions = positions;
	}
	
	/**
	 * Get the current positional reading of the potentiomenter
	 * @return Current position
	 */
	public int getPosition() {
		double currentVoltage = potSource.get();
		for (int position = 1; position <= mPositions; position++)
			if (currentVoltage <= ((1.0 / (double) (mPositions - 1)) * (double) (position - 0.5)))
				return position;
		return -1;
	}
}