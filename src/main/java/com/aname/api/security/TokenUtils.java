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

	/**
	* Generar un token de acceso para el email especificado. El token es válido durante 30 días.
	* @param email - Email del usuario para generar un token de acceso
	* @return String con el token generado
	*/
	public String generateAccesToken(String email) {
		return Jwts.builder()				
				.setSubject(email)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS * 1000))
				.signWith(SignatureAlgorithm.HS256, ACCESS_TOKEN_SECRET.getBytes()).compact();
	}

	// Validar el token de acceso

	/**
	* Verifica si el token es válido. Este método se utiliza para verificar si las credenciales del usuario son válidas
	* @param token - El token para comprobar
	* @param userDetails - Los detalles del usuario
	* @return Verdad si el token es válido falso de otro modo
	*/
	public boolean isTokenValid(String token, UserDetails userDetails) {
		try {
			 final String username = getUsernameFromToken(token);
		        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
		} catch (Exception e) {
			System.out.println("Token invalido, error: ".concat(e.getMessage()));
			return false;
		}
	}
	
    /**
    * Verifica si el token ha expirado. Esto se utiliza para evitar ataques de repetición que se envían al token durante mucho tiempo
    * @param token - el token para comprobar su vencimiento
    * @return si el token expira falso de otro modo no se lanza ninguna excepción
    */
    private boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    
    /**
    * Obtiene la fecha de expiracion del token especificado
    * @param token - El token para analizar
    * @return la fecha de vencimiento del token o nula si no existe el token
    */
    private Date getExpirationDateFromToken(String token) {
        return Jwts.parser().setSigningKey(ACCESS_TOKEN_SECRET.getBytes()).parseClaimsJws(token).getBody().getExpiration();
    }

	// Obtener el username del token

	/**
	* Extrae el nombre de usuario del token JWT. Esto se utiliza para autenticar al usuario a la API. El token debe ser válido
	* @param token - Token JWT valido
	* @return nombre de usuario extraído del token
	*/
	public String getUsernameFromToken(String token) {
		return Jwts.parser().setSigningKey(ACCESS_TOKEN_SECRET.getBytes()).parseClaimsJws(token).getBody().getSubject();
	}

}