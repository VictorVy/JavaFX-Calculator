package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application
{
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Scene main = new Scene(root);
        main.getStylesheets().add("/sample/style.css");

        primaryStage.setTitle("Math-R-Us");
        primaryStage.getIcons().add(new Image("poop.png"));
        primaryStage.setScene(main);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
