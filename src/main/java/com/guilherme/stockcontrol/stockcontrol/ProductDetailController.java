package com.guilherme.stockcontrol.stockcontrol;

import com.guilherme.stockcontrol.stockcontrol.dao.BuyDAO;
import com.guilherme.stockcontrol.stockcontrol.dao.SalesDAO;
import com.guilherme.stockcontrol.stockcontrol.model.Product;
import javafx.scene.control.Label;

import static com.guilherme.stockcontrol.stockcontrol.Util.*;

/**
 * Controlador para a tela de detalhes do produto.
 * Esta classe é responsável por exibir as informações detalhadas sobre um produto selecionado.
 */
public class ProductDetailController {
    public Label detailIdLabel;             // Label para exibir o ID do produto
    public Label detailNameLabel;           // Label para exibir o nome do produto
    public Label detailDescriptionLabel;    // Label para exibir a descrição do produto
    public Label detailQuantityLabel;       // Label para exibir a quantidade em estoque do produto
    public Label detailPurchasePriceLabel;  // Label para exibir o preço de compra do produto
    public Label detailRetailPriceLabel;    // Label para exibir o preço de varejo do produto
    public Label detailProfitMarginLabel;   // Label para exibir a margem de lucro do produto
    public Label detailCreatedAtLabel;      // Label para exibir a data de criação do produto
    public Label detailUpdatedAtLabel;      // Label para exibir a data da última atualização do produto
    public Label detailSalesLabel;          // Label para exibir a quantidade total de vendas do produto
    public Label saleAmount;                // Label para exibir o valor total em vendas do produto em Reais
    public Label totalUnitBuys;             // Label para exibir o total comprado em unidades
    public Label totalBuys;                 // Label para exibir o total comprado em Reais

    SalesDAO salesDAO = new SalesDAO();     // Instância do DAO para acessar dados do estoque
    BuyDAO buyDAO = new BuyDAO();     // Instância do DAO para acessar dados do estoque

    /**
     * Preenche os detalhes do produto na tela.
     * @param product O produto selecionado cujas informações serão exibidas.
     */
    public void getProduct(Product product) {
        // Exibe o ID do produto
        detailIdLabel.setText(String.valueOf(product.getProduct_id()));

        // Exibe o nome e a descrição do produto
        detailNameLabel.setText(product.getProduct_name());
        detailDescriptionLabel.setText(product.getProduct_description());

        // Exibe a quantidade em estoque
        detailQuantityLabel.setText(String.valueOf(product.getStock_quantity()));


        // Formata e exibe o preço de compra e de varejo do produto
        detailPurchasePriceLabel.setText(currencyFormatter.format(product.getPurchase_price()));
        detailRetailPriceLabel.setText(currencyFormatter.format(product.getRetail_price()));

        // Calcula e exibe a margem de lucro do produto
        float profitMargin = ((product.getRetail_price() - product.getPurchase_price()) / product.getRetail_price()) * 100f;
        String formattedMarginProfit = String.format("%.1f%% (%s por unidade)", profitMargin, currencyFormatter.format(product.getRetail_price() - product.getPurchase_price()));
        detailProfitMarginLabel.setText(formattedMarginProfit);

        // Exibe o valor total em vendas do produto e o total de vendas do produto
        detailSalesLabel.setText(String.valueOf(salesDAO.getProductTotalSales(product.getProduct_id())));
        saleAmount.setText(currencyFormatter.format(salesDAO.getProductSaleAmount(product.getProduct_id())));

        // Exibe as informações de compra do produto
        totalUnitBuys.setText(String.valueOf(buyDAO.getTotalUnitPurchased(product.getProduct_id())));
        totalBuys.setText(currencyFormatter.format(buyDAO.getTotalPurchasedInCurrency(product.getProduct_id())));

        // Formata e exibe as datas de criação e última atualização do produto
        String formattedCreatedAt = product.getCreated_at().format(dateTimeFormatter);
        String formattedUpdated = product.getUpdated_at().format(dateTimeFormatter);

        detailCreatedAtLabel.setText(formattedCreatedAt);
        detailUpdatedAtLabel.setText(formattedUpdated);

    }
}
