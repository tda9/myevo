package com.da.iam.service;

import com.da.iam.entity.User;
import com.da.iam.exception.UserNotFoundException;
import com.da.iam.repo.BlackListTokenRepo;
import com.da.iam.repo.UserRepo;
import com.da.iam.utils.RSAKeyUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.function.Function;

@Service
@RequiredArgsConstructor

public class JWTService {
    private static final String SECRET = "e38d1dc06f02bc2435485df871653622678e9e1cdf74105119c9d376abdffd6c";
    @Autowired
    BlackListTokenRepo blackListTokenRepo;
    @Autowired
    UserRepo userRepo;

    /**
     * Generate a JWT token
     * <p>
     * Description:
     * 1. Set the subject of the token to the username of the user
     * 2. Set the issued time of the token to the generated time
     * 3. expiration time of the token: 10 minutes start from generated time
     * 4. Sign the token with the key
     * 5. Compact the token and return it
     * </p>

     * @return JWT token
     */
    public String generateToken(String username) throws Exception {
        PrivateKey privateKey = rsaKeyUtil.getPrivateKey();
        //1 phut
        return Jwts.builder().setSubject(username).setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + 1000*60*10))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    private final RSAKeyUtil rsaKeyUtil;

    public Claims extractAllClaims(String token) throws Exception {
        PublicKey publicKey = rsaKeyUtil.getPublicKey();
        return Jwts.parserBuilder().setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) throws Exception {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    public String extractEmail(String jwt) throws Exception {
        return extractClaim(jwt, Claims::getSubject);
    }

    private Date extractExpiration(String token) throws Exception {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) throws Exception {
        return extractExpiration(token).before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) throws Exception {
        User u = userRepo.findByEmail(userDetails.getUsername()).orElseThrow(()->new UserNotFoundException("User  not found"));
        if (!blackListTokenRepo.findTopByUserIdOrderByCreatedAtDesc(u.getUserId()).get().getToken().equals(token)) {
            return false;
        }
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
    // Extract all claims from the token and return them in a Claims object
//    private Claims extractAllClaims(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(getSignInKey()).build()
//                .parseClaimsJws(token).getBody();
//    }

    // Get the key to sign the token
//    private Key getSignInKey() {
//        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }

}
