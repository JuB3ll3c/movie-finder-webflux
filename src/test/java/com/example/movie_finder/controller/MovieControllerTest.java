package com.example.movie_finder.controller;

import com.example.movie_finder.entity.Genre;
import com.example.movie_finder.model.ActorDto;
import com.example.movie_finder.model.MovieDto;
import com.example.movie_finder.service.MovieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;

@WebFluxTest(MovieController.class)
class MovieControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private MovieService movieService;

    @Test
    void findAll_ShouldReturnMovies() {
        MovieDto movieDto = new MovieDto(1L, "Inception", Genre.ACTION, LocalDate.of(2010, 7, 16),
                List.of(new ActorDto(10L, "Leonardo", "DiCaprio")));

        when(movieService.findAll()).thenReturn(Flux.just(movieDto));

        webTestClient.get().uri("/movies")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MovieDto.class)
                .hasSize(1)
                .contains(movieDto);
    }

    @Test
    void findById_ShouldReturnMovie_WhenExists() {
        MovieDto movieDto = new MovieDto(1L, "Inception", Genre.ACTION, LocalDate.of(2010, 7, 16),
                List.of(new ActorDto(10L, "Leonardo", "DiCaprio")));

        when(movieService.findById(1L)).thenReturn(Mono.just(movieDto));

        webTestClient.get().uri("/movies/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(MovieDto.class)
                .isEqualTo(movieDto);
    }

    @Test
    void findById_ShouldReturnNotFound_WhenMovieDoesNotExist() {
        when(movieService.findById(99L)).thenReturn(Mono.empty());

        webTestClient.get().uri("/movies/99")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void createMovie_ShouldReturnCreated() {
        ActorDto actorDto = new ActorDto(10L, "Leonardo", "DiCaprio");
        MovieDto request = new MovieDto(null, "Inception", Genre.ACTION, LocalDate.of(2010, 7, 16), List.of(actorDto));
        MovieDto created = new MovieDto(1L, "Inception", Genre.ACTION, LocalDate.of(2010, 7, 16), List.of(actorDto));

        when(movieService.createMovie(request)).thenReturn(Mono.just(created));

        webTestClient.post().uri("/movies")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(MovieDto.class)
                .isEqualTo(created);
    }
}
