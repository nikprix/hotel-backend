package com.mykolabs.hotel.util;

import com.mykolabs.hotel.persistence.EmployeeDAO;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang.StringUtils;

import java.security.Key;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import javax.ws.rs.core.Context;

/**
 * Contains JWT generation methods as well as validation helpers.
 *
 * Ref. https://stormpath.com/blog/jwt-java-create-verify
 *
 * @author nikprixmar
 */
public class TokenUtil {

    private static final Logger log = Logger.getLogger(TokenUtil.class.getName());

    /**
     * Generates JWT and returns as String.
     *
     * @param username
     * @param id
     * @param roles
     * @param expires
     * @param apiKey
     * @return
     * @throws Exception
     */
    public static String getJWTString(String username, int id, String[] roles, Date expires, SecretKey apiKey) throws Exception {

        log.log(Level.INFO, "Injected secret key: {0}", Base64.getEncoder().encodeToString(apiKey.getEncoded()));

        // Issue a token (can be a random String persisted to a database or a JWT token)
        // The issued token must be associated to a user
        // Return the issued token
        if (username == null) {
            throw new NullPointerException("null username is illegal");
        }
        if (roles == null) {
            throw new NullPointerException("null roles are illegal");
        }
        if (expires == null) {
            throw new NullPointerException("null expires is illegal");
        }
        if (apiKey == null) {
            throw new NullPointerException("null apiKey is illegal");
        }

        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        // Signing JWT with ApiKey secret        
        //byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(apiKey.getEncoded());
        Key signingKey = new SecretKeySpec(apiKey.getEncoded(), signatureAlgorithm.getJcaName());

        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder()
                .setIssuedAt(getCurrentDate())
                .setExpiration(expires)
                .setSubject(username)
                .setAudience(StringUtils.join(Arrays.asList(roles), ","))
                .setIssuer("BookStore")
                .setId(String.valueOf(id))
                .signWith(signatureAlgorithm, signingKey);

        //Build the JWT and serialize it to a compact, URL-safe string
        String jwtString = builder.compact();

        //Persist token to the DB
        EmployeeDAO employeeDAO = new EmployeeDAO();
        try {
            employeeDAO.setToken(username, jwtString);
        } catch (SQLException ex) {
            throw new Exception("Error while saving token into the DB. See server logs for details.");
        }

        log.log(Level.INFO, "JWT string: {0}", jwtString);
        return jwtString;
    }

    /**
     * Validates provided by client token.
     *
     * @param token
     * @param key
     * @return
     */
    public static boolean isValid(String token, Key key) {
        try {
            log.log(Level.INFO, "Secret KEY passed to the validation: {0}", Base64.getEncoder().encodeToString(key.getEncoded()));
            Jwts.parser().setSigningKey(key).parseClaimsJws(token.trim());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns Name from token's claim.
     *
     * @param jwsToken
     * @param key
     * @return
     */
    public static String getName(String jwsToken, Key key) {
        if (isValid(jwsToken, key)) {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(key).parseClaimsJws(jwsToken);
            return claimsJws.getBody().getSubject();
        }
        return null;
    }

    /**
     * Returns roles from Token's claim.
     *
     * @param jwsToken
     * @param key
     * @return
     */
    public static String[] getRoles(String jwsToken, Key key) {
        if (isValid(jwsToken, key)) {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(key).parseClaimsJws(jwsToken);
            return claimsJws.getBody().getAudience().split(",");
        }
        return new String[]{};
    }

    /**
     * Returns id from token's claim.
     *
     * @param jwsToken
     * @param key
     * @return
     */
    public static int getId(String jwsToken, Key key) {
        if (isValid(jwsToken, key)) {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(key).parseClaimsJws(jwsToken);
            return Integer.parseInt(claimsJws.getBody().getId());
        }
        return -1;
    }

    /**
     * Returns expiration date from token's claim.
     *
     * @param jwsToken
     * @param key
     * @return
     */
    public static Date getExpiryDate(String jwsToken, Key key) {
        if (isValid(jwsToken, key)) {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(key).parseClaimsJws(jwsToken);
            return claimsJws.getBody().getExpiration();
        }
        return null;
    }

    /**
     * Current Date.
     *
     * @return
     */
    public static Date getCurrentDate() {
        long nowMillis = System.currentTimeMillis();
        return new Date(nowMillis);
    }

}
