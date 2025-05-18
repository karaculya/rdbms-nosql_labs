package ssau.labs.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssau.labs.repo.MusicChartRepository;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/charts")
public class MusicChartController {
    private final MusicChartRepository repository;

    public MusicChartController(MusicChartRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/top-songs")
    public ResponseEntity<?> addToTopSongs(@RequestParam String songName) {
        try {
            repository.addToTopSongs(songName);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding song to top: " + e.getMessage());
        }
    }

    @GetMapping("/top-songs")
    public ResponseEntity<?> getTopSongs(@RequestParam(defaultValue = "10") int limit) {
        try {
            return ResponseEntity.ok(repository.getTopSongs(limit));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting top songs: " + e.getMessage());
        }
    }

    @PostMapping("/record-play/{songId}")
    public ResponseEntity<?> recordSongPlay(@PathVariable String songId) {
        try {
            repository.recordSongPlay(songId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error recording play: " + e.getMessage());
        }
    }

    @GetMapping("/play-count/{songId}")
    public ResponseEntity<?> getSongPlayCount(@PathVariable String songId) {
        try {
            Long count = repository.getSongPlayCount(songId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting play count: " + e.getMessage());
        }
    }

    @PostMapping("/artist-genres/{artistId}")
    public ResponseEntity<?> addArtistGenre(@PathVariable String artistId, @RequestParam String genre) {
        try {
            repository.addArtistGenre(artistId, genre);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding artist genre: " + e.getMessage());
        }
    }

    @GetMapping("/artist-genres/{artistId}")
    public ResponseEntity<?> getArtistGenres(@PathVariable String artistId) {
        try {
            Set<String> genres = repository.getArtistGenres(artistId);
            return ResponseEntity.ok(genres);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting artist genres: " + e.getMessage());
        }
    }

    @PostMapping("/daily-charts/{chartId}")
    public ResponseEntity<?> createDailyChart(
            @PathVariable String chartId,
            @RequestBody List<String> songIds,
            @RequestParam int hoursToLive) {
        try {
            repository.createDailyChart(chartId, songIds, hoursToLive);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating daily chart: " + e.getMessage());
        }
    }

    @GetMapping("/daily-charts/{chartId}")
    public ResponseEntity<?> getDailyChart(@PathVariable String chartId) {
        try {
            List<String> chart = repository.getDailyChart(chartId);
            return ResponseEntity.ok(chart);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting daily chart: " + e.getMessage());
        }
    }

    @DeleteMapping("/reset-stats")
    public ResponseEntity<?> resetStats() {
        try {
            repository.resetAllStats();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error resetting stats: " + e.getMessage());
        }
    }
}
