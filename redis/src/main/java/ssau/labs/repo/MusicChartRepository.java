package ssau.labs.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
public class MusicChartRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    // Sorted Set (ZSET) для топ-песен
    private static final String TOP_SONGS_KEY = "music:top_songs";
    // Hash (HASH) для статистики прослушиваний
    private static final String SONG_STATS_KEY = "music:song_stats";
    // Set (SET) для жанров артистов
    private static final String ARTIST_GENRES_KEY = "music:artist_genres";
    // List (LIST) для ежедневных чартов
    private static final String DAILY_CHARTS_PREFIX = "music:daily_charts:";

    @Autowired
    public MusicChartRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private Long safeIncrementHash(String key, String hashKey, long delta) {
        try {
            return redisTemplate.opsForHash().increment(key, hashKey, delta);
        } catch (Exception e) {
            redisTemplate.opsForHash().put(key, hashKey, delta);
            return delta;
        }
    }

    private Long safeGetHashValue(String key, String hashKey) {
        try {
            Object value = redisTemplate.opsForHash().get(key, hashKey);
            if (value == null) return 0L;
            if (value instanceof Number) return ((Number) value).longValue();
            return Long.parseLong(value.toString());
        } catch (Exception e) {
            return 0L;
        }
    }

    public void addToTopSongs(String songName) {
        redisTemplate.opsForZSet().add(TOP_SONGS_KEY, songName, 0);
    }

    public Set<String> getTopSongs(int limit) {
        return redisTemplate.opsForZSet()
                .reverseRange(TOP_SONGS_KEY, 0, limit - 1)
                .stream()
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    public void recordSongPlay(String songId) {
        safeIncrementHash(SONG_STATS_KEY, songId, 1L);
    }

    public Long getSongPlayCount(String songId) {
        return safeGetHashValue(SONG_STATS_KEY, songId);
    }

    public void addArtistGenre(String artistId, String genre) {
        redisTemplate.opsForSet().add(ARTIST_GENRES_KEY + artistId, genre);
    }

    public Set<String> getArtistGenres(String artistId) {
        return redisTemplate.opsForSet()
                .members(ARTIST_GENRES_KEY + artistId)
                .stream()
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    public void createDailyChart(String chartId, List<String> songIds, int hoursToLive) {
        String key = DAILY_CHARTS_PREFIX + chartId;
        redisTemplate.opsForList().rightPushAll(key, songIds.toArray());
        redisTemplate.expire(key, hoursToLive, TimeUnit.HOURS);
    }

    public List<String> getDailyChart(String chartId) {
        return redisTemplate.opsForList()
                .range(DAILY_CHARTS_PREFIX + chartId, 0, -1)
                .stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    public void resetAllStats() {
        redisTemplate.delete(TOP_SONGS_KEY);
        redisTemplate.delete(SONG_STATS_KEY);

        Set<String> genreKeys = redisTemplate.keys(ARTIST_GENRES_KEY + "*");
        if (genreKeys != null) {
            redisTemplate.delete(genreKeys);
        }

        Set<String> chartKeys = redisTemplate.keys(DAILY_CHARTS_PREFIX + "*");
        if (chartKeys != null) {
            redisTemplate.delete(chartKeys);
        }
    }
}
