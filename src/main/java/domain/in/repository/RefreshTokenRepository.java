package domain.in.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import domain.in.model.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>{

	Optional<RefreshToken> findByToken(String tokenValue);

}
