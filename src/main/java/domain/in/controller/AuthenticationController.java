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

import domain.in.config.jwt.JwtUtil;
import domain.in.dto.AuthenticationRequest;
import domain.in.dto.RegisterRequest;
import domain.in.exception.InvalidCaptchaException;
import domain.in.security.CaptchaValidator;
import domain.in.security.CustomUserDetailService;
import domain.in.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
	
	@Value("${jwt.expirationMs}")
	private long expirationMs;
	
	private final AuthenticationManager authManager;

	private final CustomUserDetailService userDetailService;

	private final JwtUtil jwtUtil;

	private final UserService service;

	private final CaptchaValidator captchaValidator;
	
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
		
	    ResponseCookie cookie = ResponseCookie.from("access_token", token)
	            .httpOnly(true)
	            .secure(true)
	            .path("/")
	            .maxAge(Duration.ofMillis(expirationMs))
	            .sameSite("Strict")
	            .build();
	    
	    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
	}

	@PostMapping("/register")
	public ResponseEntity<?> signUp(@RequestBody RegisterRequest request) {
		// Step 1: CAPTCHA verification
		if (!captchaValidator.validateCpatcha(request.getCaptchToken())) {
			throw new InvalidCaptchaException("Invalid CAPTCHA");
		}
		
		// Step 2: Proceed to save
		service.save(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	private void doAuthenticate(String userName, String password) {
		Authentication authentication = authManager
				.authenticate(new UsernamePasswordAuthenticationToken(userName, password));
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
