package com.guilherme.stockcontrol.stockcontrol;

import com.guilherme.stockcontrol.stockcontrol.dao.StockDAO;
import com.guilherme.stockcontrol.stockcontrol.model.Item;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.function.UnaryOperator;

import static com.guilherme.stockcontrol.stockcontrol.Util.addTextLimiter;
import static com.guilherme.stockcontrol.stockcontrol.Util.getChangeUnaryOperator;

public class InsertItemController {
    public TextField itemNameTextField;
    public TextArea itemDescriptionTextField;
    public TextField priceTextField;
    public TextField itemQuantityTextField;
    public Button saveItemBtn;
    public Button clearFieldsBtn;
    public Button cancelBtn;
    public Label errorLabel;

    public void initialize() {

        TextFormatter<String> textFormatter = new TextFormatter<>(getChangeUnaryOperator("\\d*(\\.\\d*)?"));
        priceTextField.setTextFormatter(textFormatter);

        TextFormatter<String> intTextFormatter = new TextFormatter<>(getChangeUnaryOperator("^-?\\d*$"));
        itemQuantityTextField.setTextFormatter(intTextFormatter);

        addTextLimiter(itemNameTextField, 100);
        //Todo: Add Char Counter Under Item Name Text field.

    }


    public void onSaveItemClicked(ActionEvent actionEvent) {
        StockDAO stockDao = new StockDAO();


        if (!itemNameTextField.getText().isEmpty() && !priceTextField.getText().isEmpty() && !itemQuantityTextField.getText().isEmpty()) {

            String itemName = itemNameTextField.getText(); //This Field Cannot Be Null
            String itemDescription = itemDescriptionTextField.getText();
            float itemPrice = Float.parseFloat(priceTextField.getText()); //This Field Cannot Be Null
            int itemQuantity = Integer.parseInt(itemQuantityTextField.getText()); //This Field Cannot Be Null

            Item item = new Item();
            item.setItemName(itemName);
            item.setItemDescription(itemDescription);
            item.setItemPrice(itemPrice);
            item.setItemQuantity(itemQuantity);

            try {
                stockDao.insertItem(item);
                Stage stage = (Stage) saveItemBtn.getScene().getWindow();
                stage.close();
            } catch (Exception e) {
                errorLabel.setVisible(true);
                errorLabel.setText("Error: " + e);
            }
        } else {
            //Todo: Trigger Error Warning
            errorLabel.setVisible(true);
            errorLabel.setText("Make sure you have filled out all the required fields.");
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
