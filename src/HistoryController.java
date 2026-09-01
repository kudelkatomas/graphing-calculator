import exceptions.FunctionDatabaseException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Optional;

public class HistoryController {

    private Stage stage;
    private ObservableList<MyFunction> functions;
    private final ObservableList<MyFunction> functionsHistory = FXCollections.observableArrayList();

    @FXML private ListView<MyFunction> functionsHistoryView;

    @FXML public void initialize() {
        initializeFunctionsHistory();
    }

    @FXML private void functionsHistoryViewMouseClickedAction(MouseEvent event) {
        switch (event.getButton()) {
            case PRIMARY: functionsHistoryViewAddFunctionToFunctionsAction(); break;
            case SECONDARY: functionsHistoryViewDeleteFunctionAction(); break;
        }
    }

    @FXML private void functionsHistoryViewKeyPressedAction(KeyEvent event) {
        switch (event.getCode()) {
            case ENTER: functionsHistoryViewAddFunctionToFunctionsAction(); break;
            case DELETE: functionsHistoryViewDeleteFunctionAction(); break;
        }
    }

    private void functionsHistoryViewAddFunctionToFunctionsAction() {
        MyFunction selectedItem = functionsHistoryView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) return;

        Alert additionConfirmationDialog = new Alert(
                Alert.AlertType.CONFIRMATION, "Do you wish to add this function?");

        additionConfirmationDialog.setTitle("Confirm Addition");
        additionConfirmationDialog.setHeaderText(selectedItem.toString());

        Optional<ButtonType> result = additionConfirmationDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK && !functions.contains(selectedItem)) {
            functions.add(selectedItem);
        }
    }

    private void functionsHistoryViewDeleteFunctionAction() {
        MyFunction selectedItem = functionsHistoryView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) return;

        Alert deletionConfirmationDialog = new Alert(
                Alert.AlertType.CONFIRMATION, "Do you wish to delete this function from history?");

        deletionConfirmationDialog.setTitle("Confirm Deletion");
        deletionConfirmationDialog.setHeaderText(selectedItem.toString());

        Optional<ButtonType> result = deletionConfirmationDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) removeFunctionFromHistory(selectedItem);
    }

    private void removeFunctionFromHistory(MyFunction function) {
        try (FunctionDatabaseClient client = new FunctionDatabaseClient();
             FunctionDatabase database = new FunctionDatabase(client.getConnection())) {

            database.deleteFunction(function);
            functionsHistory.remove(function);

        } catch (SQLException | FunctionDatabaseException e) {
            Alert error = new Alert(Alert.AlertType.ERROR, e.getMessage());
            error.setTitle("Database Error");
            error.setHeaderText("Database Error Occurred");
        }
    }

    private void initializeFunctionsHistory() {
        try (FunctionDatabaseClient client = new FunctionDatabaseClient();
             FunctionDatabase database = new FunctionDatabase(client.getConnection())) {

            functionsHistory.clear();
            functionsHistory.addAll(database.getAllFunctions());
            functionsHistoryView.setItems(functionsHistory);

        } catch (SQLException | FunctionDatabaseException e) {
            //System.out.println(e.getMessage());

            Alert error = new Alert(
                    Alert.AlertType.ERROR,
                    "Something went wrong when loading history from the database.");
            error.setTitle("Database Error");
            error.setHeaderText("Database Error Occurred");
            error.show();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setFunctions(ObservableList<MyFunction> functions) {
        this.functions = functions;
    }
}
