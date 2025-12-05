package domain.in.api.dto;

import java.util.Date;

import lombok.Data;

@Data
public class UserDTO {

	Integer id;
	String username;
	String password;
	Date createdAt;

}
