package ssau.labs.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import ssau.labs.model.Composition;
import ssau.labs.repo.CrudRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
public class CompositionRepository implements CrudRepository<Composition> {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String COMPOSITION_KEY_PREFIX = "composition:";
    private static final String ALL_COMPOSITIONS_KEY = "all_compositions";
    private static final String ALBUM_COMPOSITIONS_PREFIX = "album_compositions:";

    @Autowired
    public CompositionRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(Composition composition) {
        redisTemplate.opsForValue().set(COMPOSITION_KEY_PREFIX + composition.getId(), composition);
        redisTemplate.opsForSet().add(ALL_COMPOSITIONS_KEY, composition.getId());
        redisTemplate.opsForSet().add(ALBUM_COMPOSITIONS_PREFIX + composition.getAlbumId(), composition.getId());
    }

    @Override
    public Composition findById(Integer id) {
        return (Composition) redisTemplate.opsForValue().get(COMPOSITION_KEY_PREFIX + id);
    }

    @Override
    public void deleteById(Integer id) {
        Composition composition = findById(id);
        if (composition != null) {
            redisTemplate.delete(COMPOSITION_KEY_PREFIX + id);
            redisTemplate.opsForSet().remove(ALL_COMPOSITIONS_KEY, id);
            redisTemplate.opsForSet().remove(ALBUM_COMPOSITIONS_PREFIX + composition.getAlbumId(), id);
        }
    }

    @Override
    public List<Composition> findAll() {
        Set<Object> compositionIds = redisTemplate.opsForSet().members(ALL_COMPOSITIONS_KEY);
        List<Composition> compositions = new ArrayList<>();

        if (compositionIds != null) {
            for (Object idObj : compositionIds) {
                Integer id = (Integer) idObj;
                Composition composition = findById(id);
                if (composition != null) {
                    compositions.add(composition);
                }
            }
        }
        return compositions;
    }

    @Override
    public void update(Integer id, Composition composition) {
        composition.setId(id);
        save(composition);
    }

    public List<Composition> findByAlbumId(Integer albumId) {
        Set<Object> compositionIds = redisTemplate.opsForSet().members(ALBUM_COMPOSITIONS_PREFIX + albumId);
        List<Composition> compositions = new ArrayList<>();

        if (compositionIds != null) {
            for (Object idObj : compositionIds) {
                Integer id = (Integer) idObj;
                Composition composition = findById(id);
                if (composition != null) {
                    compositions.add(composition);
                }
            }
        }
        return compositions;
    }
}
