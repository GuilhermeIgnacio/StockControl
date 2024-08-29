package com.guilherme.stockcontrol.stockcontrol;

import com.guilherme.stockcontrol.stockcontrol.dao.StockDAO;
import com.guilherme.stockcontrol.stockcontrol.model.Item;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.function.UnaryOperator;

import static com.guilherme.stockcontrol.stockcontrol.Util.addTextLimiter;
import static com.guilherme.stockcontrol.stockcontrol.Util.getChangeUnaryOperator;

public class EditItemController {
    public int itemID;
    public TextField itemNameTextField;
    public TextArea itemDescriptionTextField;
    public TextField priceTextField;
    public TextField itemQuantityTextField;
    public Button saveItemBtn;
    public Button clearFieldsBtn;
    public Button cancelBtn;
    public Label errorLabel;

    StockDAO stockDAO = new StockDAO();

    public void initialize() {

        TextFormatter<String> textFormatter = new TextFormatter<>(getChangeUnaryOperator("\\d*(\\.\\d*)?"));
        priceTextField.setTextFormatter(textFormatter);

        TextFormatter<String> intTextFormatter = new TextFormatter<>(getChangeUnaryOperator("^-?\\d*$"));
        itemQuantityTextField.setTextFormatter(intTextFormatter);

        addTextLimiter(itemNameTextField, 100);

    }

    public void getItem(Item item) {
        itemID = item.getId();
        itemNameTextField.setText(item.getItemName());
        itemDescriptionTextField.setText(item.getItemDescription());
        itemQuantityTextField.setText(String.valueOf(item.getItemQuantity()));
        priceTextField.setText(String.valueOf(item.getItemPrice()));
    }

    public void onClearFieldBtnClicked(ActionEvent actionEvent) {
        itemNameTextField.clear();
        itemDescriptionTextField.clear();
        priceTextField.clear();
        itemQuantityTextField.clear();
    }

    public void onCancelBtnClicked(ActionEvent actionEvent) {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }

    public void onSaveBtnClicked(ActionEvent actionEvent) {

        if (!itemNameTextField.getText().isEmpty() || !priceTextField.getText().isEmpty() || !itemQuantityTextField.getText().isEmpty()) {

            String itemName = itemNameTextField.getText();
            String itemDescription = itemDescriptionTextField.getText();
            int itemQuantity = Integer.parseInt(itemQuantityTextField.getText());
            float itemPrice = Float.parseFloat(priceTextField.getText());

            Item item = new Item();
            item.setId(itemID);
            item.setItemName(itemName);
            item.setItemDescription(itemDescription);
            item.setItemQuantity(itemQuantity);
            item.setItemPrice(itemPrice);

            try {
                stockDAO.updateItem(item);
                Stage stage = (Stage) saveItemBtn.getScene().getWindow();
                stage.close();
            } catch (Exception e) {
                errorLabel.setText("Error: " + e);
            }

        } else {
            errorLabel.setText("Make sure you have filled out all the required fields.");
        }

    }
}
