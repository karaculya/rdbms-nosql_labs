package ssau.labs.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssau.labs.model.Artist;

@RestController
@RequestMapping("/publisher")
public class RedisPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;

    @Autowired
    public RedisPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic topic) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    @PostMapping
    public String publish(@RequestBody Artist artist) {
        if (artist == null || artist.getName() == null) {
            throw new IllegalArgumentException("Artist must not be null");
        }
        redisTemplate.convertAndSend(topic.getTopic(), artist);
        return "Artist with name " + artist.getName() + " was published";
    }
}
