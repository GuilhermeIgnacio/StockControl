package com.guilherme.stockcontrol.stockcontrol;

import com.guilherme.stockcontrol.stockcontrol.dao.StockDAO;
import com.guilherme.stockcontrol.stockcontrol.model.Item;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HelloController {

    public Button addNewItemBtn;

    public void onAddNewItemBtnClicked(ActionEvent actionEvent) throws Exception {

        InsertItemApplication insertItemApplication = new InsertItemApplication();
        Stage stage = new Stage();

        insertItemApplication.start(stage);

    }

    public void onFetchBtnClicked(ActionEvent actionEvent) {
        StockDAO stockDAO = new StockDAO();
        for (Item item : stockDAO.fetchItems()) {
            System.out.println(item.getItemName() + " - " + item.getItemDescription() + " - " + item.getItemQuantity() + " - " + item.getItemPrice() + " - " + item.getCreatedAt() + " - " + item.getUpdatedAt());
        }
    }
}