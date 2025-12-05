package domain.in.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import domain.in.api.dto.UserDTO;
import domain.in.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper extends BaseApiMapper<User, UserDTO> {

}
