package com.settler.service.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SignatureException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtTokenProvider {

	@Value("${app.jwt.secret}")
	private String jwtSecret;

	@Value("${app.jwt.expirationMs:86400000}") // Default 24 hours
	private long jwtExpirationMs;

	private Key key;

	@PostConstruct
	public void init() {
		// Convert secret to signing key
		this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Generate a JWT token from authenticated user.
	 */
	public String generateToken(Authentication authentication) {
		UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

		return Jwts.builder().setSubject(userPrincipal.getUsername()).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
				.signWith(key, SignatureAlgorithm.HS256).compact();
	}

	/**
	 * Extract username (subject) from token.
	 */
	public String getUsernameFromJwt(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
	}

	/**
	 * Validate token for integrity and expiration.
	 */
	public boolean validateToken(String authToken) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
			return true;
		} catch (ExpiredJwtException e) {
			System.err.println("JWT token expired: " + e.getMessage());
		} catch (UnsupportedJwtException e) {
			System.err.println("JWT token unsupported: " + e.getMessage());
		} catch (MalformedJwtException e) {
			System.err.println("Invalid JWT token: " + e.getMessage());
		} catch (SignatureException e) {
			System.err.println("JWT signature invalid: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			System.err.println("JWT claims string empty: " + e.getMessage());
		}
		return false;
	}
}
