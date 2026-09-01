package functionevaluators;

import exceptions.InvalidFunctionDefinitionException;

/**
 * This object can evaluate function definitions.
 */
public interface FunctionEvaluator {

    /**
     * Checks if given text is a valid function definition.
     * It might not be flawless; you should always do your own validation.
     * @param text the text to be checked
     * @return true if the text is a valid function definition, false otherwise
     */
    boolean isFunctionDefinition(String text);

    /**
     * Checks if given function definition defines a constant function.
     * The function is constant if the definition does not contain the variable x.
     * @param functionDefinition the function to be checked
     * @return true if the function is constant, false otherwise
     */
    boolean isConstantFunction(String functionDefinition);

    /**
     * Applies a function to a given value.
     * @param functionDefinition the function to be evaluated
     * @param x the value of x
     * @return the result of applying functionDefinition function to the given value of x
     * @throws InvalidFunctionDefinitionException if the function is not defined correctly,
     * it should also throw this exception if an ArithmeticException was raised during constant function
     * evaluation but this depends on the implementation
     * @throws ArithmeticException if there was an arithmetic error during the function evaluation/application
     */
    double evaluateFunction(String functionDefinition, double x)
            throws InvalidFunctionDefinitionException, ArithmeticException;
}