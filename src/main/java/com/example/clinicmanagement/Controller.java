package com.example.clinicmanagement;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.Set;

public class Controller implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TableView<User> tableView;

    @FXML
    private TableColumn<User, String> nameColumn;

    @FXML
    private TableColumn<User, String> nikColumn;

    @FXML
    private TableColumn<User, LocalDate> birthDateColumn;

    @FXML
    private TableColumn<User, String> addressColumn;

    //Text input
    @FXML
    private TextField nameInput;

    @FXML
    private TextField nikInput;

    @FXML
    private DatePicker birthDateInput;

    @FXML
    private TextField addressInput;

    private Database database;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            this.database = new Database();
            ObservableList<User> users = this.database.getAll();
            if (users != null) tableView.setItems(users);

            nameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("name"));
            nikColumn.setCellValueFactory(new PropertyValueFactory<User, String>("nik"));
            birthDateColumn.setCellValueFactory(new PropertyValueFactory<User, LocalDate>("birthDate"));
            addressColumn.setCellValueFactory(new PropertyValueFactory<User, String>("address"));

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd");
            birthDateColumn.setCellFactory(myDateTableCell -> new TableCell<User, LocalDate>() {
                @Override
                protected void updateItem(LocalDate date, boolean dateIsEmpty) {
                    super.updateItem(date, dateIsEmpty);
                    if (date == null || dateIsEmpty) {
                        setText(null);
                    } else {
                        setText(date.format(dateFormatter));
                    }
                }
            });

            birthDateInput.setConverter(new StringConverter<LocalDate>() {
                @Override
                public String toString(LocalDate date) {
                    if (date != null) return dateFormatter.format(date);
                    else return "";
                }

                @Override
                public LocalDate fromString(String string) {
                    if (string != null && !string.isEmpty()) return LocalDate.parse(string, dateFormatter);
                    else return null;
                }
            });

            tableView.getSelectionModel().selectedItemProperty().addListener(((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    nameInput.setText(newSelection.getName());
                    nikInput.setText(newSelection.getNik());
                    nikInput.setDisable(true);
                    birthDateInput.setValue(newSelection.getBirthDate());
                    addressInput.setText(newSelection.getAddress());
                } else {
                    nikInput.setDisable(false);
                }
            }));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Something Wrong", e.getMessage());
        }
    }

    @FXML
    void reset() {
        checkEmptyFields(true);
        tableView.getSelectionModel().clearSelection();
    }

    @FXML
    void submit() {
        try {
            if (checkEmptyFields(false)) {
                showAlert(Alert.AlertType.ERROR, "Invalid Entries", "Invalid Entries! Make sure the fields are not empty and valid");
                return;
            }

            ObservableList<User> users = tableView.getItems();
            User user = new User(nameInput.getText(),
                    nikInput.getText(),
                    birthDateInput.getValue(),
                    addressInput.getText());

            if (tableView.getSelectionModel().getSelectedIndex() > -1) {
                User updateUser = database.update(user);
                if (updateUser == null) return;
                users.set(tableView.getSelectionModel().getSelectedIndex(), user);
            } else {
                User inserUser = database.insert(user);
                if (inserUser == null) return;
                users.add(inserUser);
            }

            tableView.setItems(users);
            showAlert(Alert.AlertType.INFORMATION, "Insert Successfully", "Data Successfully Inserted");
            reset();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to Submit Data", e.getMessage());
        }
    }

    @FXML
    void delete() {
        try {
            int selectedID = tableView.getSelectionModel().getSelectedIndex();
            if (selectedID > -1) {
                User deleteUser = database.delete(tableView.getSelectionModel().getSelectedItem());
                if (deleteUser == null) {
                    return;
                }

                tableView.getItems().remove(selectedID);
                showAlert(Alert.AlertType.INFORMATION, "Delete Successfully", "Data Successfully Deleted");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to Delete Data", e.getMessage());
        }
    }

    @FXML
    void selectNext() {
        tableView.getSelectionModel().selectNext();
    }

    @FXML
    void selectPrevious() {
        if (tableView.getSelectionModel().getSelectedIndex() < 0) {
            tableView.getSelectionModel().selectFirst();
        } else {
            tableView.getSelectionModel().selectPrevious();
        }
    }

    @FXML
    void exitApps() {
        System.exit(-1);
    }

    void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    boolean checkEmptyFields(Boolean isReset) {
        boolean isError = false;
        Set<Node> textFields = anchorPane.lookupAll(".text-field-element");

        for (Node textField : textFields) {
            if (!isReset && ((textField instanceof TextField && ((TextField) textField).getText().isEmpty()) ||
                    (textField instanceof DatePicker && ((DatePicker) textField).getValue() == null)))
            {
                // set error message as visible
                Label label = (Label) anchorPane.lookup(("#" + textField.getId()).replace("Input", "Alert"));
                if (label != null) label.setText(textField.getId().replace("Input", "") + " can't be empty!");

                // show error border of text-field
                textField.setStyle("-fx-border-color: red");
                isError = true;
            } else {
                // hide error message
                Label label = (Label) anchorPane.lookup(("#" + textField.getId()).replace("Input", "Alert"));
                label.setText("");

                // hide error border of text-field
                if (isReset && textField instanceof TextField) {
                    ((TextField) textField).setText("");
                } else if (isReset) {
                    ((DatePicker) textField).setValue(null);
                }

                textField.setStyle("-fx-border-color: transparent");
            }
        }

        return isError;
    }


}