package com.guilherme.stockcontrol.stockcontrol;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class InsertItemApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(InsertItemApplication.class.getResource("add-item-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Insert Item");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinWidth(500.0);
        stage.setMinHeight(400.0);
        stage.showAndWait();
    }
}
