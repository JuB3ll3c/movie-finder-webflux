package com.example.movie_finder.service;

import com.example.movie_finder.entity.Actor;
import com.example.movie_finder.entity.Genre;
import com.example.movie_finder.entity.Movie;
import com.example.movie_finder.entity.MovieActor;
import com.example.movie_finder.mapper.ActorMapper;
import com.example.movie_finder.mapper.MovieMapper;
import com.example.movie_finder.model.ActorDto;
import com.example.movie_finder.model.MovieDto;
import com.example.movie_finder.repository.ActorRepository;
import com.example.movie_finder.repository.MovieActorRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private ActorRepository actorRepository;

    @Mock
    private MovieActorRepository movieActorRepository;

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
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void findAll_ShouldReturnEmptyFlux_WhenNoMovies() {
        when(movieRepository.findAll()).thenReturn(Flux.empty());

        Flux<MovieDto> result = movieService.findAll();

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void findAll_ShouldReturnMovieDtos_WhenMoviesExist() {
        Movie movie1 = new Movie(1L, "Inception", Genre.ACTION, LocalDate.of(2010, 7, 16));
        Movie movie2 = new Movie(2L, "Interstellar", Genre.DRAMA, LocalDate.of(2014, 11, 7));

        Actor actor1 = new Actor(10L, "Leonardo", "DiCaprio");
        Actor actor2 = new Actor(11L, "Elliot", "Page");

        ActorDto actorDto1 = new ActorDto(10L, "Leonardo", "DiCaprio");
        ActorDto actorDto2 = new ActorDto(11L, "Elliot", "Page");

        MovieActor relation1 = new MovieActor(1L, 10L);
        MovieActor relation2 = new MovieActor(1L, 11L);
        MovieActor relation3 = new MovieActor(2L, 11L);

        MovieDto movieDto1 = new MovieDto(1L, "Inception", Genre.ACTION, LocalDate.of(2010, 7, 16), List.of(actorDto1, actorDto2));
        MovieDto movieDto2 = new MovieDto(2L, "Interstellar", Genre.DRAMA, LocalDate.of(2014, 11, 7), List.of(actorDto2));

        when(movieRepository.findAll()).thenReturn(Flux.just(movie1, movie2));
        when(actorRepository.findByMovieIds(List.of(1L, 2L))).thenReturn(Flux.just(actor1, actor2));
        when(movieActorRepository.findByMovieIds(List.of(1L, 2L))).thenReturn(Flux.just(relation1, relation2, relation3));
        when(actorMapper.toDto(actor1)).thenReturn(actorDto1);
        when(actorMapper.toDto(actor2)).thenReturn(actorDto2);
        when(movieMapper.toDto(movie1, List.of(actorDto1, actorDto2))).thenReturn(movieDto1);
        when(movieMapper.toDto(movie2, List.of(actorDto2))).thenReturn(movieDto2);

        Flux<MovieDto> result = movieService.findAll();

        StepVerifier.create(result)
                .assertNext(dto -> {
                    assertThat(dto.id()).isEqualTo(1L);
                    assertThat(dto.actors()).hasSize(2);
                })
                .assertNext(dto -> {
                    assertThat(dto.id()).isEqualTo(2L);
                    assertThat(dto.actors()).hasSize(1);
                    assertThat(dto.actors().getFirst().firstName()).isEqualTo("Elliot");
                })
                .verifyComplete();
    }

    @Test
    void createMovie_ShouldUseExistingActor_WhenActorExists() {
        ActorDto actorDto = new ActorDto(10L, "Leonardo", "DiCaprio");
        MovieDto movieDto = new MovieDto(null, "Inception", Genre.ACTION, LocalDate.of(2010, 7, 16), List.of(actorDto));

        Movie movieToSave = new Movie(null, "Inception", Genre.ACTION, LocalDate.of(2010, 7, 16));
        Movie savedMovie = new Movie(1L, "Inception", Genre.ACTION, LocalDate.of(2010, 7, 16));
        Actor actor = new Actor(10L, "Leonardo", "DiCaprio");
        MovieDto resultDto = new MovieDto(1L, "Inception", Genre.ACTION, LocalDate.of(2010, 7, 16), List.of(actorDto));

        when(movieMapper.toEntity(movieDto)).thenReturn(movieToSave);
        when(movieRepository.save(movieToSave)).thenReturn(Mono.just(savedMovie));
        when(actorRepository.findById(10L)).thenReturn(Mono.just(actor));
        when(movieActorRepository.save(any(MovieActor.class))).thenReturn(Mono.just(new MovieActor(1L, 10L)));
        when(actorMapper.toDto(actor)).thenReturn(actorDto);
        when(movieMapper.toDto(savedMovie, List.of(actorDto))).thenReturn(resultDto);

        Mono<MovieDto> result = movieService.createMovie(movieDto);

        StepVerifier.create(result)
                .assertNext(dto -> {
                    assertThat(dto.id()).isEqualTo(1L);
                    assertThat(dto.actors()).hasSize(1);
                    assertThat(dto.actors().getFirst().firstName()).isEqualTo("Leonardo");
                })
                .verifyComplete();
    }

    @Test
    void createMovie_ShouldCreateNewActor_WhenActorDoesNotExist() {
        ActorDto actorDto = new ActorDto(99L, "Brad", "Pitt");
        MovieDto movieDto = new MovieDto(null, "Seven", Genre.HORROR, LocalDate.of(1995, 9, 22), List.of(actorDto));

        Movie movieToSave = new Movie(null, "Seven", Genre.HORROR, LocalDate.of(1995, 9, 22));
        Movie savedMovie = new Movie(1L, "Seven", Genre.HORROR, LocalDate.of(1995, 9, 22));
        Actor actor = new Actor(99L, "Brad", "Pitt");
        MovieDto resultDto = new MovieDto(1L, "Seven", Genre.HORROR, LocalDate.of(1995, 9, 22), List.of(actorDto));

        when(movieMapper.toEntity(movieDto)).thenReturn(movieToSave);
        when(movieRepository.save(movieToSave)).thenReturn(Mono.just(savedMovie));
        when(actorRepository.findById(99L)).thenReturn(Mono.empty());
        when(actorMapper.toEntity(actorDto)).thenReturn(actor);
        when(actorRepository.save(actor)).thenReturn(Mono.just(actor));
        when(movieActorRepository.save(any(MovieActor.class))).thenReturn(Mono.just(new MovieActor(1L, 99L)));
        when(actorMapper.toDto(actor)).thenReturn(actorDto);
        when(movieMapper.toDto(savedMovie, List.of(actorDto))).thenReturn(resultDto);

        Mono<MovieDto> result = movieService.createMovie(movieDto);

        StepVerifier.create(result)
                .assertNext(dto -> {
                    assertThat(dto.id()).isEqualTo(1L);
                    assertThat(dto.actors()).hasSize(1);
                    assertThat(dto.actors().getFirst().lastName()).isEqualTo("Pitt");
                })
                .verifyComplete();
    }
}
