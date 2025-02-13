package frc.molib;

/**
 * Utility class for random extra needs
 */
public class Utility {
    private Utility() { throw new AssertionError("Utility Class"); }

    /**
	 * Adds a deadzone to an axis value where no input is read.
	 * Input is scaled from the deadzone on for a full range of motion.
	 * @param value 			Axis position
	 * @param deadzoneThreshold How far to deaden the center of the axis
	 * @return 					The adjusted axis value
	 */
	public static double deadenAxis(double value, double deadzoneThreshold) {
		if (Math.abs(value) < deadzoneThreshold)
			return 0.0;
		else if (value < 0.0)
			return (value + deadzoneThreshold) / (1.0 - deadzoneThreshold);
		else
			return (value - deadzoneThreshold) / (1.0 - deadzoneThreshold);
	}
}
