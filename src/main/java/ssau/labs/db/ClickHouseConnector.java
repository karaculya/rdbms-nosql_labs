package ssau.labs.db;

import ru.yandex.clickhouse.ClickHouseConnection;
import ru.yandex.clickhouse.ClickHouseDataSource;
import ru.yandex.clickhouse.settings.ClickHouseProperties;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class ClickHouseConnector {
    private ClickHouseDataSource dataSource;

    public ClickHouseConnector() {
        Properties properties = new Properties();
        ClickHouseProperties clickHouseProperties = new ClickHouseProperties();
        try (FileInputStream inputStream = new FileInputStream("src/main/resources/application.properties")) {
            properties.load(inputStream);
            clickHouseProperties.setUser(properties.getProperty("user"));
            clickHouseProperties.setPassword(properties.getProperty("password"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.dataSource = new ClickHouseDataSource(properties.getProperty("url"), clickHouseProperties);
    }

    public ClickHouseConnection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void execute(ClickHouseConnection connection, String query) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(query);
    }

    public static ResultSet executeQuery(ClickHouseConnection connection, String query) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public static boolean mutationIsDone(ClickHouseConnection connection, MutationChecker checker, String query) throws SQLException {
        ClickHouseConnector.execute(connection, query);
        String mutationId = checker.getLastMutationId();
        return checker.waitForMutation(mutationId);
    }
}
