package frc.molib.sensors;

public class DigitalInput extends edu.wpi.first.wpilibj.DigitalInput {
    private boolean mIsInverted = false;

    public DigitalInput(int channel) { this(channel, false); }
    public DigitalInput(int channel, boolean isInverted) {
        super(channel);   
        mIsInverted = isInverted;
    }

    public void configInverted(boolean isInverted) { mIsInverted = isInverted; }

    @Override
    public boolean get() {
        return mIsInverted ? !super.get() : super.get();
    }
}
