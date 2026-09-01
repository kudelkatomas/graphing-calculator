import functionevaluators.FunctionEvaluator;
import functionevaluators.RPNFunctionEvaluator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Graphing calculator application.
 */
public class GraphingCalculator extends Application {

    public static final FunctionEvaluator functionEvaluator = new RPNFunctionEvaluator();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
        Parent root = fxmlLoader.load();

        MainWindowController controller = fxmlLoader.getController();
        controller.setPrimaryStage(primaryStage);

        primaryStage.setTitle("Graphing Calculator");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(616);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}