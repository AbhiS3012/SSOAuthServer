package domain.in.securityconfig;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import domain.in.model.User;
import domain.in.repository.UserRepository;

@Service
public class CustomUserDetailService implements UserDetailsService {

	private final UserRepository repository;

	public CustomUserDetailService(UserRepository repository) {
		this.repository = repository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) {
		User user = repository.findByUsername(username);
		if (user == null) {
			throw new BadCredentialsException("User not found with username: " + username);
		}
		return user;
	}

}
