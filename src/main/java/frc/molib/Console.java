package frc.molib;

import java.text.SimpleDateFormat;
import java.util.Date;

/** Utility class for writing to the Driver Station Console. */
public class Console {
	private Console() { throw new AssertionError("Utility Class"); }

	/**
	 * Gets a formatted timestamp at the current time
	 * to be appended to messages logged in the console.
	 * @return Formatted timestamp
	 */
	private static String getTimestamp() { return new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()); }

	/** Prints a standard line separating other logs to the Console. */
	public static void printSeparator() { System.out.println("--------------------------------------------------"); }

	/** Prints a standardized header, creating a new section of logs to the Console. */
	public static void printHeader(String title) { 
		Console.printSeparator();
		System.out.println(title);
		Console.printSeparator();
	}

	/**
	 * Prints a formatted message to the Console.
	 * @param message Message to be logged
	 */
	public static void logMsg(String message) { System.out.println("[Log][" + getTimestamp() + "] " + message); }

	/**
	 * Prints a formatted error message to the Console.
	 * @param message Message to be logged
	 */
	public static void logErr(String message) { System.err.println("[Err][" + getTimestamp() + "] " + message); }
}