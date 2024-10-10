package com.guilherme.stockcontrol.stockcontrol;

import com.guilherme.stockcontrol.stockcontrol.model.Product;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import static com.guilherme.stockcontrol.stockcontrol.Util.getProp;

public class InsertItemApplication extends Application {

    Product selectedItem;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(InsertItemApplication.class.getResource("add-item-view.fxml"));
        fxmlLoader.setResources(getProp());
        Scene scene = new Scene(fxmlLoader.load());
        if (selectedItem == null) {
            stage.setTitle(getProp().getString("insert.window.title"));
        } else {
            stage.setTitle(getProp().getString("edit.window.title"));
        }
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinWidth(500.0);
        stage.setMinHeight(400.0);

        if (selectedItem != null) {
            InsertItemController controller = fxmlLoader.getController();
            controller.getItem(selectedItem);
        }

        stage.showAndWait();
    }
}
