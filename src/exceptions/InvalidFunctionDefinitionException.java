package exceptions;

/**
 * Indicates that given function definition is not valid.
 */
public class InvalidFunctionDefinitionException extends Exception {
    public InvalidFunctionDefinitionException(String errMessage) {
        super(errMessage);
    }

    public InvalidFunctionDefinitionException(String functionDefinition, String details) {
        super(String.format("Invalid function definition: \"%s\". Details: %s", functionDefinition, details));
    }

    public InvalidFunctionDefinitionException() { }
}
