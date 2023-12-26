package com.aname.api.security;

import java.util.Date;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class TokenUtils {

	private final static String ACCESS_TOKEN_SECRET = "3432maksmdk31k21lk2j312lk2543asd@36%&";
	private final static Long ACCESS_TOKEN_VALIDITY_SECONDS = 2_592_00L;

	// Generar token de acceso
	public String generateAccesToken(String email) {
		return Jwts.builder()				
				.setSubject(email)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS * 1000))
				.signWith(SignatureAlgorithm.HS256, ACCESS_TOKEN_SECRET.getBytes()).compact();
	}

	// Validar el token de acceso
	public boolean isTokenValid(String token, UserDetails userDetails) {
		try {
			 final String username = getUsernameFromToken(token);
		        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
		} catch (Exception e) {
			System.out.println("Token invalido, error: ".concat(e.getMessage()));
			return false;
		}
	}
	

    private boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    
    private Date getExpirationDateFromToken(String token) {
        return Jwts.parser().setSigningKey(ACCESS_TOKEN_SECRET.getBytes()).parseClaimsJws(token).getBody().getExpiration();
    }

	// Obtener el username del token
	public String getUsernameFromToken(String token) {
		return Jwts.parser().setSigningKey(ACCESS_TOKEN_SECRET.getBytes()).parseClaimsJws(token).getBody().getSubject();
	}

}