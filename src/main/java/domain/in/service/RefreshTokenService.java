package domain.in.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import domain.in.exception.CustomException;
import domain.in.model.RefreshToken;
import domain.in.model.User;
import domain.in.repository.RefreshTokenRepository;
import domain.in.repository.UserRepository;

@Service
@Transactional
public class RefreshTokenService {

	@Value("${jwt.refreshTokenDays}")
	private int refreshTokenDays;

	private final RefreshTokenRepository refreshTokenRepository;
	private final UserRepository userRepository;

	public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
		this.refreshTokenRepository = refreshTokenRepository;
		this.userRepository = userRepository;
	}

	public RefreshToken create(String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new BadCredentialsException("User not found"));

		RefreshToken token = new RefreshToken();
		token.setToken(UUID.randomUUID().toString());
		token.setUser(user);
		token.setExpiryDate(Instant.now().plus(refreshTokenDays, ChronoUnit.DAYS));

		return refreshTokenRepository.save(token);
	}

	public RefreshToken validate(String tokenValue) {
		RefreshToken token = refreshTokenRepository.findByToken(tokenValue)
				.orElseThrow(() -> new CustomException("Invalid refresh token"));

		if (token.isRevoked()) {
			throw new CustomException("Refresh token revoked");
		}

		if (token.getExpiryDate().isBefore(Instant.now())) {
			refreshTokenRepository.delete(token);
			throw new CustomException("Refresh token expired");
		}

		return token;
	}
	
	public void revoke(String tokenValue) {
		RefreshToken token = validate(tokenValue);
		token.setRevoked(true);
		refreshTokenRepository.save(token);
	}

}
