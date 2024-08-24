package com.guilherme.stockcontrol.stockcontrol;

import com.guilherme.stockcontrol.stockcontrol.dao.StockDAO;
import com.guilherme.stockcontrol.stockcontrol.model.Item;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

import java.util.function.UnaryOperator;

public class InsertItemController {
    public TextField itemNameTextField;
    public TextArea itemDescriptionTextField;
    public TextField priceTextField;
    public TextField itemQuantityTextField;
    public Button saveItemBtn;
    public Button clearFieldsBtn;
    public Button cancelBtn;

    public void initialize() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();

            if (newText.matches("\\d*(\\.\\d*)?")) {
                return change;
            }
            return null;
        };

        UnaryOperator<TextFormatter.Change> intFilter = change -> {
            String newText = change.getControlNewText();

            if (newText.matches("^-?\\d*$")) {
                return change;
            }
            return null;
        };

        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        priceTextField.setTextFormatter(textFormatter);

        TextFormatter<String> intTextFormatter = new TextFormatter<>(intFilter);
        itemQuantityTextField.setTextFormatter(intTextFormatter);

    }

    public void onSaveItemClicked(ActionEvent actionEvent) {
        StockDAO stockDao = new StockDAO();

        String itemName = itemNameTextField.getText(); //This Field Cannot Be Null
        String itemDescription = itemDescriptionTextField.getText();
        float itemPrice = Float.parseFloat(priceTextField.getText()); //This Field Cannot Be Null
        int itemQuantity = Integer.parseInt(itemQuantityTextField.getText()); //This Field Cannot Be Null

        Item item = new Item();
        item.setItemName(itemName);
        item.setItemDescription(itemDescription);
        item.setItemPrice(itemPrice);
        item.setItemQuantity(itemQuantity);

        if (!itemNameTextField.getText().isEmpty() || !priceTextField.getText().isEmpty() || !itemQuantityTextField.getText().isEmpty()) {
            stockDao.insertItem(item);
            Stage stage = (Stage) saveItemBtn.getScene().getWindow();
            stage.close();
        } else {
            //Todo: Trigger Error Warning
        }


    }

    public void onClearFieldsBtnClicked(ActionEvent actionEvent) {
        itemNameTextField.clear();
        itemDescriptionTextField.clear();
        priceTextField.clear();
        itemQuantityTextField.clear();
    }

    public void onCancelBtnClicked(ActionEvent actionEvent) {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }
}
