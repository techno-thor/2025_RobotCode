package frc.molib.buttons;

import java.util.Vector;

/**
 * Utility class for managing instances of {@link frc.molib.buttons.Button}. 
 * <p>Update frequently to maintain the most updated values.</p>
 */
public final class ButtonManager {
	private static Vector<Button> mButtonList = new Vector<Button>();

	private ButtonManager() { throw new AssertionError("Utility Class"); }

	/**
	 * Adds a new button to the manager
	 * @param button New button
	 */
	protected static void addButton(Button button) { mButtonList.addElement(button); }

	/**
	 * Removes a specific button
	 * @param button Button to remove
	 */
	protected static void removeButton(Button button) { mButtonList.remove(button); }

	/**
	 * Removes all buttons 
	 */
	public static void removeAll() { mButtonList.clear(); }

	/**
	 * Grabs new values for all registered buttons
	 */
	public static void updateValues() {
		for (Button btnTemp : mButtonList)
			btnTemp.updateValues();
	}

	/**
	 * Clears getPressed() and getReleased() flags from each existing {@link Button}.
	 * <p><i>Useful to run at the start of each game period to avoid flags raised while disabled.</i></p>
	 */
	public static void clearFlags() {
		for(Button btnTemp : mButtonList){
			btnTemp.getPressed();
			btnTemp.getReleased();
		}
	}
}