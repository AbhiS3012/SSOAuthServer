package domain.in.controller;

import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import domain.in.dto.AuthenticationRequest;
import domain.in.exception.InvalidCaptchaException;
import domain.in.jwtconfig.JwtUtil;
import domain.in.securityconfig.CaptchaValidator;
import domain.in.service.UserService;

@RestController
@RequestMapping("/apiAuth")
public class AuthenticationController {

	private final AuthenticationManager authManager;

	private final UserDetailsService userDetailService;

	private final JwtUtil jwtUtil;

	private final UserService service;

	private final CaptchaValidator captchaValidator;

	public AuthenticationController(AuthenticationManager authManager, UserDetailsService userDetailService,
			JwtUtil jwtUtil, UserService service, CaptchaValidator captchaValidator) {
		this.authManager = authManager;
		this.userDetailService = userDetailService;
		this.jwtUtil = jwtUtil;
		this.service = service;
		this.captchaValidator = captchaValidator;
	}

	@PostMapping("/login")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest request) {
		// Step 1: CAPTCHA verification
		if (!captchaValidator.validateCpatcha(request.getCaptchToken())) {
			throw new InvalidCaptchaException("Invalid CAPTCHA");
		}

		// Step 2: Proceed with authentication
		doAuthenticate(request.getUsername(), request.getPassword());
		
		final UserDetails userDetails = userDetailService.loadUserByUsername(request.getUsername());
		final String token = jwtUtil.generateToken(userDetails);

	    ResponseCookie cookie = ResponseCookie.from("sso_token", token)
	            .httpOnly(true)
	            .secure(true)
	            .path("/")
	            .maxAge(Duration.ofHours(1))
	            .sameSite("None")
	            .build();

		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
	}

	@PostMapping("/signUp")
	public ResponseEntity<?> signUp(@RequestBody AuthenticationRequest request) {
		// Step 1: CAPTCHA verification
		if (!captchaValidator.validateCpatcha(request.getCaptchToken())) {
			throw new InvalidCaptchaException("Invalid CAPTCHA");
		}
		
		// Step 2: Proceed to save
		service.save(request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	private void doAuthenticate(String userName, String password) {
		Authentication authentication = authManager
				.authenticate(new UsernamePasswordAuthenticationToken(userName, password));
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
