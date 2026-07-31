package com.example.movie_finder.service;

import com.example.movie_finder.entity.Actor;
import com.example.movie_finder.entity.Genre;
import com.example.movie_finder.entity.Movie;
import com.example.movie_finder.mapper.ActorMapper;
import com.example.movie_finder.mapper.MovieMapper;
import com.example.movie_finder.model.ActorDto;
import com.example.movie_finder.model.MovieDto;
import com.example.movie_finder.repository.ActorRepository;
import com.example.movie_finder.repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private ActorRepository actorRepository;

    @Mock
    private ActorMapper actorMapper;

    @Mock
    private MovieMapper movieMapper;

    @InjectMocks
    private MovieService movieService;

    @Test
    void findById_ShouldReturnMovieDto_WhenMovieExists() {
        Long movieId = 1L;
        Movie movie = new Movie(movieId, "Inception", Genre.ACTION, LocalDate.of(2010, 7, 16));
        Actor actor = new Actor(10L, "Leonardo", "DiCaprio");

        ActorDto actorDto = new ActorDto(10L, "Leonardo", "DiCaprio");
        MovieDto movieDto = new MovieDto(movieId, "Inception", Genre.ACTION, LocalDate.of(2010, 7, 16), List.of(actorDto));


        when(movieRepository.findById(movieId)).thenReturn(Mono.just(movie));
        when(actorRepository.findByMovieIds(List.of(movieId))).thenReturn(Flux.just(actor));
        when(actorMapper.toDto(actor)).thenReturn(actorDto);
        when(movieMapper.toDto(movie, List.of(actorDto))).thenReturn(movieDto);

        Mono<MovieDto> result = movieService.findById(movieId);

        StepVerifier.create(result)
                .assertNext(dto -> {
                    assertThat(dto.id()).isEqualTo(movieId);
                    assertThat(dto.name()).isEqualTo("Inception");
                    assertThat(dto.actors()).hasSize(1);
                    assertThat(dto.actors().getFirst().firstName()).isEqualTo("Leonardo");
                })
                .verifyComplete();
    }

    @Test
    void findById_ShouldReturnEmptyMono_WhenMovieDoesNotExist() {
        Long movieId = 99L;
        when(movieRepository.findById(movieId)).thenReturn(Mono.empty());

        Mono<MovieDto> result = movieService.findById(movieId);

        StepVerifier.create(result)
                .verifyComplete();
    }
}
