package com.guilherme.stockcontrol.stockcontrol;

import com.guilherme.stockcontrol.stockcontrol.dao.BuyDAO;
import com.guilherme.stockcontrol.stockcontrol.dao.ProductDAO;
import com.guilherme.stockcontrol.stockcontrol.dao.TransactionService;
import com.guilherme.stockcontrol.stockcontrol.model.Buy;
import com.guilherme.stockcontrol.stockcontrol.model.Product;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.Stage;

import static com.guilherme.stockcontrol.stockcontrol.Util.addTextLimiter;
import static com.guilherme.stockcontrol.stockcontrol.Util.getChangeUnaryOperator;

/**
 * Controlador para a interface de inserção e edição de itens.
 * Esta classe gerencia as interações do usuário na janela de adição ou edição de produtos.
 */
public class InsertProductController {
    public TextField productNameTextField;          // Campo de texto para o nome do produto
    public TextArea productDescriptionTextField;    // Área de texto para a descrição do produto
    public TextField purchasePriceTextField;        // Campo de texto para o preço de compra do produto
    public TextField retailPriceTextField;          // Campo de texto para o preço de venda do produto
    public TextField productQuantityTextField;      // Campo de texto para a quantidade de produto
    public Button saveProductBtn;                   // Botão para salvar o produto
    public Button clearFieldsBtn;                   // BOtão para limpar os campos
    public Button cancelBtn;                        // Botão para cancelar a operação
    public Label errorLabel;                        // Label para exibir mensagens de erro
    public CheckBox registerBuyCheckBox;

    ProductDAO productDAO = new ProductDAO();
    BuyDAO buyDAO = new BuyDAO();
    TransactionService transactionService = new TransactionService(productDAO,buyDAO);

    Product product = new Product(); // Produto que está sendo adicionado

    boolean editMode = false; // Flag para verificar se está em modo de edição

    /**
     * Metodo chamado ao inicializar o controlador.
     * Configura os validadores e formatação dos campos de entrada.
     */
    public void initialize() {

        // Formata o campo de preço de compra para aceitar apenas números e decimais
        TextFormatter<String> textFormatter = new TextFormatter<>(getChangeUnaryOperator("\\d*(\\.\\d*)?"));
        purchasePriceTextField.setTextFormatter(textFormatter);

        // Formata o campo de preço de varejo para aceitar apenas números e decimais
        TextFormatter<String> retailPriceFormatter = new TextFormatter<>(getChangeUnaryOperator("\\d*(\\.\\d*)?"));
        retailPriceTextField.setTextFormatter(retailPriceFormatter);

        // Formata o campo de quantidade para aceitar apenas números inteiros
        TextFormatter<String> intTextFormatter = new TextFormatter<>(getChangeUnaryOperator("^-?\\d*$"));
        productQuantityTextField.setTextFormatter(intTextFormatter);

        // Limita o número de caracteres no campo de nome do produto
        addTextLimiter(productNameTextField, 100);

    }

    /**
     * Preenche os campos do formulário com os dados do produto selecionado.
     *
     * @param selectedProduct O produto que será editado
     */
    public void getProduct(Product selectedProduct) {
        if (product != null) {
            editMode = true;

            product.setProduct_id(selectedProduct.getProduct_id());
            product.setProduct_name(selectedProduct.getProduct_name());
            product.setProduct_description(selectedProduct.getProduct_description());
            product.setStock_quantity(selectedProduct.getStock_quantity());
            product.setPurchase_price(selectedProduct.getPurchase_price());
            product.setRetail_price(selectedProduct.getRetail_price());

            productNameTextField.setText(product.getProduct_name());
            productDescriptionTextField.setText(product.getProduct_description());
            productQuantityTextField.setText(String.valueOf(product.getStock_quantity()));
            purchasePriceTextField.setText(String.valueOf(product.getPurchase_price()));
            retailPriceTextField.setText(String.valueOf(product.getRetail_price()));

        }
    }

    /**
     * Metodo chamado quando o botão de salvar é clicado.
     * Valida os campos e salva o produto no banco de dados.
     *
     * @param actionEvent O evento de ação do clique no botão
     */
    public void onSaveProductClicked(ActionEvent actionEvent) {


        // Verifica se todos os campos obrigatórios foram preenchidos
        if (!productNameTextField.getText().isEmpty() &&
                !purchasePriceTextField.getText().isEmpty() &&
                !retailPriceTextField.getText().isEmpty() &&
                !productQuantityTextField.getText().isEmpty()
        ) {
            // Obtém os valores dos campos
            String productName = productNameTextField.getText(); //This Field Cannot Be Null
            String productDescription = productDescriptionTextField.getText();
            float purchasePrice = Float.parseFloat(purchasePriceTextField.getText()); //This Field Cannot Be Null
            float retailPrice = Float.parseFloat(retailPriceTextField.getText()); //This Field Cannot Be Null
            int productQuantity = Integer.parseInt(productQuantityTextField.getText()); //This Field Cannot Be Null

            product.setProduct_name(productName);
            product.setProduct_description(productDescription);
            product.setPurchase_price(purchasePrice);
            product.setRetail_price(retailPrice);
            product.setStock_quantity(productQuantity);

            try {

                Buy buy = new Buy();
                buy.setProductId(product.getProduct_id());
                buy.setQuantity(product.getStock_quantity());
                buy.setBuyPrice(product.getStock_quantity() * product.getPurchase_price());
                buy.setBuyPriceUnit(product.getPurchase_price());

                // Se estiver em modo de edição, atualiza o produto, senão, insere um novo
                if (editMode) {
                    if (registerBuyCheckBox.isSelected()) {
                        productDAO.updateProduct(product);
                        buyDAO.insertBuy(buy);
                    } else {
                        productDAO.updateProduct(product);
                    }
                } else {
                    if (registerBuyCheckBox.isSelected()) {
                        transactionService.insertProductAndBuy(product, buy);
                    } else {
                        productDAO.insertProduct(product);
                    }
                }

                // Fecha a janela após salvar
                Stage stage = (Stage) saveProductBtn.getScene().getWindow();
                stage.close();
            } catch (Exception e) {
                // Exibe mensagem de erro em caso de exceção
                errorLabel.setVisible(true);
                errorLabel.setText("Error: " + e);
            }
        } else {
            // Se algum campo obrigatório não foi preenchido, exibe mensagem de erro
            errorLabel.setVisible(true);
            errorLabel.setText("Make sure you have filled out all the required fields.");
        }

    }

    /**
     * Metodo chamado quando o botão de limpar é clicado.
     * Limpa todos os campos do formulário.
     *
     * @param actionEvent O evento de ação do clique no botão
     */
    public void onClearFieldsBtnClicked(ActionEvent actionEvent) {
        productNameTextField.clear();
        productDescriptionTextField.clear();
        purchasePriceTextField.clear();
        retailPriceTextField.clear();
        productQuantityTextField.clear();
    }

    /**
     * Metodo chamado quando o botão de cancelar é clicado.
     * Fecha a janela sem salvar.
     *
     * @param actionEvent O evento de ação do clique no botão
     */
    public void onCancelBtnClicked(ActionEvent actionEvent) {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }
}
