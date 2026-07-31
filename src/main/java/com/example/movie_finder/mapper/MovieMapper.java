package com.example.movie_finder.mapper;

import com.example.movie_finder.entity.Movie;
import com.example.movie_finder.model.ActorDto;
import com.example.movie_finder.model.MovieDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {ActorMapper.class}
)
public interface MovieMapper {
    @Mapping(target = "actors", source = "actors")
    MovieDto toDto(Movie movie, List<ActorDto> actors);

    Movie toEntity(MovieDto movieDto);
}
