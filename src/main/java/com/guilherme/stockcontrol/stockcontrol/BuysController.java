package com.guilherme.stockcontrol.stockcontrol;

import com.guilherme.stockcontrol.stockcontrol.dao.BuyDAO;
import com.guilherme.stockcontrol.stockcontrol.model.Buy;
import com.guilherme.stockcontrol.stockcontrol.model.BuyDetails;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static com.guilherme.stockcontrol.stockcontrol.Util.*;

/**
 * Este controlador permite visualizar, filtrar, editar e excluir registros de compras.
 * Ele também exibe um resumo de despesas total, mensal e anual.
 */
public class BuysController implements Initializable {

    public TableView<BuyDetails> tableView; // Tabela de exibição de detalhes de compra
    public TableColumn<BuyDetails, LocalDateTime> buyDateColumn; // Coluna para a data da compra
    public TableColumn<BuyDetails, String> productNameColumn; // Coluna para o nome do produto
    public TableColumn<BuyDetails, Integer> buyQuantityColumn; // Coluna para a quantidade comprada
    public TableColumn<BuyDetails, Float> buyPriceColumn; // Coluna para o preço total da compra
    public TableColumn<BuyDetails, Float> priceUnitColumn; // Coluna para o preço unitário do produto

    public Label totalExpenseLabel; // Exibe o total gasto em compras
    public Label monthExpenseLabel; // Exibe o total gasto no mês corrente
    public Label yearExpenseLabel; // Exibe o total gasto no ano corrente
    public DatePicker fromDatePicker; // Permite filtrar compras a partir de uma data
    public DatePicker toDatePicker; // Permite filtrar compras até uma data
    public ComboBox<String> productsComboBox; // Filtro para seleção de produto

    ObservableList<BuyDetails> buyDetailsList = FXCollections.observableArrayList(); // Lista observável para armazenar detalhes de compra
    BuyDAO buyDAO = new BuyDAO(); // Data Access Object para operações de compra

    /**
     * Inicializa a interface com os dados de compra, configurando a tabela e filtros.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        fetchBuys();
        createBuysTable();
        fillProductsChoiceBox();
        displayExpenseSummary();
    }

    /**
     * Preenche os dados das despesas de compra
     */
    private void displayExpenseSummary() {
        totalExpenseLabel.setText(currencyFormatter.format(buyDAO.fetchTotalExpense()));
        monthExpenseLabel.setText(currencyFormatter.format(buyDAO.fetchMonthExpense()));
        yearExpenseLabel.setText(currencyFormatter.format(buyDAO.fetchYearExpense()));
    }

    /**
     * Busca e filtra registros de compra com base nos filtros selecionados e preenche `buyDetailsList`.
     */
    private void fetchBuys() {
        buyDetailsList.clear();

        String productName = productsComboBox.getValue();
        String startDate = fromDatePicker.getEditor().getText();
        String endDate = toDatePicker.getEditor().getText();

        for (BuyDetails buyDetails : buyDAO.fetchBuys(productName, startDate, endDate)) {
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

    /**
     * Configura a tabela para exibir dados de compra e define formatação para algumas colunas.
     */
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

    /**
     * Trata o evento do botão de exclusão de compra. Solicita confirmação ao usuário e,
     * se confirmada, exclui as compras selecionadas.
     *
     * @param actionEvent Evento de clique no botão
     */
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
                buyDAO.deleteBuy(buyIds);
                fetchBuys();
                displayExpenseSummary();
            }

        } else {
            genericAlertDialog(Alert.AlertType.INFORMATION, "", getProp().getString("empty.list.warning"), "");
        }

    }

    /**
     * Trata o evento de edição de compra, abrindo um diálogo para editar a quantidade e o preço unitário.
     *
     * @param actionEvent Evento de clique no botão de edição
     */
    public void onEditBuyClicked(ActionEvent actionEvent) {

        if (tableView.getSelectionModel().getSelectedItems().size() == 1) {
            BuyDetails selectedBuy = (BuyDetails) tableView.getSelectionModel().getSelectedItem();

            Dialog dialog = new Dialog();
            dialog.setTitle(getProp().getString("buy.edit.title"));
            dialog.setHeaderText(getProp().getString("buy.edit.header") + selectedBuy.getProductName());

            Label quantityLabel = new Label(getProp().getString("buy.quantity.label"));

            TextField quantityTextField = new TextField(String.valueOf(selectedBuy.getQuantity()));
            TextFormatter<String> quantityFormatter = new TextFormatter<>(getChangeUnaryOperator("^?\\d*$"));
            quantityTextField.setTextFormatter(quantityFormatter);
            addTextLimiter(quantityTextField, 7);

            Label buyPriceUnitLabel = new Label(getProp().getString("buy.price.unit.label"));

            TextField buyPriceUnityTextField = new TextField();
            TextFormatter<String> buyPriceUnityFormatter = new TextFormatter<>(getChangeUnaryOperator("\\d*(\\.\\d*)?"));
            buyPriceUnityTextField.setTextFormatter(buyPriceUnityFormatter);
            addTextLimiter(buyPriceUnityTextField, 7);
            buyPriceUnityTextField.setText(String.valueOf(selectedBuy.getBuyPriceUnit()));

            VBox vBox = new VBox();
            vBox.getChildren().addAll(quantityLabel, quantityTextField, buyPriceUnitLabel, buyPriceUnityTextField);
            vBox.setSpacing(10);

            vBox.setMinWidth(400);

            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            dialog.getDialogPane().setContent(vBox);

            Optional<ButtonType> result = dialog.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {

                if (!quantityTextField.getText().isEmpty() && !buyPriceUnityTextField.getText().isEmpty()) {


                    Buy buy = new Buy();
                    buy.setBuyId(selectedBuy.getBuyId());
                    buy.setProductId(selectedBuy.getProductId());
                    buy.setQuantity(Integer.parseInt(quantityTextField.getText()));
                    buy.setBuyPrice(Integer.parseInt(quantityTextField.getText()) * Float.parseFloat(buyPriceUnityTextField.getText()));
                    buy.setBuyPriceUnit(Float.parseFloat(buyPriceUnityTextField.getText()));
                    buy.setBuyDate(selectedBuy.getBuyDate());

                    buyDAO.updateBuy(buy);
                    fetchBuys();
                    displayExpenseSummary();
                } else {
                    genericAlertDialog(Alert.AlertType.INFORMATION, "", getProp().getString("changes.not.saved.warning"), getProp().getString("empty.fields.warning"));
                }
            }

        } else {
            genericAlertDialog(Alert.AlertType.INFORMATION, "", getProp().getString("buy.edit.many.items.warning"), "");
        }

    }

    /**
     * Atualiza a tabela de compras com base nos filtros selecionados.
     *
     * @param actionEvent Evento de alteração nos filtros
     */
    public void onFilterChanged(ActionEvent actionEvent) {
        if (!fromDatePicker.getEditor().getText().isEmpty() || !toDatePicker.getEditor().getText().isEmpty()) {
            fetchBuys();

            // Se algum produto estiver selecionado, recria a tabela filtrando por produto
        } else if (productsComboBox.getValue() != null) {
            fetchBuys();
        }
    }

    /**
     * Preenche a ComboBox de produtos com nomes de produtos únicos obtidos da lista de detalhes de compra.
     */
    private void fillProductsChoiceBox() {

        productsComboBox.getItems().add("");

        // Preenche a comboBox com os nomes dos produtos únicos
        for (String productName : buyDetailsList.stream().map(BuyDetails::getProductName).collect(Collectors.toSet())) {
            productsComboBox.getItems().add(productName);
        }

    }

}
