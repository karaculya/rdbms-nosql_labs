package ssau.labs.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import ssau.labs.model.Artist;
import ssau.labs.repo.CrudRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
public class ArtistRepository implements CrudRepository<Artist> {
    private final RedisTemplate redisTemplate;
    private static final String ARTIST_KEY_PREFIX = "artist:";
    private static final String ALL_ARTISTS_KEY = "all_artists";

    @Autowired
    public ArtistRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(Artist artist) {
        redisTemplate.opsForValue().set(ARTIST_KEY_PREFIX + artist.getId(), artist);
        redisTemplate.opsForSet().add(ALL_ARTISTS_KEY, artist.getId());
    }

    @Override
    public Artist findById(Integer id) {
        return (Artist) redisTemplate.opsForValue().get(ARTIST_KEY_PREFIX + id);
    }

    @Override
    public List<Artist> findAll() {
        Set<Object> artistIds = redisTemplate.opsForSet().members(ALL_ARTISTS_KEY);
        List<Artist> artists = new ArrayList<>();

        if (artistIds != null) {
            for (Object idObj : artistIds) {
                Integer id = (Integer) idObj;
                Artist artist = findById(id);
                if (artist != null) {
                    artists.add(artist);
                }
            }
        }
        return artists;
    }

    @Override
    public void update(Integer id, Artist artist) {
        artist.setId(id);
        save(artist);
    }

    @Override
    public void deleteById(Integer id) {
        redisTemplate.delete(ARTIST_KEY_PREFIX + id);
        redisTemplate.opsForSet().remove(ALL_ARTISTS_KEY, id);
    }
}