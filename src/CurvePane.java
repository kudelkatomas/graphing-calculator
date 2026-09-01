import exceptions.InvalidFunctionDefinitionException;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

public class CurvePane extends Pane {

    private final MyFunction function;
    private final AxesPane axes;

    /**
     * Creates a CurvePane which is a Pane child with a function curve for the given function.
     * @param function a function which the curve represents
     * @param axes axes based on which it will place the curve
     * @throws InvalidFunctionDefinitionException if the function is not defined correctly
     */
    public CurvePane(MyFunction function, AxesPane axes) throws InvalidFunctionDefinitionException {
        this.function = function;
        this.axes = axes;

        Circle[] curve = createCurve();
        if (curve == null) return;

        for (Circle point : curve) {
            if (point == null) continue;

            getChildren().add(point);
        }
    }

    /**
     * Returns a curve represented as an array of circles.
     * If a circle is null, it means that f(x) is not defined for given x (e.g. 2/x is not defined for x=0).
     * @return null if the function is constant AND the value is out of visible range,
     * otherwise it returns an array of circles representing the curve
     * @throws InvalidFunctionDefinitionException if the function is not defined correctly
     */
    private Circle[] createCurve() throws InvalidFunctionDefinitionException {
        double[][] coordinates = calculateCoordinates();
        if (coordinates == null) return null;

        Circle[] curve = new Circle[coordinates.length];

        for (int i = 0; i < coordinates.length; i++) {
            if (coordinates[i] == null) {
                curve[i] = null;
                continue;
            }

            Circle point = new Circle();
            point.setRadius(GraphPane.FUNCTION_CURVE_THICKNESS);
            point.setTranslateX(coordinates[i][0]);
            point.setTranslateY(coordinates[i][1]);

            curve[i] = point;
        }

        return curve;
    }

    /**
     * Returns an array of coordinates [x, f(x)] but the values are pane coordinates relative to the axes.
     * @return null if the function is constant AND the value is out of visible range,
     * otherwise it returns an array of coordinates
     * @throws InvalidFunctionDefinitionException if the function is not defined correctly and also
     * if the function is constant and an ArithmeticException was raised during its evaluation
     */
    private double[][] calculateCoordinates() throws InvalidFunctionDefinitionException {
        int valuesCount = (int)Math.ceil(axes.getXAxisWidth());
        double[][] coordinates = new double[valuesCount][2];

        double fromX = axes.getXAxisLowerBound();
        double toX = axes.getXAxisUpperBound();
        double dx = (toX - fromX) / valuesCount;
        double x = fromX;
        double value;
        double offset = 1 + GraphPane.GRAPH_MARGIN / 2.0;

        if (function.isConstant()) {
            try {
                value = GraphingCalculator.functionEvaluator.evaluateFunction(function.functionDefinition(), 0);
            } catch (ArithmeticException e) {
                throw new InvalidFunctionDefinitionException(function.functionDefinition(), e.getMessage());
            }

            if (!axes.getYAxis().isValueOnAxis(value)) return null;

            double yDisplayPosition = offset + axes.getYAxis().getDisplayPosition(value);

            for (int i = 0; i < valuesCount; i++, x += dx) {
                coordinates[i][0] = offset + axes.getXAxis().getDisplayPosition(x);
                coordinates[i][1] = yDisplayPosition;
            }
        } else {
            for (int i = 0; i < valuesCount; i++, x += dx) {
                try {
                    value = GraphingCalculator.functionEvaluator.evaluateFunction(function.functionDefinition(), x);
                } catch (ArithmeticException e) {
                    coordinates[i] = null;
                    continue;
                }

                if (!axes.getYAxis().isValueOnAxis(value)) {
                    coordinates[i] = null;
                    continue;
                }

                coordinates[i][0] = offset + axes.getXAxis().getDisplayPosition(x);
                coordinates[i][1] = offset + axes.getYAxis().getDisplayPosition(value);
            }
        }

        return coordinates;
    }
}
