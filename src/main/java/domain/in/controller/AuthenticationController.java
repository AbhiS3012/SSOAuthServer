package domain.in.controller;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import domain.in.config.custom.CaptchaValidator;
import domain.in.config.custom.CustomUserDetailService;
import domain.in.config.jwt.JwtUtil;
import domain.in.dto.AuthenticationRequest;
import domain.in.exception.InvalidCaptchaException;
import domain.in.model.RefreshToken;
import domain.in.service.RefreshTokenService;
import domain.in.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
	
	@Value("${jwt.expirationMs}")
	private long expirationMs;
	
	@Value("${jwt.refreshTokenDays}")
	private long refreshTokenDays;

	private final AuthenticationManager authManager;

	private final CustomUserDetailService userDetailService;

	private final JwtUtil jwtUtil;

	private final UserService service;

	private final CaptchaValidator captchaValidator;
	
	private final RefreshTokenService refreshTokenService;

	public AuthenticationController(AuthenticationManager authManager, CustomUserDetailService userDetailService,
			JwtUtil jwtUtil, UserService service, CaptchaValidator captchaValidator,
			RefreshTokenService refreshTokenService) {
		this.authManager = authManager;
		this.userDetailService = userDetailService;
		this.jwtUtil = jwtUtil;
		this.service = service;
		this.captchaValidator = captchaValidator;
		this.refreshTokenService = refreshTokenService;
	}

	@PostMapping("/login")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest request) {
		// Step 1: CAPTCHA verification
		if (!captchaValidator.validateCpatcha(request.getCaptchToken())) {
			throw new InvalidCaptchaException("Invalid CAPTCHA");
		}

		// Step 2: Proceed with authentication
		doAuthenticate(request.getUsername(), request.getPassword());
		
		// Step 3: Proceed JWT generation
		final UserDetails userDetails = userDetailService.loadUserByUsername(request.getUsername());
		final String token = jwtUtil.generateToken(userDetails);
		
		// Step 4: Proceed Refresh Token generation
		final RefreshToken refreshToken = refreshTokenService.create(request.getUsername());

	    ResponseCookie cookie = ResponseCookie.from("access_token", token)
	            .httpOnly(true)
	            .secure(true)
	            .path("/")
	            .maxAge(Duration.ofMinutes(expirationMs))
	            .sameSite("Strict")
	            .build();
	    
	    ResponseCookie rtCookie = ResponseCookie.from("refresh_token", refreshToken.getToken())
	            .httpOnly(true)
	            .secure(true)
	            .path("/")
	            .maxAge(Duration.ofDays(refreshTokenDays))
	            .sameSite("Strict")
	            .build();

		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
				.header(HttpHeaders.SET_COOKIE, rtCookie.toString()).build();
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
