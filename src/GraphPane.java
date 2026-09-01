import exceptions.InvalidFunctionDefinitionException;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;

import java.util.HashMap;

public class GraphPane extends StackPane {

    // Ensure GRAPH_MARGIN is an even number.
    public static final int GRAPH_MARGIN = 30;
    public static final int DEFAULT_X_AXIS_RANGE = 20;
    public static final int FUNCTION_CURVE_THICKNESS = 1;
    public static final int NUMBER_OF_TICKS_ON_AXES = 10;

    private ObservableList<MyFunction> functions;
    private final HashMap<MyFunction, CurvePane> functionCurves = new HashMap<>();

    private final AxesPane axes = new AxesPane();

    public GraphPane() {
        getChildren().add(axes);
        enableZoom();
        addAxesListener();
    }

    public void setFunctions(ObservableList<MyFunction> functions) {
        this.functions = functions;
        functionCurves.clear();

        for (MyFunction function : functions) {
            addNewFunctionCurve(function);
        }

        addFunctionsListener();
    }

    private void addFunctionsListener() {
        this.functions.addListener((ListChangeListener<MyFunction>) c -> {
            while (c.next()) {
                if (c.wasRemoved()) {
                    for (MyFunction function : c.getRemoved()) {
                        deleteFunctionCurve(function);
                    }
                }
                if (c.wasAdded()) {
                    for (MyFunction function : c.getAddedSubList()) {
                        addNewFunctionCurve(function);
                    }
                }
            }
        });
    }

    private void addAxesListener() {
        axes.getYAxis().upperBoundProperty().addListener((observable, oldValue, newValue) -> drawFunctionCurves());
    }

    public void drawFunctionCurves() {
        for (MyFunction function : functions) {
            drawFunctionCurve(function);
        }
    }

    private void addNewFunctionCurve(MyFunction function) {
        if (functionCurves.containsKey(function)) return;

        functionCurves.put(function, null);

        drawFunctionCurve(function);
    }

    /**
     * Draws the function curve.
     * If an error was encountered during the function evaluation/application
     * a warning dialog will pop up and the function definition will be deleted.
     * @param function a function which the curve will represent
     */
    private void drawFunctionCurve(MyFunction function) {
        if (!functionCurves.containsKey(function)) return;

        if (functionCurves.get(function) != null) {
            eraseFunctionCurve(function);
        }

        try {
            CurvePane curve = new CurvePane(function, axes);
            functionCurves.put(function, curve);

            getChildren().add(curve);
        } catch (InvalidFunctionDefinitionException e) {
            String warning = String.format("%s%nThe function definition will be deleted.", e.getMessage());
            Alert warningDialog = new Alert(Alert.AlertType.WARNING, warning);
            warningDialog.setTitle("Invalid Function Definition");
            warningDialog.show();

            functions.remove(function);
        }
    }

    private void deleteFunctionCurve(MyFunction function) {
        eraseFunctionCurve(function);
        functionCurves.remove(function);
    }

    private void eraseFunctionCurve(MyFunction function) {
        getChildren().remove(functionCurves.get(function));
    }

    private void enableZoom() {
        setOnScroll(event -> {
            if (event.getDeltaY() > 0) axes.zoomIn();
            else if (event.getDeltaY() < 0) axes.zoomOut();
        });
    }
}
