package com.guilherme.stockcontrol.stockcontrol;

import com.guilherme.stockcontrol.stockcontrol.model.Item;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

public class EditItemController {
    public int itemID;
    public TextField itemNameTextField;
    public TextArea itemDescriptionTextField;
    public TextField priceTextField;
    public TextField itemQuantityTextField;

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

    public void getItem(Item item) {
        itemID = item.getId();
        itemNameTextField.setText(item.getItemName());
        itemDescriptionTextField.setText(item.getItemDescription());
        itemQuantityTextField.setText(String.valueOf(item.getItemQuantity()));
        priceTextField.setText(String.valueOf(item.getItemPrice()));
    }

}
