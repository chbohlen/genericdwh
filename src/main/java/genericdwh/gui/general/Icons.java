package genericdwh.gui.general;

import genericdwh.main.Main;
import javafx.scene.image.Image;

public class Icons {
	/* Windows */
	public static Image MAIN_WINDOW = new Image(Main.class.getResourceAsStream("/icons/windows/main.png"));
	public static Image EDITOR_WINDOW = new Image(Main.class.getResourceAsStream("/icons/windows/editor.png"));
	public static Image CONNECT_DIALOG = new Image(Main.class.getResourceAsStream("/icons/windows/connect.png"));
	public static Image CONFIRMATION_DIALOG = new Image(Main.class.getResourceAsStream("/icons/windows/confirmation.png"));
	
	/* Main */
	public static Image FOLDER = new Image(Main.class.getResourceAsStream("/icons/main/folder.png"));
	public static Image DIMENSION_HIERARCHY = new Image(Main.class.getResourceAsStream("/icons/main/dimension_hierarchy.png"));
	public static Image DIMENSION = new Image(Main.class.getResourceAsStream("/icons/main/dimension.png"));
	public static Image REFERENCE_OBJECT = new Image(Main.class.getResourceAsStream("/icons/main/reference_object.png"));
	public static Image RATIO = new Image(Main.class.getResourceAsStream("/icons/main/ratio.png"));
	
	/* Editor */
	public static Image UNGROUPED = new Image(Main.class.getResourceAsStream("/icons/editor/ungrouped.png"));
	public static Image GROUPED = new Image(Main.class.getResourceAsStream("/icons/editor/grouped.png"));
}
