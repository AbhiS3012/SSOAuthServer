package domain.in.dto;

import java.util.Date;

import lombok.Data;

@Data
public class AuthenticationResponse {

	private final String jwtToken;

	private final Date expirstionMs;

}
