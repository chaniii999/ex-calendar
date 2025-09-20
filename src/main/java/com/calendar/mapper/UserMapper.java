package com.calendar.mapper;

import com.calendar.dto.auth.SignUpReq;
import com.calendar.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = org.mapstruct.factory.Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", ignore = true) // ULID는 @PrePersist에서 자동 생성
    @Mapping(target = "email", source = "dto.email")
    @Mapping(target = "nickname", source = "dto.nickname")
    @Mapping(target = "password", ignore = true) // Service 레벨에서 암호화 후 주입
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User toEntity(SignUpReq dto);
}
