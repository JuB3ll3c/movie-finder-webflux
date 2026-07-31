package com.example.movie_finder.mapper;

import com.example.movie_finder.entity.Actor;
import com.example.movie_finder.model.ActorDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ActorMapper {

    ActorDto toDto(Actor actor);

    Actor toEntity(ActorDto actorDto);
}
