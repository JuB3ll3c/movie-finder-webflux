package com.example.movie_finder.repository;

import com.example.movie_finder.entity.Actor;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ActorRepository extends ReactiveCrudRepository<Actor, Long> {
    @Query("""
        SELECT DISTINCT a.*
        FROM actor a
        JOIN movie_actor ma ON a.id = ma.actor_id
        WHERE ma.movie_id IN (:movieIds)
    """)
    Flux<Actor> findByMovieIds(@Param("movieIds") List<Long> movieIds);
}
