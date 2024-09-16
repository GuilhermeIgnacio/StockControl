package com.guilherme.stockcontrol.stockcontrol;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static com.guilherme.stockcontrol.stockcontrol.Util.getProp;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        fxmlLoader.setResources(getProp());
        Scene scene = new Scene(fxmlLoader.load(), 720, 480);
        stage.setTitle(getProp().getString("main.window.title"));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}