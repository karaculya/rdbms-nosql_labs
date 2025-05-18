package ssau.labs.subscriber;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import ssau.labs.model.Artist;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

@Slf4j
public class RedisReceiver implements MessageListener {
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            Artist artist = deserializeArtist(message.getBody());
            log.info("Getting artist {}", artist);
        } catch (IOException | ClassNotFoundException e) {
            log.error("Error deserializing message: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private Artist deserializeArtist(byte[] body) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(body))) {
            return (Artist) ois.readObject();
        }
    }
}
