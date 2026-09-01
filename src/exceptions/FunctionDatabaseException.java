package exceptions;

public class FunctionDatabaseException extends Exception {
    public FunctionDatabaseException(String errMessage, Exception e) {
        super(errMessage, e);
    }
}
