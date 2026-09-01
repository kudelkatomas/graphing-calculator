package functionevaluators;

import exceptions.InvalidFunctionDefinitionException;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

/**
 * Simple Reverse Polish notation function evaluator.
 */
public class RPNFunctionEvaluator implements FunctionEvaluator {
    private final Map<String, UnaryOperator<Double>> unaryOperators = new HashMap<>();
    private final Map<String, BinaryOperator<Double>> binaryOperators = new HashMap<>();

    private final Stack<String> result = new Stack<>();

    private String expression;

    public RPNFunctionEvaluator() {
        initializeOperators();
    }

    private void initializeOperators() {
        initializeUnaryOperators();
        initializeBinaryOperators();
    }

    private void initializeUnaryOperators() {
        unaryOperators.put("sin", Math::sin);
        unaryOperators.put("cos", Math::cos);
        unaryOperators.put("sqrt", Math::sqrt);
        unaryOperators.put("log", Math::log);
        unaryOperators.put("tan", Math::tan);
        unaryOperators.put("atan", Math::atan);
        unaryOperators.put("abs", Math::abs);
    }

    private void initializeBinaryOperators() {
        binaryOperators.put("+", Double::sum);
        binaryOperators.put("-", (a, b) -> a - b);
        binaryOperators.put("*", (a, b) -> a * b);
        binaryOperators.put("/", (a, b) -> a / b);
        binaryOperators.put("pow", Math::pow);
    }

    private boolean isUnaryOperator(String atom) {
        return unaryOperators.containsKey(atom);
    }

    private boolean isBinaryOperator(String atom) {
        return binaryOperators.containsKey(atom);
    }

    private static boolean isNumber(String atom) {
        try {
            Double.parseDouble(atom);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static String formatToExpression(String text, double x) {
        return text.trim().toLowerCase().replaceAll("\\bx\\b", Double.toString(x));
    }

    /**
     * Checks if given text is a valid function definition.
     * It might not be flawless; you might need to do your own validation.
     * It generates 5 random numbers and checks if the function can be applied to at least one of them.
     * @param text the text to be checked
     * @return true if the text is a valid function definition, false otherwise
     */
    @Override
    public boolean isFunctionDefinition(String text) {
        if (text.isBlank()) return false;

        Random rand = new Random();

        for (int i = 0; i < 5; i ++) {
            double randomNumber = rand.nextDouble(-1000000, 1000000);

            try {
                evaluateFunction(text, randomNumber);
                return true;
            } catch (InvalidFunctionDefinitionException e) {
                return false;
            } catch (ArithmeticException ignored) {}
        }

        return false;
    }

    @Override
    public boolean isConstantFunction(String functionDefinition) {
        return !functionDefinition.matches(".*\\b[xX]\\b.*");
    }

    @Override
    public double evaluateFunction(String functionDefinition, double x) throws InvalidFunctionDefinitionException, ArithmeticException {
        this.result.clear();
        this.expression = formatToExpression(functionDefinition, x);

        if (this.expression.isBlank()) {
            throw new InvalidFunctionDefinitionException(functionDefinition, "The function definition is blank");
        }

        try {
            return evaluateExpression();
        } catch (ArithmeticException e) {
            /* If the function is constant and an ArithmeticException was raised,
               then the function is not defined correctly. */
            if (isConstantFunction(functionDefinition)){
                String errorMessage =
                        String.format(
                                "The function is constant and an ArithmeticException " +
                                        "with this message: \"%s\" was raised.", e.getMessage());

                throw new InvalidFunctionDefinitionException(functionDefinition, errorMessage);
            }

            String errorMessage =
                    String.format("ArithmeticException with this " +
                                    "message: \"%s\" was raised during \"f(x) = %s\" application to \"x = %f\".",
                            e.getMessage(), functionDefinition, x);

            throw new ArithmeticException(errorMessage);

        } catch (InvalidFunctionDefinitionException e) {
            String receivedErrorMessage = e.getMessage() == null ? "" : e.getMessage();

            throw new InvalidFunctionDefinitionException(functionDefinition, receivedErrorMessage);
        }
    }

    private double evaluateExpression() throws InvalidFunctionDefinitionException, ArithmeticException {
        for (String atom : expression.split(" ")) {
            if (!atom.isBlank()) processAtom(atom);
        }

        try {
            return Double.parseDouble(result.pop());
        } catch (EmptyStackException e) {
            throw new InvalidFunctionDefinitionException();
        }
    }

    private void processAtom(String atom) throws InvalidFunctionDefinitionException, ArithmeticException {
        if (isNumber(atom)) result.push(atom);
        else if (isUnaryOperator(atom)) applyUnaryOperator(atom);
        else if (isBinaryOperator(atom)) applyBinaryOperator(atom);
        else throw new InvalidFunctionDefinitionException(String.format("Unknown atom: %s.", atom));
    }

    private void applyUnaryOperator(String operator) throws InvalidFunctionDefinitionException, ArithmeticException {
        UnaryOperator<Double> unaryOperator = unaryOperators.get(operator);

        try {
            double a = Double.parseDouble(result.pop());
            double applicationResult = unaryOperator.apply(a);

            result.push(Double.toString(applicationResult));
        } catch (ArithmeticException e) {
            throw new ArithmeticException(e.getMessage());
        } catch (EmptyStackException e) {
            throw new InvalidFunctionDefinitionException(
                    String.format("Not enough operands for unary operator: %s.", operator));
        } catch (Exception e) {
            throw new InvalidFunctionDefinitionException();
        }
    }

    private void applyBinaryOperator(String operator) throws InvalidFunctionDefinitionException, ArithmeticException {
        BinaryOperator<Double> binaryOperator = binaryOperators.get(operator);

        try {
            double b = Double.parseDouble(result.pop());
            double a = Double.parseDouble(result.pop());

            if (operator.equals("/") && b == 0) throw new ArithmeticException("Division by zero.");

            double applicationResult = binaryOperator.apply(a, b);

            result.push(Double.toString(applicationResult));
        } catch (ArithmeticException e) {
            throw new ArithmeticException(e.getMessage());
        } catch (EmptyStackException e) {
            throw new InvalidFunctionDefinitionException(
                    String.format("Not enough operands for binary operator: %s.", operator));
        }
        catch (Exception e) {
            throw new InvalidFunctionDefinitionException();
        }
    }
}
