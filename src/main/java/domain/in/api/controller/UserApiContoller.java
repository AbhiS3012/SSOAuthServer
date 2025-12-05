package domain.in.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import domain.in.api.dto.UserDTO;
import domain.in.api.mapper.UserMapper;
import domain.in.model.User;

@RestController
@RequestMapping("/public/apiuser")
public class UserApiContoller extends BaseApiController<Integer, User, UserDTO, UserMapper> {

	@Autowired
	private UserMapper mapper;

	@Override
	protected UserMapper getMapper() {
		// TODO Auto-generated method stub
		return mapper;
	}

}
