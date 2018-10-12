package ru.rakhimova;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.rakhimova.bean.service.BootstrapServiceBean;

import javax.enterprise.inject.se.SeContainerInitializer;

public class App extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/mainFrame.fxml"));
        Scene scene = new Scene(root, 730, 350);
        primaryStage.setTitle("FileCloud");
        primaryStage.setScene(scene);
        primaryStage.getScene().getStylesheets().add("css/styleMainFrame.css");
        primaryStage.show();
    }

    public static void main(String[] args) {

        SeContainerInitializer.newInstance().addPackages(App.class).initialize().select(BootstrapServiceBean.class).get().init();

        launch(App.class, args);
    }
}
