package domain.in.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import domain.in.dto.AuthenticationRequest;
import domain.in.model.User;
import domain.in.repository.UserRepository;

@Service
@Transactional
public class UserService {

	@Autowired
	UserRepository repository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public void save(AuthenticationRequest request) {
		// Encode the raw password before saving
		String encodedPassword = passwordEncoder.encode(request.getPassword());

		User user = new User();
		user.setUsername(request.getUsername());
		user.setPassword(encodedPassword);

		repository.save(user);
	}

}
