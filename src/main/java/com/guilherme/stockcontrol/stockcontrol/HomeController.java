package com.guilherme.stockcontrol.stockcontrol;

import com.guilherme.stockcontrol.stockcontrol.dao.ProductDAO;
import com.guilherme.stockcontrol.stockcontrol.dao.SalesDAO;
import com.guilherme.stockcontrol.stockcontrol.model.Product;
import com.guilherme.stockcontrol.stockcontrol.model.Sale;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

import static com.guilherme.stockcontrol.stockcontrol.Util.*;

/**
 * A classe HomeController é responsável pelo controle da interface de gerenciamento de produtos no sistema de controle de estoque.
 * Ela implementa a interface Initializable, que exige a implementação do metodo initialize(), utilizado para inicializar a tabela de produtos
 * e outros componentes da interface.
 */

public class HomeController implements Initializable {

    /* Elementos da interface de usuário */

    public TableView productTableView; // Tabela de produtos exibidos

    // Colunas da tabela
    public TableColumn<Product, String> productIdTableColumn; // Coluna de ID do produto
    public TableColumn<Product, String> productNameTableColumn; // Coluna de Nome do produto
    public TableColumn<Product, String> productDescriptionTableColumn; // Coluna de descrição do produto
    public TableColumn<Product, Float> purchasePriceTableColumn; // Coluna de preço de compra
    public TableColumn<Product, Float> retailPriceTableColumn; // Coluna de preço de venda
    public TableColumn<Product, String> stockQuantityTableColumn; // Coluna de quantidade em estoque
    public TableColumn<Product, LocalDateTime> createdAtTableColumn; // Coluna de data de criação
    public TableColumn<Product, LocalDateTime> updatedAtTableColumn; // Coluna de data de atualização

    public TextField searchTextField; // Campo de busca de produtos

    // Lista observável de produtos que será exibida na tabela
    // Uma lista observável(ObservableList) é uma lista que pode ser monitorada para mudanças.
    ObservableList<Product> itemList = FXCollections.observableArrayList();

    // Objeto de acesso aos dados de estoque
    ProductDAO productDAO = new ProductDAO();
    SalesDAO salesDAO = new SalesDAO();

    /**
     * Metodo de inicialização que é chamado quando a interface é carregada.
     * Ele configura a tabela de produtos e verifica os alertas de baixo estoque.
     */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        createTable(); // Configura as colunas e popula a tabela com produtos
        Platform.runLater(this::lowStockWarning); // Executa o alerta de baixo estoque após a inicialização
    }

    /**
     * Metodo chamado quando o botão de adicionar novo item é clicado.
     * Ele abre a janela de inserção de item e atualiza a lista de produtos após a inserção.
     */

    public void onAddNewItemBtnClicked(ActionEvent actionEvent) throws Exception {

        InsertProductApplication insertProductApplication = new InsertProductApplication(); // Cria nova instância da aplicação de inserção de item
        Stage stage = new Stage(); // Cria nova janela

        // Inicia a janela de inserção de item
        insertProductApplication.start(stage);

        // Atualiza a lista de produtos ao fechar a janela de inserção
        fetchItems();

    }

    /**
     * Busca e popula a tabela com os itens do estoque.
     * Filtra os itens com base no texto inserido no campo de busca.
     */
    private void fetchItems() {
        try {
            itemList.clear(); // Limpa a lista atual

            for (Product product : productDAO.fetchItems(searchTextField.getText())) {

                Product newProduct = new Product();
                newProduct.setProduct_id(product.getProduct_id());
                newProduct.setProduct_name(product.getProduct_name());
                newProduct.setProduct_description(product.getProduct_description());
                newProduct.setStock_quantity(product.getStock_quantity());
                newProduct.setPurchase_price(product.getPurchase_price());
                newProduct.setRetail_price(product.getRetail_price());
                newProduct.setCreated_at(product.getCreated_at());
                newProduct.setUpdated_at(product.getUpdated_at());

                itemList.add(newProduct); // Adiciona cada produto à lista
                productTableView.setItems(itemList);  // Define a lista como fonte de dados da tabela

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Configura as colunas da tabela com seus respectivos valores e formatações personalizadas.
     */

    private void createTable() {

        fetchItems(); // Carrega os itens para exibir na tabela

        // Configura os valores de cada coluna da tabela
        productIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("product_id"));
        productNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("product_name"));
        productDescriptionTableColumn.setCellValueFactory(new PropertyValueFactory<>("product_description"));
        stockQuantityTableColumn.setCellValueFactory(new PropertyValueFactory<>("stock_quantity"));
        purchasePriceTableColumn.setCellValueFactory(new PropertyValueFactory<>("purchase_price"));
        retailPriceTableColumn.setCellValueFactory(new PropertyValueFactory<>("retail_price"));
        createdAtTableColumn.setCellValueFactory(new PropertyValueFactory<>("created_at"));
        updatedAtTableColumn.setCellValueFactory(new PropertyValueFactory<>("updated_at"));

        // Formatação personalizada para exibição de preços
        purchasePriceTableColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Product, Float> call(TableColumn<Product, Float> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Float item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(currencyFormatter.format(item));
                        }
                    }
                };
            }
        });

        retailPriceTableColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Product, Float> call(TableColumn<Product, Float> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Float item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(currencyFormatter.format(item));
                        }
                    }
                };
            }
        });

        // Formatação para datas de criação e atualização
        createdAtTableColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Product, LocalDateTime> call(TableColumn<Product, LocalDateTime> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(LocalDateTime item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.format(dateTimeFormatter));
                        }
                    }
                };
            }
        });

        updatedAtTableColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Product, LocalDateTime> call(TableColumn<Product, LocalDateTime> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(LocalDateTime item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.format(dateTimeFormatter));
                        }
                    }
                };
            }
        });

        // Permite múltiplas seleções de linhas na tabela
        productTableView.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );
    }

    /**
     * Metodo chamado quando o botão de edição é clicado.
     * Abre a janela de edição de um produto selecionado.
     */
    public void onEditBtnClicked(ActionEvent actionEvent) throws Exception {

        if (productTableView.getSelectionModel().getSelectedItem() != null && productTableView.getSelectionModel().getSelectedItems().size() <= 1) {

            Product selectedItem = (Product) productTableView.getSelectionModel().getSelectedItem(); // Obtém o item selecionado

            InsertProductApplication application = new InsertProductApplication();
            Stage stage = new Stage();

            application.selectedProduct = selectedItem;

            application.start(stage); // Abre a janela de edição

            fetchItems(); // Atualiza a lista de produtos

        } else {
            // Exibe um alerta se mais de um item estiver selecionado
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(getProp().getString("edit.alert.dialog.content"));

            alert.showAndWait();
        }

    }

    /**
     * Metodo chamado quando o botão de exclusão é clicado.
     * Exclui os produtos selecionados da lista e do banco de dados.
     */

    public void onDeleteBtnClicked(ActionEvent actionEvent) {

        if (!productTableView.getSelectionModel().getSelectedItems().isEmpty()) {

            ObservableList<Product> selectedProducts = productTableView.getSelectionModel().getSelectedItems();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(getProp().getString("delete.confirm.message"));

            if (selectedProducts.size() <= 1) {
                alert.setHeaderText(getProp().getString("delete.warning"));
            } else {
                alert.setHeaderText(getProp().getString("delete.warning.plural"));
            }

            ButtonType buttonTypeOne = new ButtonType(getProp().getString("delete.confirm.button"));
            ButtonType buttonTypeCancel = new ButtonType(getProp().getString("delete.cancel.button"), ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonTypeOne) {

                // Ao confirmar a ação, deleta os produtos e atualiza a lista
                selectedProducts.forEach(product ->
                        productDAO.deleteItemById(product.getProduct_id())
                );

                fetchItems();

            }
        }
    }

    /**
     * Metodo chamado quando o botão de refresh é clicado.
     * Atualiza a lista de produtos.
     */
    public void onRefreshBtnClicked(ActionEvent actionEvent) {
        fetchItems();
    }

    /**
     * Metodo chamado quando um item da tabela é clicado.
     * Abre a janela de detalhes do item ao detectar um duplo clique.
     *
     * @throws IOException Se ocorrer um erro ao carregar o arquivo FXML
     */
    public void onItemClicked(MouseEvent mouseEvent) throws IOException {

        if (mouseEvent.getClickCount() == 2) { // Verifica se houve duplo clique

            if (productTableView.getSelectionModel().getSelectedItem() != null) { // Verifica se um item foi selecionado
                Product selectedItem = (Product) productTableView.getSelectionModel().getSelectedItem(); // Obtém o item selecionado

                // Carrega a interface de detalhes do item
                FXMLLoader loader = new FXMLLoader(getClass().getResource("item-detail-view.fxml"));
                loader.setResources(getProp());

                // Configura a nova janela de detalhes do item
                Stage stage = new Stage(StageStyle.DECORATED);
                stage.setScene(new Scene(loader.load()));
                stage.setTitle(selectedItem.getProduct_name());
                stage.setMinWidth(720);
                stage.setMinHeight(520);

                // Passa o item selecionado para o controlador da tela de detalhes
                ProductDetailController controller = loader.getController();
                controller.getProduct(selectedItem);

                stage.showAndWait(); // Exibe a janela e espera a ação do usuário
            }

        }


    }

    /**
     * Metodo chamado quando o botão de registrar venda é clicado.
     * Exibe uma janela de diálogo para que o usuário informe a quantidade de produtos vendidos
     * e registra as vendas no banco de dados.
     *
     * @param actionEvent Evento de clique do botão
     */
    public void onRegisterSaleClicked(ActionEvent actionEvent) {

        if (!productTableView.getSelectionModel().getSelectedItems().isEmpty()) {
            // Verifica se há produtos selecionados
            ObservableList<Product> selectedProducts = productTableView.getSelectionModel().getSelectedItems();
            List<Sale> saleList = new ArrayList<>();

            // Cria uma janela de diálogo para inserir as quantidades vendidas
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setWidth(300);
            dialog.setHeaderText(getProp().getString("register.sale.header.text"));

            // Configura os botões de confirmação e cancelamento
            ButtonType confirmButton = new ButtonType(getProp().getString("register.sale.confirm.button"), ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(confirmButton, ButtonType.CANCEL);

            // VBox para os campos de texto
            VBox vBox = new VBox();
            vBox.setSpacing(10);

            // Map para armazenar os campos de texto correspondentes aos produtos
            Map<Product, TextField> textFieldMap = new HashMap<>();

            for (Product product : selectedProducts) {
                Label productLabel = new Label(product.getProduct_name());

                TextField soldQuantityTextField = new TextField();
                soldQuantityTextField.setPromptText("Quantidade vendida");
                soldQuantityTextField.setPrefWidth(300);

                // Formata o campo de texto para aceitar apenas números inteiros
                TextFormatter<String> intTextFormatter = new TextFormatter<>(getChangeUnaryOperator("^\\d*$"));
                soldQuantityTextField.setTextFormatter(intTextFormatter);

                vBox.getChildren().addAll(productLabel, soldQuantityTextField);

                // Armazena o campo de texto no Map para posterior recuperação
                textFieldMap.put(product, soldQuantityTextField);
            }

            dialog.getDialogPane().setContent(vBox);

            // Processa o resultado após o usuário confirmar
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                // Processa cada produto e campo de texto
                for (Product product : selectedProducts) {
                    TextField soldQuantityTextField = textFieldMap.get(product);
                    if (soldQuantityTextField != null && !soldQuantityTextField.getText().isEmpty()) {

                        // Convertendo o valor do campo soldQuantityTextField para inteiro
                        int soldQuantityInt = Integer.parseInt(soldQuantityTextField.getText());

                        if (soldQuantityInt > 0) { // Só registra a venda caso ela seja maior que zero

                            if (product.getStock_quantity() - soldQuantityInt >= 0) { // Não Inserir venda caso a quantidade que deseja ser vendida seja maior do que o disponível no estoque

                                // Cria a venda para cada produto
                                Sale sale = new Sale();
                                sale.setProductId(product.getProduct_id());
                                sale.setQuantity(soldQuantityInt);
                                sale.setSalePrice(product.getRetail_price() * soldQuantityInt);
                                sale.setPriceUnit(product.getRetail_price());

                                // Adiciona a venda à lista
                                saleList.add(sale);

                                product.setStock_quantity(product.getStock_quantity() - soldQuantityInt);
                                productDAO.updateProduct(product);
                            } else {
                                genericAlertDialog(Alert.AlertType.INFORMATION, "", "Erro ao Registrar Venda de " + product.getProduct_name(), "A quantidade informada para venda é maior do que a disponível no estoque.");
                            }
                        }
                    }
                }

                salesDAO.insertSale(saleList); // Inserte as vendas
                fetchItems(); // Atualiza a tabela
            }
        } else {
            genericAlertDialog(Alert.AlertType.INFORMATION, "", "Selecione ao menos um produto antes de registrar uma venda", "");
        }


    }

    /**
     * Metodo chamado quando o botão de alertas é clicado.
     * Exibe um alerta informando quais produtos estão com estoque baixo (menos de 5 unidades).
     *
     * @param actionEvent Evento de clique do botão
     */
    public void onAlertsClicked(ActionEvent actionEvent) {
        List<Product> lowStockProducts = itemList.stream().filter(product -> product.getStock_quantity() < 5).toList(); // Filtra produtos com menos de 5 unidades em estoque

        Alert alert;
        if (!lowStockProducts.isEmpty()) {
            alert = new Alert(Alert.AlertType.WARNING); // Alerta de aviso
            alert.setHeaderText(getProp().getString("alert.window.title"));

            StringBuilder lowStockMessage = new StringBuilder();
            lowStockProducts.forEach(product -> lowStockMessage.append(product.getProduct_name()).append("\n"));
            alert.setContentText(lowStockMessage.toString()); // Adiciona os nomes dos produtos com baixo estoque

        } else {
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(getProp().getString("alert.window.title.no.low.stock"));
        }

        alert.show();

    }

    /**
     * Metodo chamado quando o campo de busca é alterado.
     * Atualiza a lista de produtos exibidos na tabela com base no texto de busca.
     *
     * @param actionEvent Evento de alteração do texto de busca
     */
    public void onSearchChanged(ActionEvent actionEvent) {

        try {
            fetchItems(); // Busca os itens com base no texto atual do campo de busca
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Exibe um alerta de aviso se houver produtos com baixo estoque (menos de 5 unidades).
     * O alerta é exibido apenas uma vez por execução do programa.
     */
    private void lowStockWarning() {

        List<Product> lowStockProducts = itemList.stream().filter(product -> product.getStock_quantity() < 5).toList(); // Filtra produtos com baixo estoque

        if (!lowStockProducts.isEmpty() && !dialogShown) { // Verifica se o alerta já foi mostrado

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(getProp().getString("alert.window.title"));

            StringBuilder lowStockMessage = new StringBuilder();
            lowStockProducts.forEach(product -> lowStockMessage.append(product.getProduct_name()).append("\n"));
            alert.setContentText(lowStockMessage.toString());

            alert.show();

            dialogShown = true; // Marca o alerta como mostrado

        }

    }

}
