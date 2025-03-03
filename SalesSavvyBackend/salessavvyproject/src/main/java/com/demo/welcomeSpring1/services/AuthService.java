/*
	public User authenticate(String username, String password) {
	//ptional<USer> = userRepository.findByUsername(username);
		//user ser = user.get();
	//i(eu


package com.demo.welcomeSpring1.services;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.demo.welcomeSpring1.entities.JWTToken;
import com.demo.welcomeSpring1.entities.User;
import com.demo.welcomeSpring1.repositories.JWTTokenRepository;
import com.demo.welcomeSpring1.repositories.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class AuthService {
    private final Key SIGNING_KEY;
    private final UserRepository userRepository;
    private final JWTTokenRepository jwtTokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, JWTTokenRepository jwtTokenRepository, @Value("${jwt.secret}") String jwtSecret) {
        this.userRepository = userRepository;
        this.jwtTokenRepository = jwtTokenRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.SIGNING_KEY = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public User authenticate(String username, String password) {
        User user = userRepository.findByUsername(username) .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }
        return user;
    }

	public String generateToken(User user) {
		// TODO Auto-generated method stub
		  String token;
		  LocalDateTime currentTime = LocalDateTime.now();
		  //from repository fetch existing token
		  JWTToken existingToken = jwtTokenRepository.findByUserId(user.getUserId());
		  //get the time of token expiry
		 //ocalDateTime expiryTime = existingToken.getExpiresAt();
		  if (existingToken != null && currentTime.isBefore( existingToken.getExpiresAt())) {
			  token = existingToken.getToken();
			  }
		  else {
			  token = generateNewToken(user);
			  if(existingToken!=null) {
				  jwtTokenRepository.delete(existingToken);
			  }
			  saveToken(token, user);
		  }



		  return token;
	}
	public String generateNewToken(User user) {
		String token = Jwts.builder().
				setSubject(user.getUsername()).
				claim("role", user.getRole().name()).
				setIssuedAt(new Date()).
				setExpiration(new Date(System.currentTimeMillis()+3600000)).
				signWith(SIGNING_KEY, SignatureAlgorithm.HS512).
				compact();

		return token;
	}

	public void saveToken(String token) {
		JWTToken jwtToken = new JWTToken(user, token, LocalDateTime.now().plusHours(1));

		jwtTokenRepository.save(token);

	}
}
*/



package com.demo.welcomeSpring1.services;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.demo.welcomeSpring1.entities.JWTToken;
import com.demo.welcomeSpring1.entities.User;
import com.demo.welcomeSpring1.repositories.JWTTokenRepository;
import com.demo.welcomeSpring1.repositories.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class AuthService {
    private final Key SIGNING_KEY;
    private final UserRepository userRepository;
    private final JWTTokenRepository jwtTokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, JWTTokenRepository jwtTokenRepository,
                       @Value("${jwt.secret}") String jwtSecret) {
        this.userRepository = userRepository;
        this.jwtTokenRepository = jwtTokenRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.SIGNING_KEY = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public User authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }
        return user;
    }

    public String generateToken(User user) {
        LocalDateTime currentTime = LocalDateTime.now();

        // Fetch existing token
        JWTToken existingToken = jwtTokenRepository.findByUserId(user.getUserId());

        if (existingToken != null && currentTime.isBefore(existingToken.getExpiresAt())) {
            return existingToken.getToken();
        } else {
            String token = generateNewToken(user);

            if (existingToken != null) {
                jwtTokenRepository.delete(existingToken);
            }
            saveToken(user, token);
            return token;
        }
    }

    public String generateNewToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour
                .signWith(SIGNING_KEY, SignatureAlgorithm.HS256)//HS512
                .compact();
    }

    public void saveToken(User user, String token) {
        JWTToken jwtToken = new JWTToken(user, token, LocalDateTime.now().plusHours(1));
        jwtTokenRepository.save(jwtToken);
    }

	public boolean validateToken(String token) {
		// TODO Auto-generated method stub
		try {
			System.err.println("VALIDATING TOKEN...");
			// Parse and validate the token
			Jwts.parserBuilder()
			.setSigningKey(SIGNING_KEY)
		    .build()
		    .parseClaimsJws(token);
			// Check if the token exists in the database and is not expired
			Optional<JWTToken> jwtToken = jwtTokenRepository.findByToken(token);
			if (jwtToken.isPresent()) {
				System.err.println("Token Expiry: " + jwtToken.get().getExpiresAt());
				System.err.println("Current Time: " + LocalDateTime.now());
				return jwtToken.get().getExpiresAt().isAfter(LocalDateTime.now());
				}
			return false;
			} catch (Exception e) {
				System.err.println("Token validation failed: " + e.getMessage());
				return false;
				}
		}


	public String extractUsername(String token) {
		// TODO Auto-generated method stub
	 return Jwts.parserBuilder()
			 .setSigningKey(SIGNING_KEY)
			 .build()
			 .parseClaimsJws(token)
			 .getBody()
			 .getSubject();

	}
	public void logout(User user)
 {
		int tuserId = user.getUserId();
		if(user!=null) {
			int userId = user.getUserId();
			jwtTokenRepository.deleteByUserId(userId);
		}
		}
 }



