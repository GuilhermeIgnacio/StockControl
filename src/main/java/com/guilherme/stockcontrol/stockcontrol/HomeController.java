package com.guilherme.stockcontrol.stockcontrol;

import com.guilherme.stockcontrol.stockcontrol.dao.StockDAO;
import com.guilherme.stockcontrol.stockcontrol.model.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.guilherme.stockcontrol.stockcontrol.Util.*;
import static com.guilherme.stockcontrol.stockcontrol.Util.getProp;

public class HomeController implements Initializable {

    public TableView itemTableView;

    //Table Columns
    public TableColumn<Item, String> idTableColumn;
    public TableColumn<Item, String> nameTableColumn;
    public TableColumn<Item, String> descriptionTableColumn;
    public TableColumn<Item, String> quantityTableColumn;
    public TableColumn<Item, String> salesTableColumn;
    public TableColumn<Item, Float> purchasePriceTableColumn;
    public TableColumn<Item, Float> retailPriceTableColumn;
    public TableColumn<Item, LocalDateTime> createdAtTableColumn;
    public TableColumn<Item, LocalDateTime> updatedAtTableColumn;
    //Table Columns

    public Button editBtn;
    public Button deleteBtn;
    public Button registerSaleBtn;

    ObservableList<Item> itemList = FXCollections.observableArrayList();

    StockDAO stockDAO = new StockDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        fetchItems();

        idTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameTableColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        descriptionTableColumn.setCellValueFactory(new PropertyValueFactory<>("itemDescription"));
        quantityTableColumn.setCellValueFactory(new PropertyValueFactory<>("itemQuantity"));
        salesTableColumn.setCellValueFactory(new PropertyValueFactory<>("itemSales"));
        purchasePriceTableColumn.setCellValueFactory(new PropertyValueFactory<>("purchasePrice"));
        retailPriceTableColumn.setCellValueFactory(new PropertyValueFactory<>("retailPrice"));
        createdAtTableColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        updatedAtTableColumn.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));

        purchasePriceTableColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Item, Float> call(TableColumn<Item, Float> param) {
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
            public TableCell<Item, Float> call(TableColumn<Item, Float> param) {
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

        createdAtTableColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Item, LocalDateTime> call(TableColumn<Item, LocalDateTime> param) {
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
            public TableCell<Item, LocalDateTime> call(TableColumn<Item, LocalDateTime> param) {
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

    }

    public void onAddNewItemBtnClicked(ActionEvent actionEvent) throws Exception {

        InsertItemApplication insertItemApplication = new InsertItemApplication();
        Stage stage = new Stage();

        insertItemApplication.start(stage);

        fetchItems();

    }

    private void fetchItems() {
        try {
            itemList.clear();

            for (Item item : stockDAO.fetchItems()) {

                Item newItem = new Item();
                newItem.setId(item.getId());
                newItem.setItemName(item.getItemName());
                newItem.setItemDescription(item.getItemDescription());
                newItem.setItemQuantity(item.getItemQuantity());
                newItem.setItemSales(item.getItemSales());
                newItem.setPurchasePrice(item.getPurchasePrice());
                newItem.setRetailPrice(item.getRetailPrice());
                newItem.setCreatedAt(item.getCreatedAt());
                newItem.setUpdatedAt(item.getUpdatedAt());


                // Custom CellFactory para createdAt


                itemList.add(newItem);
                itemTableView.setItems(itemList);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onEditBtnClicked(ActionEvent actionEvent) throws Exception {

        if (itemTableView.getSelectionModel().getSelectedItem() != null) {

            Item selectedItem = (Item) itemTableView.getSelectionModel().getSelectedItem();

            InsertItemApplication application = new InsertItemApplication();
            Stage stage = new Stage();

            application.selectedItem = selectedItem;

            application.start(stage);

            fetchItems();

        }

    }

    public void onDeleteBtnClicked(ActionEvent actionEvent) {
        if (itemTableView.getSelectionModel().getSelectedItem() != null) {

            Item selectedItem = (Item) itemTableView.getSelectionModel().getSelectedItem();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(getProp().getString("delete.confirm.message"));
            alert.setHeaderText(getProp().getString("delete.warning"));

            ButtonType buttonTypeOne = new ButtonType(getProp().getString("delete.confirm.button"));
            ButtonType buttonTypeCancel = new ButtonType(getProp().getString("delete.cancel.button"), ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonTypeOne) {

                stockDAO.deleteItemById(selectedItem.getId());
                fetchItems();

            } else {
                //User Cancelled Item Deletion
                System.out.println("Item Deletion Canceled");
            }
        }
    }

    public void onRefreshBtnClicked(ActionEvent actionEvent) {
        fetchItems();
    }

    public void onItemClicked(MouseEvent mouseEvent) throws IOException {

        if (mouseEvent.getClickCount() == 2) {

            if (itemTableView.getSelectionModel().getSelectedItem() != null) {
                Item selectedItem = (Item) itemTableView.getSelectionModel().getSelectedItem();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("item-detail-view.fxml"));
                loader.setResources(getProp());

                Stage stage = new Stage(StageStyle.DECORATED);
                stage.setScene(new Scene(loader.load()));
                stage.setTitle(selectedItem.getItemName());
                stage.setMinWidth(720);
                stage.setMinHeight(520);

                ItemDetailController controller = loader.getController();
                controller.getItem(selectedItem);

                stage.showAndWait();
            }

        }


    }

    public void onRegisterSaleClicked(ActionEvent actionEvent) {

        if (itemTableView.getSelectionModel().getSelectedItem() != null) {

            Item selectedItem = (Item) itemTableView.getSelectionModel().getSelectedItem();

            TextInputDialog dialog = new TextInputDialog();
            dialog.setHeaderText(getProp().getString("register.sale.header.text"));
            dialog.setContentText(getProp().getString("register.sale.content.text"));

            TextFormatter<String> intTextFormatter = new TextFormatter<>(getChangeUnaryOperator("^?\\d*$"));
            dialog.getEditor().setTextFormatter(intTextFormatter);

            Optional<String> result = dialog.showAndWait();

            if (result.isPresent() && !dialog.getEditor().getText().isEmpty()) {

                selectedItem.setItemSales(Integer.parseInt(dialog.getEditor().getText()) + selectedItem.getItemSales());

                stockDAO.updateItem(selectedItem);

                fetchItems();

            }

        }

    }

}
