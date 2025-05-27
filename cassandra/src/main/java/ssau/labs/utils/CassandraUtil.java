package ssau.labs.utils;

import com.datastax.oss.driver.api.core.CqlSession;

import java.io.IOException;
import java.net.InetSocketAddress;

public class CassandraUtil {
    private static CqlSession session;

    public static void init() throws IOException {
        if (session == null) {
            PropertiesLoader loader = PropertiesLoader.getInstance();
            String url = loader.getProperties().getProperty("url");
            int port = Integer.parseInt(loader.getProperties().getProperty("port"));
            String datacenter = loader.getProperties().getProperty("datacenter");
            String keyspace = loader.getProperties().getProperty("keyspace");
            session = CqlSession.builder()
                    .addContactPoint(new InetSocketAddress(url, port))
                    .withLocalDatacenter(datacenter)
                    .build();

            session.execute("CREATE KEYSPACE IF NOT EXISTS " + keyspace +
                    " WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}");
            session.execute("USE " + keyspace);
        }
    }

    public static CqlSession getSession() throws IOException {
        if (session == null) {
            init();
        }
        return session;
    }

    public static void closeSession() {
        if (session != null) {
            session.close();
        }
    }
}
