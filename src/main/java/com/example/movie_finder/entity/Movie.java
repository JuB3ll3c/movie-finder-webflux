package com.example.movie_finder.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Movie {
    @Id
    private Long id;
    private String name;
    private Genre genre;
    private LocalDate publicationDate;
}
