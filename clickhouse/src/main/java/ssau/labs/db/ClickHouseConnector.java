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
        try (FileInputStream inputStream = new FileInputStream("C:\\Users\\sirar\\IdeaProjects\\rdbms&nosql_labs\\clickhouse\\src\\main\\resources\\application.properties")) {
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
}
