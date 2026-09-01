import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Main window controller.
 */
public class MainWindowController implements Initializable {

    private Stage primaryStage;

    private final ObservableList<MyFunction> functions = FXCollections.observableArrayList();
    private final StringProperty status = new SimpleStringProperty();

    @FXML private BorderPane mainBorderPane;
    @FXML private ListView<MyFunction> functionsView;
    private GraphPane graph;
    @FXML private Text txtStatus;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        functionsView.setItems(functions);

        addStatusListener();

        initializeGraph();
    }

    private void initializeGraph() {
        setStatus("Initializing graph...");

        graph = new GraphPane();
        mainBorderPane.setCenter(graph);
        graph.setFunctions(functions);

        setStatus("Ready...");
    }

    private void addStatusListener() {
        txtStatus.textProperty().bind(status);
        status.addListener((observable, oldValue, newValue) -> System.out.println(newValue));
    }

    @FXML private void reloadGraphAction() {
        graph.drawFunctionCurves();
    }

    @FXML private void exportHistoryToXMLAction() {
        Alert exportHistoryConfirmationDialog = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Do you wish to export the history to XML?");

        exportHistoryConfirmationDialog.setTitle("Confirm Export");
        exportHistoryConfirmationDialog.setHeaderText("Export History to XML");

        Optional<ButtonType> result = exportHistoryConfirmationDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) exportHistoryToXML();
    }

    private void exportHistoryToXML() {
        try {
            setStatus("Exporting history to XML...");
            Path outputFilePath = Path.of("src", "history.xml");
            XMLHistoryWriter.exportFunctionDatabaseToXML(outputFilePath);
        } catch (Exception e) {
            //System.out.println(e.getMessage());

            Alert error = new Alert(
                    Alert.AlertType.ERROR,
                    "Something went wrong during history export to XML.");

            error.setTitle("History Export Error");
            error.setHeaderText("History Export Error Occurred");
            error.show();
        } finally {
            setStatus("Ready...");
        }
    }

    @FXML private void closeAppAction() {
        primaryStage.close();
    }

    @FXML private void addFunctionAction() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("NewFunction.fxml"));
        Parent root = fxmlLoader.load();

        Stage stage = new Stage();
        NewFunctionController controller = fxmlLoader.getController();
        controller.setStage(stage);
        controller.setFunctions(functions);

        stage.initOwner(primaryStage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Define a function");
        stage.centerOnScreen();

        Scene scene = new Scene(root, 400, 250);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("NewFunction.css")).toString());
        stage.setScene(scene);
        stage.show();
    }

    @FXML private void showHistoryAction() throws IOException {
        setStatus("Loading history...");

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("History.fxml"));
        Parent root = fxmlLoader.load();

        Stage stage = new Stage();
        HistoryController controller = fxmlLoader.getController();
        controller.setStage(stage);
        controller.setFunctions(functions);

        stage.initOwner(primaryStage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("History");
        stage.centerOnScreen();

        Scene scene = new Scene(root, 400, 400);
        stage.setScene(scene);
        stage.show();

        setStatus("Ready...");
    }

    @FXML private void deleteFunctionsAction() {
        Alert deleteConfirmationDialog = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Do you wish to delete all function definitions? History will not be affected.");

        deleteConfirmationDialog.setTitle("Confirm Deletion");
        deleteConfirmationDialog.setHeaderText("Delete All Functions");

        Optional<ButtonType> result = deleteConfirmationDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) functions.clear();
    }

    @FXML private void functionsViewMouseClickedAction() {
        functionsViewDeleteFunctionAction();
    }

    @FXML private void functionsViewKeyPressedAction(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER) ||
            event.getCode().equals(KeyCode.DELETE)) {
            functionsViewDeleteFunctionAction();
        }
    }

    private void functionsViewDeleteFunctionAction() {
        MyFunction selectedItem = functionsView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) return;

        Alert deleteConfirmationDialog = new Alert(
                Alert.AlertType.CONFIRMATION, "Do you wish to delete this function? History will not be affected.");

        deleteConfirmationDialog.setTitle("Confirm Deletion");
        deleteConfirmationDialog.setHeaderText(selectedItem.toString());

        Optional<ButtonType> result = deleteConfirmationDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) functions.remove(selectedItem);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private void setStatus(String status) {
        this.status.set(status);
    }
}