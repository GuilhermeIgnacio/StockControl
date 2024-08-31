package com.guilherme.stockcontrol.stockcontrol;

import com.guilherme.stockcontrol.stockcontrol.dao.StockDAO;
import com.guilherme.stockcontrol.stockcontrol.model.Item;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.Stage;

import static com.guilherme.stockcontrol.stockcontrol.Util.addTextLimiter;
import static com.guilherme.stockcontrol.stockcontrol.Util.getChangeUnaryOperator;

public class InsertItemController {
    public TextField itemNameTextField;
    public TextArea itemDescriptionTextField;
    public TextField purchasePriceTextField;
    public TextField retailPriceTextField;
    public TextField itemQuantityTextField;
    public Button saveItemBtn;
    public Button clearFieldsBtn;
    public Button cancelBtn;
    public Label errorLabel;

    Item item = new Item();

    boolean editMode = false;

    public void initialize() {

        TextFormatter<String> textFormatter = new TextFormatter<>(getChangeUnaryOperator("\\d*(\\.\\d*)?"));
        purchasePriceTextField.setTextFormatter(textFormatter);

        TextFormatter<String> retailPriceFormatter = new TextFormatter<>(getChangeUnaryOperator("\\d*(\\.\\d*)?"));
        retailPriceTextField.setTextFormatter(retailPriceFormatter);


        TextFormatter<String> intTextFormatter = new TextFormatter<>(getChangeUnaryOperator("^-?\\d*$"));
        itemQuantityTextField.setTextFormatter(intTextFormatter);

        addTextLimiter(itemNameTextField, 100);
        //Todo: Add Char Counter Under Item Name Text field.

    }

    public void getItem(Item selectedItem) {
        if (item != null) {
            editMode = true;

            item.setId(selectedItem.getId());
            item.setItemName(selectedItem.getItemName());
            item.setItemDescription(selectedItem.getItemDescription());
            item.setItemQuantity(selectedItem.getItemQuantity());
            item.setPurchasePrice(selectedItem.getPurchasePrice());
            item.setRetailPrice(selectedItem.getRetailPrice());

            itemNameTextField.setText(item.getItemName());
            itemDescriptionTextField.setText(item.getItemDescription());
            itemQuantityTextField.setText(String.valueOf(item.getItemQuantity()));
            purchasePriceTextField.setText(String.valueOf(item.getPurchasePrice()));
            retailPriceTextField.setText(String.valueOf(item.getRetailPrice()));

        }
    }

    public void onSaveItemClicked(ActionEvent actionEvent) {
        StockDAO stockDao = new StockDAO();


        if (!itemNameTextField.getText().isEmpty() && !purchasePriceTextField.getText().isEmpty() && !retailPriceTextField.getText().isEmpty() && !itemQuantityTextField.getText().isEmpty()) {

            String itemName = itemNameTextField.getText(); //This Field Cannot Be Null
            String itemDescription = itemDescriptionTextField.getText();
            float purchasePrice = Float.parseFloat(purchasePriceTextField.getText()); //This Field Cannot Be Null
            float retailPrice = Float.parseFloat(retailPriceTextField.getText()); //This Field Cannot Be Null
            int itemQuantity = Integer.parseInt(itemQuantityTextField.getText()); //This Field Cannot Be Null


            item.setItemName(itemName);
            item.setItemDescription(itemDescription);
            item.setPurchasePrice(purchasePrice);
            item.setRetailPrice(retailPrice);
            item.setItemQuantity(itemQuantity);

            try {

                if (editMode) {
                    stockDao.updateItem(item);
                } else {
                    stockDao.insertItem(item);
                }

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
        purchasePriceTextField.clear();
        retailPriceTextField.clear();
        itemQuantityTextField.clear();
    }

    public void onCancelBtnClicked(ActionEvent actionEvent) {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }
}
