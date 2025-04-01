package khuong.com.kitchendomain.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TokenExtractor {

    private final JwtService jwtService;

    @SuppressWarnings("unchecked")
    public UserDetails getUserDetailsFromToken(String token) {
        Claims claims = jwtService.extractAllClaims(token);
        String username = claims.getSubject();
        
        List<String> roles = (List<String>) claims.get("roles");
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        
        return new User(username, "", authorities);
    }
}