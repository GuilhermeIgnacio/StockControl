package com.guilherme.stockcontrol.stockcontrol;

import com.guilherme.stockcontrol.stockcontrol.dao.StockDAO;
import com.guilherme.stockcontrol.stockcontrol.model.Sale;
import com.guilherme.stockcontrol.stockcontrol.model.SaleProduct;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
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

    StockDAO stockDAO = new StockDAO(); // Instância de acesso ao banco de dados

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Inicializa a tabela de vendas e a comboBox de produtos
        createOrdersTable();
        fillProductsChoiceBox();

        // Define as receitas total, mensal e anual
        totalIncomeLabel.setText(currencyFormatter.format(stockDAO.totalIncome()));
        monthIncomeLabel.setText(currencyFormatter.format(stockDAO.monthIncome()));
        yearIncomeLabel.setText(currencyFormatter.format(stockDAO.yearIncome()));

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
            for (SaleProduct saleProduct : stockDAO.fetchSaleProduct(productName, startDate, endDate)) {
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
                stockDAO.deleteSale(saleIds);
                createOrdersTable();
            }

        } else {
            genericAlertDialog(Alert.AlertType.INFORMATION, "", "Selecione ao menos um produto antes de excluir.", "");
        }

    }


    public void onEditSaleClicked(ActionEvent actionEvent) {
        if (tableView.getSelectionModel().getSelectedItem() != null && tableView.getSelectionModel().getSelectedItems().size() == 1) {

            SaleProduct selectedSale = (SaleProduct) tableView.getSelectionModel().getSelectedItem();

            Dialog dialog = new Dialog();

            dialog.setHeaderText("Editar venda de " + selectedSale.getProductName());
            dialog.setTitle("Editar Venda");
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);


            VBox vBox = new VBox();

            vBox.setSpacing(10);
            vBox.setMinWidth(400);
            vBox.setMaxWidth(400);

            TextField soldQuantityTextField = new TextField();
            TextFormatter<String> soldQuantityFormatter = new TextFormatter<>(getChangeUnaryOperator("^?\\d*$"));
            soldQuantityTextField.setTextFormatter(soldQuantityFormatter);
            soldQuantityTextField.setPromptText("Quantidade Vendida");
            soldQuantityTextField.setText(String.valueOf(selectedSale.getQuantity()));

            TextField salePriceTextField = new TextField();
            TextFormatter<String> salePriceFormatter = new TextFormatter<>(getChangeUnaryOperator("\\d*(\\.\\d*)?"));
            salePriceTextField.setTextFormatter(salePriceFormatter);
            salePriceTextField.setPromptText("Valor da Venda");
            salePriceTextField.setText(String.valueOf(selectedSale.getSalePrice()));

            TextField unitPriceTextField = new TextField();
            TextFormatter<String> unitPriceFormatter = new TextFormatter<>(getChangeUnaryOperator("\\d*(\\.\\d*)?"));
            unitPriceTextField.setTextFormatter(unitPriceFormatter);
            unitPriceTextField.setPromptText("Valor por Unidade");
            unitPriceTextField.setText(String.valueOf(selectedSale.getPriceUnit()));

            vBox.getChildren().addAll(soldQuantityTextField, salePriceTextField, unitPriceTextField);

            dialog.getDialogPane().setContent(vBox);

            Optional result = dialog.showAndWait();

            if (result.get() == ButtonType.OK) {
                //Todo: Resolver a falta de concordância de dados quando um campo é editado e os outros não são apropriadamente atualizados
                selectedSale.setQuantity(Integer.parseInt(soldQuantityTextField.getText()));
                selectedSale.setSalePrice(Float.parseFloat(salePriceTextField.getText()));
                selectedSale.setPriceUnit(Float.parseFloat(unitPriceTextField.getText()));

                stockDAO.updateSale(selectedSale);

                fetchSales();

            }


        } else if (tableView.getSelectionModel().getSelectedItems().size() > 1) {

        } else if (tableView.getSelectionModel() == null || tableView.getSelectionModel().getSelectedItems().isEmpty()) {

        }
    }
}
