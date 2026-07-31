package com.example.movie_finder.repository;

import com.example.movie_finder.entity.MovieActor;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface MovieActorRepository extends ReactiveCrudRepository<MovieActor, Long> {
    @Query("SELECT * FROM movie_actor WHERE movie_id IN (:movieIds)")
    Flux<MovieActor> findByMovieIds(@Param("movieIds") List<Long> movieIds);
}
