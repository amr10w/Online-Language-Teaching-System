package Main;

import javafx.scene.control.Alert;

public class AlertMessage {
    public static void alertMessage(String header,String message)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
