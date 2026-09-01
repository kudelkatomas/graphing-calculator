import exceptions.FunctionDatabaseException;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.SQLException;

public class NewFunctionController {

    private Stage stage;
    private ObservableList<MyFunction> functions;

    @FXML private TextField txtFunction;
    @FXML private Text txtNotification;

    @FXML private void defineAction() {
        if (GraphingCalculator.functionEvaluator.isFunctionDefinition(txtFunction.getText())) {
            try (FunctionDatabaseClient client = new FunctionDatabaseClient();
                 FunctionDatabase database = new FunctionDatabase(client.getConnection())) {

                MyFunction function = database.createFunction(txtFunction.getText());
                functions.add(function);
                stage.close();

            } catch (SQLException | FunctionDatabaseException e) {
                Alert error = new Alert(Alert.AlertType.ERROR, e.getMessage());
                error.setTitle("Database Error");
                error.setHeaderText("Database Error Occurred");
            }
        } else {
            txtNotification.setText("Invalid function definition");
        }
    }

    @FXML private void cancelAction() {
        stage.close();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setFunctions(ObservableList<MyFunction> functions) {
        this.functions = functions;
    }
}
