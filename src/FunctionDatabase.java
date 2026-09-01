import exceptions.FunctionDatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FunctionDatabase implements AutoCloseable {

    private PreparedStatement getAllFunctions;
    private PreparedStatement getFunctionByFunctionId;
    private PreparedStatement getLastFunctionId;
    private PreparedStatement createFunction;
    private PreparedStatement deleteFunction;

    public FunctionDatabase(Connection connection) throws FunctionDatabaseException {
        prepareStatements(connection);
    }

    private void prepareStatements(Connection connection) throws FunctionDatabaseException {
        try {
            getAllFunctions = connection.prepareStatement(
                    "SELECT * FROM " + FunctionDatabaseClient.DB_TABLE_NAME);
            getFunctionByFunctionId = connection.prepareStatement(
                    "SELECT * FROM " + FunctionDatabaseClient.DB_TABLE_NAME + " WHERE (function_id = ?)");
            getLastFunctionId = connection.prepareStatement(
                    "SELECT MAX(function_id) as function_id FROM " + FunctionDatabaseClient.DB_TABLE_NAME);
            createFunction = connection.prepareStatement(
                    "INSERT INTO " + FunctionDatabaseClient.DB_TABLE_NAME +" (function_id, function_definition, is_constant) VALUES (?, ?, ?)");
            deleteFunction = connection.prepareStatement(
                    "DELETE FROM " + FunctionDatabaseClient.DB_TABLE_NAME + " WHERE (function_id = ?)");

        } catch (SQLException e) {
            throw new FunctionDatabaseException("Unable to initialize prepared statements.", e);
        }
    }

    public List<MyFunction> getAllFunctions() throws FunctionDatabaseException {
        List<MyFunction> functions = new ArrayList<>();

        try (ResultSet results = getAllFunctions.executeQuery()) {
            while (results.next()) {
                MyFunction function = new MyFunction(results.getInt("function_id"),
                        results.getString("function_definition"),
                        results.getBoolean("is_constant"));

                functions.add(function);
            }

        } catch (SQLException e) {
            throw new FunctionDatabaseException("Unable to get all functions.", e);
        }

        return functions;
    }

    public MyFunction getFunctionByFunctionId(int functionId) throws FunctionDatabaseException {
        MyFunction function = null;

        try {
            getFunctionByFunctionId.setInt(1, functionId);

            try (ResultSet results = getFunctionByFunctionId.executeQuery()) {
                if (results.next()) {
                    function = new MyFunction(results.getInt("function_id"),
                            results.getString("function_definition"),
                            results.getBoolean("is_constant"));
                }
            }

        } catch (SQLException e) {
            throw new FunctionDatabaseException("Unable to get function by functionId.", e);
        }

        return function;
    }

    public MyFunction createFunction(String functionDefinition) throws FunctionDatabaseException {
        MyFunction function;

        try {
            ResultSet results = getLastFunctionId.executeQuery();
            int functionId = results.next() ? results.getInt("function_id") + 1 : 1;
            results.close();

            boolean isConstant = GraphingCalculator.functionEvaluator.isConstantFunction(functionDefinition);

            createFunction.setInt(1, functionId);
            createFunction.setString(2, functionDefinition);
            createFunction.setBoolean(3, isConstant);
            createFunction.executeUpdate();

            function = new MyFunction(functionId, functionDefinition, isConstant);

        } catch (SQLException e) {
            throw new FunctionDatabaseException("Unable to create function.", e);
        }

        return function;
    }

    public void deleteFunction(MyFunction function) throws FunctionDatabaseException {
        try {
            deleteFunction.setInt(1, function.functionId());
            deleteFunction.executeUpdate();
        } catch (SQLException e) {
            throw new FunctionDatabaseException("Unable to delete function.", e);
        }
    }

    @Override
    public void close() {
        try {
            getAllFunctions.close();
            getFunctionByFunctionId.close();
            getLastFunctionId.close();
            createFunction.close();
            deleteFunction.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            //e.printStackTrace();
        }
    }
}
