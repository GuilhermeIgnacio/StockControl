package com.guilherme.stockcontrol.stockcontrol;

import com.guilherme.stockcontrol.stockcontrol.dao.StockDAO;
import com.guilherme.stockcontrol.stockcontrol.model.BuyDetails;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.guilherme.stockcontrol.stockcontrol.Util.*;

/**
 * Todo: Comentário nesta classe e seus métodos
 */
public class BuysController implements Initializable {

    public TableView tableView;
    public TableColumn<BuyDetails, LocalDateTime> buyDateColumn;
    public TableColumn<BuyDetails, String> productNameColumn;
    public TableColumn<BuyDetails, Integer> buyQuantityColumn;
    public TableColumn<BuyDetails, Float> buyPriceColumn;
    public TableColumn<BuyDetails, Float> priceUnitColumn;

    public Label totalExpenseLabel;
    public Label monthExpenseLabel;
    public Label yearExpenseLabel;
    public DatePicker fromDatePicker;
    public DatePicker toDatePicker;
    public ComboBox productsComboBox;

    ObservableList<BuyDetails> buyDetailsList = FXCollections.observableArrayList();

    StockDAO stockDAO = new StockDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        fetchBuys();

        createBuysTable();

    }

    private void fetchBuys() {
        buyDetailsList.clear();

        for (BuyDetails buyDetails : stockDAO.fetchBuys()) {
            BuyDetails newBuyDetails = new BuyDetails();

            newBuyDetails.setBuyId(buyDetails.getBuyId());
            newBuyDetails.setProductId(buyDetails.getProductId());
            newBuyDetails.setProductName(buyDetails.getProductName());
            newBuyDetails.setQuantity(buyDetails.getQuantity());
            newBuyDetails.setBuyPrice(buyDetails.getBuyPrice());
            newBuyDetails.setBuyPriceUnit(buyDetails.getBuyPriceUnit());
            newBuyDetails.setBuyDate(buyDetails.getBuyDate());

            buyDetailsList.add(newBuyDetails);

        }
    }

    private void createBuysTable() {
        tableView.setItems(buyDetailsList);

        buyDateColumn.setCellValueFactory(new PropertyValueFactory<>("buyDate"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        buyQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        buyPriceColumn.setCellValueFactory(new PropertyValueFactory<>("buyPrice"));
        priceUnitColumn.setCellValueFactory(new PropertyValueFactory<>("buyPriceUnit"));

        // Formata a coluna da data da venda
        buyDateColumn.setCellFactory(_ -> new TableCell<>() {

            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(dateTimeFormatter));
                }
            }
        });

        // Formata a coluna do preço da venda com o formato de moeda
        buyPriceColumn.setCellFactory(_ -> new TableCell<>() {
            @Override
            protected void updateItem(Float item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(currencyFormatter.format(item));
                }
            }
        });

        priceUnitColumn.setCellFactory(_ -> new TableCell<>() {
            @Override
            protected void updateItem(Float item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(currencyFormatter.format(item));
                }
            }
        });

        tableView.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );

    }

    public void onDeleteBuyButtonClicked(ActionEvent actionEvent) {

        ObservableList<BuyDetails> selectedBuys = tableView.getSelectionModel().getSelectedItems();

        if (!selectedBuys.isEmpty()) {

            List<Integer> buyIds = selectedBuys.stream().map(BuyDetails::getBuyId).toList();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

            if (selectedBuys.size() > 1) {
                alert.setHeaderText(getProp().getString("buy.delete.header.message.plural"));
            } else if (selectedBuys.size() == 1) {
                alert.setHeaderText(getProp().getString("buy.delete.header.message"));
            }

            alert.setContentText(getProp().getString("buy.delete.content.message"));

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                stockDAO.deleteBuy(buyIds);
                fetchBuys();
            }

        } else {
            genericAlertDialog(Alert.AlertType.INFORMATION, "", getProp().getString("empty.list.warning"), "");
        }

    }
}
