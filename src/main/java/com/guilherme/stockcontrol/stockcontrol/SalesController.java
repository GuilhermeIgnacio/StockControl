package com.guilherme.stockcontrol.stockcontrol;

import com.guilherme.stockcontrol.stockcontrol.dao.SalesDAO;
import com.guilherme.stockcontrol.stockcontrol.dao.StockDAO;
import com.guilherme.stockcontrol.stockcontrol.model.SaleProduct;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
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
 * Controlador para a interface de gerenciamento de vendas.
 * Esta classe é responsável por exibir informações de vendas em uma tabela, calcular as receitas totais, mensais e anuais,
 * além de permitir a filtragem por produto e intervalo de datas.
 */
public class SalesController implements Initializable {

    public Label totalIncomeLabel;                                  // Exibe a receita total
    public Label monthIncomeLabel;                                  // Exibe a receita mensal
    public Label yearIncomeLabel;                                   // Exibe a receita anual

    public TableView tableView;                                     // Tabela que exibe as vendas
    public TableColumn<SaleProduct, LocalDateTime> saleDateColumn;           // Coluna da data da venda
    public TableColumn<SaleProduct, String> productNameColumn;      // Coluna do nome do produto vendido
    public TableColumn<SaleProduct, Integer> soldQuantityColumn;    // Coluna da quantidade de produtos vendidos naquela venda
    public TableColumn<SaleProduct, Float> salePriceColumn;         // Coluna do preço da venda
    public TableColumn<SaleProduct, Float> priceUnitColumn;          // Coluna do preço da unidade daquela venda

    public DatePicker fromDatePicker;                               // Filtro para a data inicial
    public DatePicker toDatePicker;                                 // Filtro para a data final
    public ComboBox<String> productsComboBox;                       // ComboBox para selecionar o produto a ser filtrado

    ObservableList<SaleProduct> saleProductsList = FXCollections.observableArrayList(); // Lista observável de vendas

    SalesDAO salesDAO = new SalesDAO(); // Instância de acesso ao banco de dados

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Inicializa a tabela de vendas e a comboBox de produtos
        createOrdersTable();
        fillProductsChoiceBox();
        displayIncomeSummary();

    }

    /**
     * Preenche os elementos com dados de renda, total, mensal e anual
     */
    private void displayIncomeSummary() {
        // Define as receitas total, mensal e anual
        totalIncomeLabel.setText(currencyFormatter.format(salesDAO.totalIncome()));
        monthIncomeLabel.setText(currencyFormatter.format(salesDAO.monthIncome()));
        yearIncomeLabel.setText(currencyFormatter.format(salesDAO.yearIncome()));
    }

    /**
     * Busca as vendas com base no produto e nas datas de filtro selecionadas.
     */
    private void fetchSales() {

        // Obtém os valores de filtro da interface
        String productName = (String) productsComboBox.getValue();
        String startDate = fromDatePicker.getEditor().getText();
        String endDate = toDatePicker.getEditor().getText();

        try {
            // Limpa a lista de vendas antes de buscar os dados novamente
            saleProductsList.clear();

            // Busca as vendas do banco de dados com base nos filtros
            for (SaleProduct saleProduct : salesDAO.fetchSaleProduct(productName, startDate, endDate)) {
                SaleProduct newSaleProduct = new SaleProduct();
                newSaleProduct.setSaleId(saleProduct.getSaleId());
                newSaleProduct.setProductName(saleProduct.getProductName());
                newSaleProduct.setQuantity(saleProduct.getQuantity());
                newSaleProduct.setSalePrice(saleProduct.getSalePrice());
                newSaleProduct.setPriceUnit(saleProduct.getPriceUnit());
                newSaleProduct.setSaleDate(saleProduct.getSaleDate());

                // Adiciona a venda à lista e define-a na tabela
                saleProductsList.add(newSaleProduct);
                tableView.setItems(saleProductsList);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Cria a tabela de vendas e define as colunas com seus respectivos valores.
     */
    private void createOrdersTable() {
        fetchSales(); // Atualiza as vendas antes de configurar as colunas

        // Configura as colunas da tabela
        saleDateColumn.setCellValueFactory(new PropertyValueFactory<>("saleDate"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        soldQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        salePriceColumn.setCellValueFactory(new PropertyValueFactory<>("salePrice"));
        priceUnitColumn.setCellValueFactory(new PropertyValueFactory<>("priceUnit"));

        // Formata a coluna da data da venda
        saleDateColumn.setCellFactory(_ -> new TableCell<>() {

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
        salePriceColumn.setCellFactory(_ -> new TableCell<>() {
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
     * Preenche a comboBox de produtos com os nomes dos produtos disponíveis.
     */
    private void fillProductsChoiceBox() {

        productsComboBox.getItems().add("");

        // Preenche a comboBox com os nomes dos produtos únicos
        for (String productName : saleProductsList.stream().map(SaleProduct::getProductName).collect(Collectors.toSet())) {
            productsComboBox.getItems().add(productName);
        }

    }

    /**
     * Ação executada ao clicar no botão de busca. Filtra as vendas com base nas opções selecionadas.
     */
    public void onSearchBtnClicked(ActionEvent actionEvent) {

        // Se as datas de início ou término estiverem preenchidas, recria a tabela com os filtros aplicados
        if (!fromDatePicker.getEditor().getText().isEmpty() || !toDatePicker.getEditor().getText().isEmpty()) {
            createOrdersTable();

            // Se algum produto estiver selecionado, recria a tabela filtrando por produto
        } else if (productsComboBox.getValue() != null) {
            createOrdersTable();
        }

    }

    /**
     * Metodo para deletar uma ou mais vendas selecionadas da tabela.
     * Solicita a confirmação do usuário antes de deletar as vendas, e, em caso positivo,
     * remove os registros do banco de dados e atualiza a tabela de vendas.
     *
     * @param actionEvent evento que dispara a ação de deleção.
     */
    public void onDeleteSaleClicked(ActionEvent actionEvent) {

        if (!tableView.getSelectionModel().getSelectedItems().isEmpty()) {

            ObservableList<SaleProduct> selectedSales = tableView.getSelectionModel().getSelectedItems();

            List<Integer> saleIds = selectedSales.stream().map(SaleProduct::getSaleId).toList();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

            if (saleIds.size() == 1) {
                alert.setHeaderText(getProp().getString("sale.delete.header.confirmation"));
            } else {
                alert.setHeaderText(getProp().getString("sale.delete.header.confirmation.plural"));
            }
            alert.setContentText(getProp().getString("sale.delete.content.confirmation"));

            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == ButtonType.OK) {
                salesDAO.deleteSale(saleIds);
                createOrdersTable();
                displayIncomeSummary();
            }

        } else {
            genericAlertDialog(Alert.AlertType.INFORMATION, "", getProp().getString("sale.delete.empty.list"), "");
        }

    }

    /**
     * Metodo para editar os detalhes de uma venda selecionada. Permite ao usuário modificar
     * a quantidade vendida e o preço unitário de uma venda, atualizando os valores no banco de dados
     * após a confirmação.
     *
     * @param actionEvent evento que dispara a ação de edição.
     */
    public void onEditSaleClicked(ActionEvent actionEvent) {
        if (tableView.getSelectionModel().getSelectedItem() != null && tableView.getSelectionModel().getSelectedItems().size() == 1) {

            SaleProduct selectedSale = (SaleProduct) tableView.getSelectionModel().getSelectedItem();

            Dialog dialog = new Dialog();

            dialog.setTitle(getProp().getString("sale.edit.title"));
            dialog.setHeaderText(getProp().getString("sale.edit.header") + selectedSale.getProductName());
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            Label soldQuantityLabel = new Label(getProp().getString("sale.sold.quantity.label"));
            Label unitPriceLabel = new Label(getProp().getString("sale.unit.price.label"));

            TextField soldQuantityTextField = new TextField(String.valueOf(selectedSale.getQuantity()));
            TextFormatter<String> soldQuantityFormatter = new TextFormatter<>(getChangeUnaryOperator("^?\\d*$"));
            soldQuantityTextField.setTextFormatter(soldQuantityFormatter);
            addTextLimiter(soldQuantityTextField, 7);

            TextField unitPriceTextField = new TextField(String.valueOf(selectedSale.getPriceUnit()));
            TextFormatter<String> salePriceFormatter = new TextFormatter<>(getChangeUnaryOperator("\\d*(\\.\\d*)?"));
            unitPriceTextField.setTextFormatter(salePriceFormatter);
            addTextLimiter(unitPriceTextField, 7);

            VBox vBox = new VBox(soldQuantityLabel, soldQuantityTextField, unitPriceLabel, unitPriceTextField);
            vBox.setSpacing(10);
            vBox.setMinWidth(400);

            dialog.getDialogPane().setContent(vBox);

            Optional result = dialog.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (!soldQuantityTextField.getText().isEmpty() && !unitPriceTextField.getText().isEmpty()) {
                    selectedSale.setQuantity(Integer.parseInt(soldQuantityTextField.getText()));
                    selectedSale.setPriceUnit(Float.parseFloat(unitPriceTextField.getText()));

                    float buyPrice = Integer.parseInt(soldQuantityTextField.getText()) * Float.parseFloat(unitPriceTextField.getText());

                    selectedSale.setSalePrice(buyPrice);

                    salesDAO.updateSale(selectedSale);

                    fetchSales();
                    displayIncomeSummary();
                } else {
                    genericAlertDialog(Alert.AlertType.INFORMATION, "", getProp().getString("changes.not.saved.warning"), getProp().getString("empty.fields.warning"));
                }

            }


        } else if (tableView.getSelectionModel().getSelectedItems().size() > 1) {
            genericAlertDialog(Alert.AlertType.INFORMATION, "", getProp().getString("sale.edit.many.items.warning"), "");
        } else if (tableView.getSelectionModel() == null || tableView.getSelectionModel().getSelectedItems().isEmpty()) {
            genericAlertDialog(Alert.AlertType.INFORMATION, "", getProp().getString("sale.edit.empty.list"), "");
        }
    }
}
