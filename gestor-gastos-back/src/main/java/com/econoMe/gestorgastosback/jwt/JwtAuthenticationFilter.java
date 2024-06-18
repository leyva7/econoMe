package com.econoMe.gestorgastosback.jwt;

import com.econoMe.gestorgastosback.exception.InvalidJwtAuthenticationException;
import com.econoMe.gestorgastosback.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.http.HttpHeaders;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService; // Servicio para operaciones relacionadas con JWT
    private final UserDetailsService userDetailsService; // Servicio para cargar detalles de usuario

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            final String token = getTokenFromRequest(request); // Obtiene el token JWT del encabezado de la solicitud
            final String username;

            if (token == null) {
                // Si no hay token, continúa con el siguiente filtro
                filterChain.doFilter(request, response);
                return;
            }

            // Extrae el nombre de usuario desde el token JWT
            username = jwtService.getUsernameFromToken(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Si el nombre de usuario es válido y no hay autenticación en el contexto actual
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(token, userDetails)) {
                    // Si el token JWT es válido para el usuario
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    // Si el token JWT no es válido
                    throw new InvalidJwtAuthenticationException("Token JWT no válido");
                }
            }
        } catch (ExpiredJwtException e) {
            // Captura la excepción si el token JWT ha expirado
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Establece el código de estado 401 (Unauthorized)
            response.setContentType("application/json"); // Establece el tipo de contenido de la respuesta como JSON
            response.getWriter().write("{\"error\": \"El token JWT ha expirado. Por favor, inicia sesión nuevamente.\"}"); // Escribe el mensaje de error en la respuesta JSON
            return;
        }

        // Continúa con el siguiente filtro en la cadena de filtros
        filterChain.doFilter(request, response);
    }

    // Método para obtener el token JWT del encabezado de la solicitud
    private String getTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Devuelve solo el token (sin el prefijo "Bearer ")
        }

        return null; // Devuelve null si no se encontró un token válido en el encabezado
    }
}
