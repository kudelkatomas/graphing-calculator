import java.sql.*;

public class FunctionDatabaseClient implements AutoCloseable {

    public static final String DB_NAME = "graphing_calculator_db";
    public static final String DB_TABLE_NAME = "FUNCTION_DB";
    private Connection connection = null;

    public FunctionDatabaseClient() throws SQLException {
        connect();
    }

    private static boolean isReady(Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet tables = metaData.getTables(null, null, DB_TABLE_NAME, null)) {
            return tables.next();
        }
    }

    private static void initializeTable(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.addBatch("CREATE TABLE " + DB_TABLE_NAME + "\n" +
                    "(function_id INTEGER NOT NULL," +
                    " function_definition VARCHAR(100) NOT NULL,\n" +
                    " is_constant BOOLEAN NOT NULL,\n" +
                    "PRIMARY KEY (function_id))");

            stmt.executeBatch();
        }
    }

    public static void main(String[] args) throws SQLException {
        String connectionURL = "jdbc:derby:" + DB_NAME + ";create=true";

        try (Connection connection = DriverManager.getConnection(connectionURL)) {
            if (!isReady(connection)) {
                initializeTable(connection);
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void connect() throws SQLException {
        String connectionURL = "jdbc:derby:" + DB_NAME + ";create=true";
        connection = DriverManager.getConnection(connectionURL);
        if (!isReady(connection)) { initializeTable(connection); }
    }

    @Override
    public void close() {
        try {
            connection.close();
            connection = null;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            //e.printStackTrace();
        }
    }
}
