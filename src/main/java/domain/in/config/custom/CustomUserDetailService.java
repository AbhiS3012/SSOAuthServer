package domain.in.config.custom;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import domain.in.repository.UserRepository;

@Service
public class CustomUserDetailService implements UserDetailsService {

	private final UserRepository repository;

	public CustomUserDetailService(UserRepository repository) {
		this.repository = repository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) {
		return repository.findByUsername(username)
				.orElseThrow(() -> new BadCredentialsException("User not found with username: " + username));
	}

}
