package com.guilherme.stockcontrol.stockcontrol;

import com.guilherme.stockcontrol.stockcontrol.dao.StockDAO;
import com.guilherme.stockcontrol.stockcontrol.model.Item;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    public Button addNewItemBtn;
    public TableView itemTableView;

    //Table Columns
    public TableColumn<Item, String> idTableColumn;
    public TableColumn<Item, String> nameTableColumn;
    public TableColumn<Item, String> descriptionTableColumn;
    public TableColumn<Item, String> quantityTableColumn;
    public TableColumn<Item, String> priceTableColumn;
    public TableColumn<Item, String> createdAtTableColumn;
    public TableColumn<Item, String> updatedAtTableColumn;
    //Table Columns

    ObservableList<Item> itemList = FXCollections.observableArrayList();

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        StockDAO stockDAO = new StockDAO();

        try {
            itemList.clear();

            for (Item item : stockDAO.fetchItems()) {

                Item newItem = new Item();
                newItem.setId(item.getId());
                newItem.setItemName(item.getItemName());
                newItem.setItemDescription(item.getItemDescription());
                newItem.setItemQuantity(item.getItemQuantity());
                newItem.setItemPrice(item.getItemPrice());
                newItem.setCreatedAt(item.getCreatedAt());
                newItem.setUpdatedAt(item.getUpdatedAt());

                itemList.add(newItem);
                itemTableView.setItems(itemList);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        idTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameTableColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        descriptionTableColumn.setCellValueFactory(new PropertyValueFactory<>("itemDescription"));
        quantityTableColumn.setCellValueFactory(new PropertyValueFactory<>("itemQuantity"));
        priceTableColumn.setCellValueFactory(new PropertyValueFactory<>("itemPrice"));
        createdAtTableColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        updatedAtTableColumn.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));

    }
}