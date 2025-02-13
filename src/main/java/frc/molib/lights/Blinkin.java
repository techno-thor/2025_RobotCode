package frc.molib.lights;

import edu.wpi.first.wpilibj.motorcontrol.Spark;

/**
 * An interface to the Blinkin LED Driver
 */
public class Blinkin {
	private Spark mtrController;

	/**
	 * Constructor
	 * @param channel The PWM channel that the Blinkin is attached to.
	 */ 
	public Blinkin(int channel) {
		this.mtrController = new Spark(channel);
		this.turnOff();
	}

	/**
	 * Change which mode the LEDs are set to.
	 * @param mode The equivalent motor value associated with each mode.
	 */
	public void setMode(double mode) { this.mtrController.set(mode); }

	/**
	 * Disable LEDs
	 */
	public void turnOff() { setMode(mode.solid.BLACK); }
	
	public static class mode {
		public static class pattern {
			public static class fixedPalette {
				public static class rainbow {
					public static final double	FULL_COLOR					=-0.99;
					public static final double	PARTY						=-0.97;
					public static final double	OCEAN						=-0.95;
					public static final double	LAVA						=-0.93;
					public static final double	FOREST						=-0.91;
					public static final double	GLITTER						=-0.89;
				}
					public static final double	CONFETTI					=-0.87;
				public static class shot {
					public static final double	RED							=-0.85;
					public static final double	BLUE						=-0.83;
					public static final double	WHITE						=-0.81;
				}
				public static class sinelon {
					public static final double RAINBOW						=-0.79;
					public static final double PARTY						=-0.77;
					public static final double OCEAN						=-0.75;
					public static final double LAVA							=-0.73;
					public static final double FOREST						=-0.71;
				}
				public static class beatsPerMinute {
					public static final double RAINBOW						=-0.69;
					public static final double PARTY						=-0.67;
					public static final double OCEAN						=-0.65;
					public static final double LAVA							=-0.63;
					public static final double FOREST						=-0.61;
				}
				public static class fire {
					public static final double MEDIUM						=-0.59;
					public static final double LARGE						=-0.57;
				}
				public static class twinkles {
					public static final double RAINBOW						=-0.55;
					public static final double PARTY						=-0.53;
					public static final double OCEAN						=-0.51;
					public static final double LAVA							=-0.49;
					public static final double FOREST						=-0.47;
				}
				public static class colorWaves {
					public static final double RAINBOW						=-0.45;
					public static final double PARTY						=-0.43;
					public static final double OCEAN						=-0.41;
					public static final double LAVA							=-0.39;
					public static final double FOREST						=-0.37;
				}
				public static class larsonScanner {
					public static final double RED							=-0.35;
					public static final double GRAY							=-0.33;
				}
				public static class lightChase {
					public static final double RED							=-0.31;
					public static final double BLUE							=-0.29;
					public static final double GRAY							=-0.27;
				}
				public static class heartBeat {
					public static final double RED							=-0.25;
					public static final double BLUE							=-0.23;
					public static final double WHITE						=-0.21;
					public static final double GRAY							=-0.19;
				}
				public static class breath {
					public static final double RED							=-0.17;
					public static final double BLUE							=-0.15;
					public static final double GRAY							=-0.13;
				}
				public static class strobe {
					public static final double RED							=-0.11;
					public static final double BLUE							=-0.09;
					public static final double GOLD							=-0.07;
					public static final double WHITE						=-0.05;
				}
			}
			public static class color1 {
					public static final double END_TO_ENd_BLEND_TO_BLACK	=-0.03;
					public static final double LARSON_SCANNER				=-0.01;
					public static final double LIGHT_CHASE					= 0.01;
				public static class heartBeat {
					public static final double SLOW							= 0.03;
					public static final double MEDIUM						= 0.05;
					public static final double FAST							= 0.07;
				}
				public static class breath {
					public static final double SLOW							= 0.09;
					public static final double FAST							= 0.11;
				}
					public static final double SHOT							= 0.13;
					public static final double STROBE						= 0.15;
			}

			public static class color2 {
					public static final double END_TO_END_BLEND_TO_BLACK	= 0.17;
					public static final double LARSON_SCANNER				= 0.19;
					public static final double LIGHT_CHASE					= 0.21;
				public static class heartBeat {
					public static final double SLOW							= 0.23;
					public static final double MEDIUM						= 0.25;
					public static final double FAST							= 0.27;
				}
				public static class breath {
					public static final double SLOW							= 0.29;
					public static final double FAST							= 0.31;
				}
					public static final double SHOT							= 0.33;
					public static final double STROBE						= 0.35;
			};

			public static class color1and2 {
				public static class sparkle {
					public static final double COLOR_1_ON_COLOR_2			= 0.37;
					public static final double COLOR_2_ON_COLOR_1			= 0.39;
				}
					public static final double GRADIENT						= 0.41;
					public static final double BEATS_PER_MINUTE				= 0.43;
				public static class endToEndBlend {
					public static final double COLOR_1_TO_COLOR_2			= 0.45;
					public static final double COLOR_2_TO_COLOR_1			= 0.47;
				}
					public static final double END_TO_END_NO_BLEND			= 0.49;
					public static final double TWINKLE						= 0.51;
					public static final double COLOR_WAVE					= 0.53;
					public static final double SINELON						= 0.55;
			};
		}

		public static class solid {
					public static final double HOT_PINK						= 0.57;
					public static final double DARK_RED						= 0.59;
					public static final double RED							= 0.61;
					public static final double RED_ORANGE					= 0.63;
					public static final double ORANGE						= 0.65;
					public static final double GOLD							= 0.67;
					public static final double YELLOW						= 0.69;
					public static final double LAWN_GREEN					= 0.71;
					public static final double LIME							= 0.73;
					public static final double DARK_GREEN					= 0.75;
					public static final double GREEN						= 0.77;
					public static final double BLUE_GREEN					= 0.79;
					public static final double AQUA							= 0.81;
					public static final double SKY_BLUE						= 0.83;
					public static final double DARK_BLUE					= 0.85;
					public static final double BLUE							= 0.87;
					public static final double BLUE_VIOLET					= 0.89;
					public static final double VIOLET						= 0.91;
					public static final double WHITE						= 0.93;
					public static final double GRAY							= 0.95;
					public static final double DARK_GRAY					= 0.97;
					public static final double BLACK						= 0.99;
		}
	}
}