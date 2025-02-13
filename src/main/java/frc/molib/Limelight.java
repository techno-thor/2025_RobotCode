package frc.molib;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.molib.dashboard.DashboardValue;

/**
 * Interface for the Limelight table on NetworkTables
 */
public final class Limelight {
	public enum LEDMode {
		kDefault(0),
		kOff(1),
		kBlink(2),
		kOn(3);

		public final int value;
		private LEDMode(int value) { this.value = value; }
	}

	public enum CamMode {
		kVisionProcessor(0),
		kDriverCam(1);
		
		public final int value;
		private CamMode(int value) { this.value = value; }
	}

	public enum StreamMode {
		kStandard(0),
		kPrimaryPiP(1),
		kSecondaryPiP(2);

		public final int value;
		private StreamMode(int value){ this.value = value; }
	}
	
	private static final NetworkTable tblLimelight = NetworkTableInstance.getDefault().getTable("limelight");

	private static final DashboardValue<Double>		dshHasTarget = new DashboardValue<Double>(tblLimelight, "tv");
	private static final DashboardValue<Double> 	dshPosX = new DashboardValue<Double>(tblLimelight, "tx");
	private static final DashboardValue<Double> 	dshPosY = new DashboardValue<Double>(tblLimelight, "ty");
	private static final DashboardValue<Double> 	dshWidth = new DashboardValue<Double>(tblLimelight, "thor");
	private static final DashboardValue<Double> 	dshHeight = new DashboardValue<Double>(tblLimelight, "tver");
	private static final DashboardValue<Double> 	dshArea = new DashboardValue<Double>(tblLimelight, "ta");
	
	private static final DashboardValue<Integer>	dshLEDMode = new DashboardValue<Integer>(tblLimelight, "ledMode");
	private static final DashboardValue<Integer>	dshCamMode = new DashboardValue<Integer>(tblLimelight, "camMode");
	private static final DashboardValue<Integer>	dshPipeline = new DashboardValue<Integer>(tblLimelight, "pipeline");
	private static final DashboardValue<Integer>	dshStreamMode = new DashboardValue<Integer>(tblLimelight, "stream");

	private Limelight() {}

	public static boolean hasTarget() { return dshHasTarget.get() == 1; }
	public static double getPosX() { return dshPosX.get(); }
	public static double getPosY() { return dshPosY.get(); }
	public static double getWidth() { return dshWidth.get(); }
	public static double getHeight() { return dshHeight.get(); }
	public static double getArea() { return dshArea.get(); }
	
	public static void setLEDMode(LEDMode mode) { dshLEDMode.set(mode.value); }
	public static void setCamMode(CamMode mode) { dshCamMode.set(mode.value); }
	public static void setPipeline(int pipeline) { dshPipeline.set(pipeline); }
	public static void setStream(StreamMode mode) { dshStreamMode.set(mode.value); }

	public static LEDMode getLEDMode() { return LEDMode.values()[dshLEDMode.get()]; }
	public static CamMode getCamMode() { return CamMode.values()[dshCamMode.get()]; }
	public static int getPipeline() { return dshPipeline.get(); }
	public static StreamMode getStreamMode() { return StreamMode.values()[dshStreamMode.get()]; }	
}