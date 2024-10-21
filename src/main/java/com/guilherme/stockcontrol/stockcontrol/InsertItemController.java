package com.guilherme.stockcontrol.stockcontrol;

import com.guilherme.stockcontrol.stockcontrol.dao.StockDAO;
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
public class InsertItemController {
    public TextField itemNameTextField;         // Campo de texto para o nome do produto
    public TextArea itemDescriptionTextField;   // Área de texto para a descrição do produto
    public TextField purchasePriceTextField;    // Campo de texto para o preço de compra do produto
    public TextField retailPriceTextField;      // Campo de texto para o preço de venda do produto
    public TextField itemQuantityTextField;     // Campo de texto para a quantidade de produto
    public Button saveItemBtn;                  // Botão para salvar o produto
    public Button clearFieldsBtn;               // BOtão para limpar os campos
    public Button cancelBtn;                    // Botão para cancelar a operação
    public Label errorLabel;                    // Label para exibir mensagens de erro

    Product item = new Product(); // Produto que está sendo adicionado

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
        itemQuantityTextField.setTextFormatter(intTextFormatter);

        // Limita o número de caracteres no campo de nome do produto
        addTextLimiter(itemNameTextField, 100);

    }

    /**
     * Preenche os campos do formulário com os dados do produto selecionado.
     * @param selectedProduct O produto que será editado
     */
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

    /**
     * Metodo chamado quando o botão de salvar é clicado.
     * Valida os campos e salva o produto no banco de dados.
     * @param actionEvent O evento de ação do clique no botão
     */
    public void onSaveItemClicked(ActionEvent actionEvent) {
        StockDAO stockDao = new StockDAO();

        // Verifica se todos os campos obrigatórios foram preenchidos
        if (!itemNameTextField.getText().isEmpty() &&
                !purchasePriceTextField.getText().isEmpty() &&
                !retailPriceTextField.getText().isEmpty() &&
                !itemQuantityTextField.getText().isEmpty()
        ) {
            // Obtém os valores dos campos
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
                // Se estiver em modo de edição, atualiza o item, senão, insere um novo
                if (editMode) {
                    stockDao.updateItem(item);
                } else {
                    stockDao.insertItem(item);
                }

                // Fecha a janela após salvar
                Stage stage = (Stage) saveItemBtn.getScene().getWindow();
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
     * @param actionEvent O evento de ação do clique no botão
     */
    public void onClearFieldsBtnClicked(ActionEvent actionEvent) {
        itemNameTextField.clear();
        itemDescriptionTextField.clear();
        purchasePriceTextField.clear();
        retailPriceTextField.clear();
        itemQuantityTextField.clear();
    }

    /**
     * Metodo chamado quando o botão de cancelar é clicado.
     * Fecha a janela sem salvar.
     * @param actionEvent O evento de ação do clique no botão
     */
    public void onCancelBtnClicked(ActionEvent actionEvent) {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }
}
