package com.example.movie_finder.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
public class MovieActor {
    private Long movieId;
    private Long actorId;
}
