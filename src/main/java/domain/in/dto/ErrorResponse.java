package domain.in.dto;

import lombok.Data;

@Data
public class ErrorResponse {

	private final String error;

	private final String exceptionMsg;
	
	private final Integer status;

}
