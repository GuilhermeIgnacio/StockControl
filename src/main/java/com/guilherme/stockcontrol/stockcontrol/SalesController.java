package com.guilherme.stockcontrol.stockcontrol;

import com.guilherme.stockcontrol.stockcontrol.dao.StockDAO;
import com.guilherme.stockcontrol.stockcontrol.model.SaleProduct;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    public TableColumn<SaleProduct, Date> saleDateColumn;           // Coluna da data da venda
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
            private final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
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
}
