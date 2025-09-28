package domain.in.dto;

import lombok.Data;

@Data
public class AuthenticationRequest {

	private String username;

	private String password;

	private String captchToken;

}
