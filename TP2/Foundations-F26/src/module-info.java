module FoundationsF26 {
	requires javafx.controls;
	requires java.sql;
	
	opens applicationMain to javafx.graphics, javafx.fxml;
	opens guiLessonsList to javafx.graphics, javafx.fxml;
	opens guiLessonEditor to javafx.graphics, javafx.fxml;
	opens guiLessonDetail to javafx.graphics, javafx.fxml;
}
