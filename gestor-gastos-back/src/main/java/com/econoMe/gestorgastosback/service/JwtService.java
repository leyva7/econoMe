package com.econoMe.gestorgastosback.service;

import com.econoMe.gestorgastosback.exception.UserException;
import com.econoMe.gestorgastosback.model.User;
import com.econoMe.gestorgastosback.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Autowired
    private UserRepository userRepository;

    // Clave secreta para firmar los tokens JWT (debe ser almacenada de forma segura)
    private static final String SECRET_KEY = "+66jHCLihGgchsGQ5QBnwRC0rCVzzR0R16xaXn4BL3Y=";

    // Método para generar un token JWT
    public String getToken(UserDetails user) {
        return getToken(new HashMap<>(), user);
    }

    // Método privado para generar un token JWT con reclamaciones adicionales
    private String getToken(Map<String, Object> extraClaims, UserDetails user) {
        return Jwts.builder()
                .setClaims(extraClaims) // Establece las reclamaciones personalizadas
                .setSubject(user.getUsername()) // Establece el nombre de usuario como sujeto del token
                .setIssuedAt(new Date(System.currentTimeMillis())) // Establece la fecha de emisión del token
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // Establece la fecha de expiración del token (1 hora)
                .signWith(getKey(), SignatureAlgorithm.HS256) // Firma el token con el algoritmo HMAC SHA-256
                .compact(); // Compacta el token en una cadena JWT
    }

    // Método privado para obtener la clave secreta como objeto Key
    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY); // Decodifica la clave secreta de Base64
        return Keys.hmacShaKeyFor(keyBytes); // Crea y devuelve la clave HMAC SHA basada en los bytes decodificados
    }

    // Método privado para obtener todas las reclamaciones (claims) de un token JWT
    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey()) // Establece la clave secreta para verificar la firma del token
                .build()
                .parseClaimsJws(token) // Parsea el token JWT para obtener el cuerpo de las reclamaciones
                .getBody();
    }

    // Método para obtener un reclamación específica de un token JWT
    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token); // Obtiene todas las reclamaciones del token
        return claimsResolver.apply(claims); // Aplica la función de resolución de reclamaciones a las reclamaciones del token
    }

    // Método para obtener el nombre de usuario (subject) desde un token JWT
    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject); // Obtiene el sujeto (nombre de usuario) del token
    }

    // Método para verificar si un token JWT es válido para un UserDetails dado
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token); // Obtiene el nombre de usuario del token
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token)); // Verifica si el nombre de usuario coincide y el token no ha expirado
    }

    // Método privado para obtener la fecha de expiración de un token JWT
    private Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration); // Obtiene la fecha de expiración del token
    }

    // Método privado para verificar si un token JWT ha expirado
    private boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date()); // Verifica si la fecha de expiración del token es anterior a la fecha actual
    }

    // Método para obtener el usuario asociado a un token JWT
    public User getUserFromToken(String token) {
        String username = getUsernameFromToken(token); // Obtiene el nombre de usuario desde el token
        return userRepository.findByUsername(username) // Busca al usuario en el repositorio por su nombre de usuario
                .orElseThrow(() -> new UserException("Usuario no encontrado")); // Lanza una excepción si el usuario no es encontrado
    }

    // Método para obtener el token JWT desde la solicitud HTTP
    public String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization"); // Obtiene el encabezado "Authorization" de la solicitud HTTP
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Devuelve el token JWT eliminando el prefijo "Bearer "
        }
        return null; // Devuelve null si no se encuentra ningún token JWT en la solicitud
    }

}
