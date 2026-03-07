package domain.in.config.custom;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

	private final CustomUserDetailService userDetailsService;
	private final PasswordEncoder passwordEncoder;

	public CustomAuthenticationProvider(CustomUserDetailService userDetailsService, PasswordEncoder passwordEncoder) {
		this.userDetailsService = userDetailsService;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {

		try {
			return userDetailsService.loadUserByUsername(username);
		} catch (Exception e) {
			throw new BadCredentialsException("User not found with username: " + username, e);
		}
	}

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		if (authentication.getCredentials() == null) {
			throw new BadCredentialsException("No credentials provided");
		}

		String presentedPassword = authentication.getCredentials().toString();
		if (!passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
			throw new BadCredentialsException("Invalid credentials");
		}
	}

}
