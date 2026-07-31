package com.example.movie_finder.model;

import com.example.movie_finder.entity.Genre;
import java.time.LocalDate;
import java.util.List;

public record MovieDto(Long id, String name, Genre genre, LocalDate publicationDate, List<ActorDto> actors) {
}
