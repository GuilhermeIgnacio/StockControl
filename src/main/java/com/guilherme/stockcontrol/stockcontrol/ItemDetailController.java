package com.guilherme.stockcontrol.stockcontrol;

import com.guilherme.stockcontrol.stockcontrol.model.Item;
import javafx.scene.control.Label;

public class ItemDetailController {
    public Label detailIdLabel;
    public Label detailNameLabel;
    public Label detailDescriptionLabel;
    public Label detailQuantityLabel;
    public Label detailPriceLabel;

    public void getItem(Item item) {
        detailIdLabel.setText(String.valueOf(item.getId()));
        detailNameLabel.setText(item.getItemName());
        detailDescriptionLabel.setText(item.getItemDescription());
        detailQuantityLabel.setText(String.valueOf(item.getItemQuantity()));
        detailPriceLabel.setText("$" + item.getItemPrice());
    }
}
