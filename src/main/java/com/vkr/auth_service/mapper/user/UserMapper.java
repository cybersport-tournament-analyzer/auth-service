package com.vkr.auth_service.mapper.user;

import com.vkr.auth_service.dto.user.UserDto;
import com.vkr.auth_service.entity.user.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserMapper {


    public abstract UserDto toDto(User user);

}
