import javafx.geometry.Side;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.Pane;

public class AxesPane extends Pane {

    private final NumberAxis xAxis = new NumberAxis();
    private final NumberAxis yAxis = new NumberAxis();

    public AxesPane() {
        disableAxesAutoRanging();
        setAxesSides();
        initializeAxesBindings();

        getChildren().addAll(xAxis, yAxis);
    }

    private void disableAxesAutoRanging() {
        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);
    }

    private void setAxesSides() {
        xAxis.setSide(Side.BOTTOM);
        yAxis.setSide(Side.LEFT);
    }

    private void initializeAxesBindings() {
        bindAxesPosition();
        bindAxesSize();
        bindAxesRange();
    }

    private void bindAxesPosition() {
        xAxis.setLayoutX(GraphPane.GRAPH_MARGIN / 2.0);
        yAxis.setLayoutY(GraphPane.GRAPH_MARGIN / 2.0);

        xAxis.layoutYProperty().bind(heightProperty().divide(2));
        yAxis.layoutXProperty().bind(
                xAxis.widthProperty()
                        .add(GraphPane.GRAPH_MARGIN)
                        .divide(2.0)
                        .subtract(yAxis.widthProperty())
                        .add(1));
    }

    private void bindAxesSize() {
        widthProperty().addListener(
                (observable, oldValue, newValue) ->
                        xAxis.setPrefWidth(newValue.doubleValue() - GraphPane.GRAPH_MARGIN));

        heightProperty().addListener(
                (observable, oldValue, newValue) ->
                        yAxis.setPrefHeight(newValue.doubleValue() - GraphPane.GRAPH_MARGIN));
    }

    private void bindAxesRange() {
        setXAxisRange(GraphPane.DEFAULT_X_AXIS_RANGE);

        xAxis.widthProperty().addListener(observable -> updateYAxisRange());
        yAxis.heightProperty().addListener(observable -> updateYAxisRange());

        xAxis.lowerBoundProperty().addListener(observable -> updateYAxisRange());
        xAxis.upperBoundProperty().addListener(observable -> updateYAxisRange());
    }

    private void updateYAxisRange() {
        double unitSize = xAxis.widthProperty().divide(getXAxisRange()).get();
        double yAxisRange = yAxis.heightProperty().divide(unitSize).get();

        setYAxisRange(yAxisRange);
    }

    public double getXAxisRange() {
        return xAxis.getUpperBound() - xAxis.getLowerBound();
    }

    public double getYAxisRange() { return yAxis.getUpperBound() - yAxis.getLowerBound(); }

    private void setXAxisRange(double range) {
        setXAxisBounds(0 - range / 2, range / 2);
    }

    private void setYAxisRange(double range) {
        setYAxisBounds(0 - range / 2, range / 2);
    }

    private void setXAxisBounds(double lowerBound, double upperBound) {
        xAxis.setLowerBound(lowerBound);
        xAxis.setUpperBound(upperBound);

        updateXAxisTickUnit();
    }

    private void setYAxisBounds(double lowerBound, double upperBound) {
        yAxis.setLowerBound(lowerBound);
        yAxis.setUpperBound(upperBound);

        updateYAxisTickUnit();
    }

    public double getXAxisLowerBound() {
        return xAxis.getLowerBound();
    }

    public double getXAxisUpperBound() {
        return xAxis.getUpperBound();
    }

    public double getXAxisWidth() {
        return xAxis.getWidth();
    }

    private void updateXAxisTickUnit() {
        xAxis.setTickUnit(getXAxisRange() / GraphPane.NUMBER_OF_TICKS_ON_AXES);
    }

    private void updateYAxisTickUnit() {
        yAxis.setTickUnit(getYAxisRange() / GraphPane.NUMBER_OF_TICKS_ON_AXES);
    }

    public NumberAxis getXAxis() {
        return xAxis;
    }

    public NumberAxis getYAxis() {
        return yAxis;
    }

    public void zoomIn() {
        setXAxisRange(getXAxisRange() * 0.5);
    }

    public void zoomOut() {
        setXAxisRange(getXAxisRange() * 2);
    }
}
