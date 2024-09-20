package com.guilherme.stockcontrol.stockcontrol;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.guilherme.stockcontrol.stockcontrol.Util.getProp;

public class HelloController implements Initializable {

    public Button statisticsBtn;
    public VBox contentArea;
    public Button homeBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            loadContent("home-view.fxml");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void loadContent(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(fxml)));
        loader.setResources(getProp());
        Parent newContent = loader.load();
        contentArea.getChildren().setAll(newContent);
    }

    public void onStatisticsBtnClicked(ActionEvent actionEvent) throws Exception {
        loadContent("statistics-view.fxml");
    }

    public void onHomeBtnClicked(ActionEvent actionEvent) throws Exception {
        loadContent("home-view.fxml");
    }
}