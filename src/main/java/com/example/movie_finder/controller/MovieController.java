package com.example.movie_finder.controller;

import com.example.movie_finder.model.MovieDto;
import com.example.movie_finder.service.MovieService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/movies")
@AllArgsConstructor
public class MovieController {
    private final MovieService movieService;

    @GetMapping
    public Flux<MovieDto> findAll() {
        return movieService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<MovieDto>> findById(@PathVariable Long id) {
        return movieService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<MovieDto>> createMovie(@RequestBody MovieDto movieDto) {
        return movieService.createMovie(movieDto)
                .map(createdMovie -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(createdMovie));
    }

}
