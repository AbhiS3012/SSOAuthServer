package domain.in.config.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import domain.in.config.custom.CustomUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	private static final String COOKIE_NAME = "access_token";

	private final CustomUserDetailService userDetailsService;

	private final JwtUtil jwtUtil;

	public JwtAuthenticationFilter(CustomUserDetailService userDetailsService, JwtUtil jwtUtil) {
		this.userDetailsService = userDetailsService;
		this.jwtUtil = jwtUtil;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		final String authorizationHeader = request.getHeader("Authorization");

		String userName = null;
		String jwt = null;

		// Try to get JWT from Authorization header
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			jwt = authorizationHeader.substring(7);
			try {
				userName = jwtUtil.extractUserName(jwt);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// If not found, try to get JWT from cookie (e.g., 'sso_token')
		if (jwt == null && request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if (COOKIE_NAME.equals(cookie.getName())) {
					jwt = cookie.getValue();
					break;
				}
			}
		}

		// Extract username and validate token
		if (jwt != null) {
			try {
				userName = jwtUtil.extractUserName(jwt);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

			if (jwtUtil.validateToken(jwt, userDetails)) {

				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());

				usernamePasswordAuthenticationToken
						.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
		}
		filterChain.doFilter(request, response);
	}

}
