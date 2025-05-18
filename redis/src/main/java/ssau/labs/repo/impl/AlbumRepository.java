package ssau.labs.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import ssau.labs.model.Album;
import ssau.labs.repo.CrudRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
public class AlbumRepository implements CrudRepository<Album> {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String ALBUM_KEY_PREFIX = "album:";
    private static final String ALL_ALBUMS_KEY = "all_albums";
    private static final String ARTIST_ALBUMS_PREFIX = "artist_albums:";

    @Autowired
    public AlbumRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(Album album) {
        redisTemplate.opsForValue().set(ALBUM_KEY_PREFIX + album.getId(), album);
        redisTemplate.opsForSet().add(ALL_ALBUMS_KEY, album.getId());
        redisTemplate.opsForSet().add(ARTIST_ALBUMS_PREFIX + album.getArtistId(), album.getId());
    }

    @Override
    public Album findById(Integer id) {
        return (Album) redisTemplate.opsForValue().get(ALBUM_KEY_PREFIX + id);
    }

    @Override
    public void deleteById(Integer id) {
        Album album = findById(id);
        if (album != null) {
            redisTemplate.delete(ALBUM_KEY_PREFIX + id);
            redisTemplate.opsForSet().remove(ALL_ALBUMS_KEY, id);
            redisTemplate.opsForSet().remove(ARTIST_ALBUMS_PREFIX + album.getArtistId(), id);
        }
    }

    @Override
    public List<Album> findAll() {
        Set<Object> albumIds = redisTemplate.opsForSet().members(ALL_ALBUMS_KEY);
        List<Album> albums = new ArrayList<>();

        if (albumIds != null) {
            for (Object idObj : albumIds) {
                Integer id = (Integer) idObj;
                Album album = findById(id);
                if (album != null) {
                    albums.add(album);
                }
            }
        }
        return albums;
    }

    @Override
    public void update(Integer id, Album album) {
        album.setId(id);
        save(album);
    }

    public List<Album> findByArtistId(Integer artistId) {
        Set<Object> albumIds = redisTemplate.opsForSet().members(ARTIST_ALBUMS_PREFIX + artistId);
        List<Album> albums = new ArrayList<>();

        if (albumIds != null) {
            for (Object idObj : albumIds) {
                Integer id = (Integer) idObj;
                Album album = findById(id);
                if (album != null) {
                    albums.add(album);
                }
            }
        }
        return albums;
    }
}
