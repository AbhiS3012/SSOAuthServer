package domain.in.dto;

import java.util.concurrent.atomic.AtomicLong;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserSession {

	private final String jwtId;
	private final long createdAt;
	private final AtomicLong lastAccessTime;

	public UserSession(String jwtId) {
		this.jwtId = jwtId;
		this.createdAt = System.currentTimeMillis();
		this.lastAccessTime = new AtomicLong(System.currentTimeMillis());

	}

	public void touch() {
		lastAccessTime.set(System.currentTimeMillis());
	}

	public long getLastAccessTime() {
		return lastAccessTime.get();
	}

}
