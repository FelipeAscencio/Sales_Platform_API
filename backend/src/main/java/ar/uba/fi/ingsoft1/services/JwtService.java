package ar.uba.fi.ingsoft1.services;

import ar.uba.fi.ingsoft1.controller.users.UserDTO;
import ar.uba.fi.ingsoft1.domain.User;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final SecretKey KEY = Jwts.SIG.HS256.key().build();  // TODO: check were it should be

    public String generateToken(UserDTO user) {
        return Jwts.builder().subject(user.getEmail()).claim("admin", user.getIsAdmin()).signWith(KEY).compact();
    }

    public boolean validateToken(String token, String email) {
        try {
            return Jwts.parser().verifyWith(KEY).build().parseSignedClaims(token).getPayload().getSubject().equals(email);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateAdminToken(String token) {
        try {
            return Jwts.parser().verifyWith(KEY).build().parseSignedClaims(token).getBody().get("admin", Boolean.class);
        } catch (Exception e) {
            return false;
        }
    }
}
