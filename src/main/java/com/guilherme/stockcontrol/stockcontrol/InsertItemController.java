package com.guilherme.stockcontrol.stockcontrol;

import com.guilherme.stockcontrol.stockcontrol.dao.StockDAO;
import com.guilherme.stockcontrol.stockcontrol.model.Product;
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

    Product item = new Product();

    boolean editMode = false;

    public void initialize() {

        TextFormatter<String> textFormatter = new TextFormatter<>(getChangeUnaryOperator("\\d*(\\.\\d*)?"));
        purchasePriceTextField.setTextFormatter(textFormatter);

        TextFormatter<String> retailPriceFormatter = new TextFormatter<>(getChangeUnaryOperator("\\d*(\\.\\d*)?"));
        retailPriceTextField.setTextFormatter(retailPriceFormatter);

        TextFormatter<String> intTextFormatter = new TextFormatter<>(getChangeUnaryOperator("^-?\\d*$"));
        itemQuantityTextField.setTextFormatter(intTextFormatter);

        addTextLimiter(itemNameTextField, 100);

    }

    public void getItem(Product selectedProduct) {
        if (item != null) {
            editMode = true;

            item.setProduct_id(selectedProduct.getProduct_id());
            item.setProduct_name(selectedProduct.getProduct_name());
            item.setProduct_description(selectedProduct.getProduct_description());
            item.setStock_quantity(selectedProduct.getStock_quantity());
            item.setPurchase_price(selectedProduct.getPurchase_price());
            item.setRetail_price(selectedProduct.getRetail_price());

            itemNameTextField.setText(item.getProduct_name());
            itemDescriptionTextField.setText(item.getProduct_description());
            itemQuantityTextField.setText(String.valueOf(item.getStock_quantity()));
            purchasePriceTextField.setText(String.valueOf(item.getPurchase_price()));
            retailPriceTextField.setText(String.valueOf(item.getRetail_price()));

        }
    }

    public void onSaveItemClicked(ActionEvent actionEvent) {
        StockDAO stockDao = new StockDAO();

        if (!itemNameTextField.getText().isEmpty() &&
                !purchasePriceTextField.getText().isEmpty() &&
                !retailPriceTextField.getText().isEmpty() &&
                !itemQuantityTextField.getText().isEmpty()
        ) {

            String itemName = itemNameTextField.getText(); //This Field Cannot Be Null
            String itemDescription = itemDescriptionTextField.getText();
            float purchasePrice = Float.parseFloat(purchasePriceTextField.getText()); //This Field Cannot Be Null
            float retailPrice = Float.parseFloat(retailPriceTextField.getText()); //This Field Cannot Be Null
            int itemQuantity = Integer.parseInt(itemQuantityTextField.getText()); //This Field Cannot Be Null


            item.setProduct_name(itemName);
            item.setProduct_description(itemDescription);
            item.setPurchase_price(purchasePrice);
            item.setRetail_price(retailPrice);
            item.setStock_quantity(itemQuantity);

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
