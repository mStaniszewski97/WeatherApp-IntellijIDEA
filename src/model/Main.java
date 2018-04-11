package model;
	
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent parent = (Parent) FXMLLoader.load(getClass().getResource("/view/MainPane.fxml"));
			Scene scene = new Scene(parent);
			primaryStage.setTitle("WeatherApp");
			primaryStage.setScene(scene);
			primaryStage.show();			
		} catch(Exception e) {
			e.printStackTrace();
		}
		primaryStage.setOnCloseRequest(e -> Platform.exit());
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
