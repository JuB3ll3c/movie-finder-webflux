package com.example.movie_finder.service;

import com.example.movie_finder.entity.Actor;
import com.example.movie_finder.entity.Movie;
import com.example.movie_finder.entity.MovieActor;
import com.example.movie_finder.mapper.ActorMapper;
import com.example.movie_finder.mapper.MovieMapper;
import com.example.movie_finder.model.ActorDto;
import com.example.movie_finder.model.MovieDto;
import com.example.movie_finder.repository.ActorRepository;
import com.example.movie_finder.repository.MovieActorRepository;
import com.example.movie_finder.repository.MovieRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final ActorRepository actorRepository;
    private final MovieActorRepository movieActorRepository;
    private final MovieMapper movieMapper;
    private final ActorMapper actorMapper;

    public Flux<MovieDto> findAll() {
        return movieRepository.findAll()
                .collectList()                          //findAll().collectList pour avoir un Mono<List<Movie>> et pas un Flux<Movie>. Puis flatMapMany pour renvoyer un Flux<MovieDto>
                .flatMapMany(movies -> {
                    if (movies.isEmpty()) {
                        return Flux.empty();
                    }

                    List<Long> movieIds = movies.stream().map(Movie::getId).toList();

                    Mono<List<Actor>> actorsMono = actorRepository.findByMovieIds(movieIds).collectList();
                    Mono<List<MovieActor>> relationsMono = movieActorRepository.findByMovieIds(movieIds).collectList();

                    return Mono.zip(actorsMono, relationsMono)
                            .flatMapMany(tuple -> {
                                List<Actor> actors = tuple.getT1();
                                List<MovieActor> relations = tuple.getT2();

                                // ActorDto par id actor pour recherche plus rapide
                                Map<Long, ActorDto> actorDtoById = actors.stream()
                                        .collect(Collectors.toMap(
                                                Actor::getId,
                                                actorMapper::toDto
                                        ));
                                // List<MovieActor> par id de movie
                                Map<Long, List<MovieActor>> relationsByMovieId = relations.stream()
                                        .collect(Collectors.groupingBy(MovieActor::getMovieId));

                                List<MovieDto> movieDtos = movies.stream().map(movie -> {
                                    // Pour chaque movie on récupère la List<MovieActor>. getOrDefault permet de renvoyer list vide si pas de liste d'acteur associée au movie
                                    List<MovieActor> movieActorList = relationsByMovieId.getOrDefault(movie.getId(), List.of());

                                    // Depuis movieActorList on map la list des actorDto associé au movie
                                    List<ActorDto> movieActorDtos = movieActorList.stream()
                                            .map(movieActor -> actorDtoById.get(movieActor.getActorId()))
                                            .toList();

                                    return movieMapper.toDto(movie, movieActorDtos);
                                }).toList();

                                return Flux.fromIterable(movieDtos);
                            });
                });
    }

    public Mono<MovieDto> findById(Long id) {
        return movieRepository.findById(id)
                .flatMap(movie ->
                        actorRepository.findByMovieIds(List.of(movie.getId()))
                                .map(actorMapper::toDto)
                                .collectList()
                                .map(actors -> movieMapper.toDto(movie, actors))
                );
    }


    @Transactional
    public Mono<MovieDto> createMovie(MovieDto movieDto) {
        Movie movieToSave = movieMapper.toEntity(movieDto);

        return movieRepository.save(movieToSave)
                .flatMap(savedMovie ->
                        Flux.fromIterable(movieDto.actors())
                                .flatMap(this::getOrCreateActor)
                                .flatMap(savedActor ->
                                        movieActorRepository.save(new MovieActor(savedMovie.getId(), savedActor.getId()))
                                                .thenReturn(savedActor)
                                )
                                .map(actorMapper::toDto)
                                .collectList()
                                .map(actorDtos -> movieMapper.toDto(savedMovie, actorDtos))
                );
    }

    private Mono<Actor> getOrCreateActor(ActorDto actorDto) {
            return actorRepository.findById(actorDto.id())
                    .switchIfEmpty(
                            Mono.defer(() ->
                                    actorRepository.save(actorMapper.toEntity(actorDto))
                            )
                    );
    }
}
