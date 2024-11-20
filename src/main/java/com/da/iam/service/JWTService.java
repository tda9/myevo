package com.da.iam.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JWTService {
    private static final String SECRET = "e38d1dc06f02bc2435485df871653622678e9e1cdf74105119c9d376abdffd6c";
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey()).build()
                .parseClaimsJws(token).getBody();
    }
    public <T> T extractClaim(String token, Function<Claims,T> cliamResolver){
        final Claims claims = extractAllClaims(token);
        return cliamResolver.apply(claims);
    }

    public String extractEmail(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }
    private Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername()))&&!isTokenExpired(token);
    }

    public String generateToken(UserDetails userDetails){
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*1))
                .signWith(getSignInKey(), io.jsonwebtoken.SignatureAlgorithm.HS256)
                .compact();
    }

}
