package com.mykolabs.hotel.authentication;

import org.apache.commons.codec.binary.Base64;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Helper class for generating hashed passwords Based on
 * http://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
 * http://stackoverflow.com/a/11038230/5971690
 * http://stackoverflow.com/a/19349227/5971690
 *
 * @author nikprixmar
 */
public class PassGen {

    private static final Logger log = Logger.getLogger(PassGen.class.getName());

    // The higher the number of iterations the more 
    // expensive computing the hash is for us and
    // also for an attacker.
    private static final int ITERATIONS = 20 * 1000;
    private static final int SALTLEN = 32;
    private static final int DESIREDKEYLEN = 256;

    /**
     * Computes a salted PBKDF2 hash of given plaintext password suitable for
     * storing in a database. Empty passwords are not supported.
     *
     * @param password
     * @return
     * @throws java.lang.Exception
     */
    public static String getSaltedHash(String password) throws Exception {
        byte[] salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(SALTLEN);
        // store the salt with the password
        return Base64.encodeBase64String(salt) + "$" + hash(password, salt);
    }

    /**
     * Checks whether given plaintext password corresponds to a stored salted
     * hash of the password.
     *
     * @param password
     * @param stored
     * @return
     * @throws java.lang.Exception
     */
    public static boolean check(String password, String stored) throws Exception {
        String[] saltAndPass = stored.split("\\$");
        if (saltAndPass.length != 2) {
            throw new IllegalStateException(
                    "The stored password have the form 'salt$hash'");
        }
        String hashOfInput = hash(password, Base64.decodeBase64(saltAndPass[0]));

        log.log(Level.INFO, "Provided password salted hash: {0}", hashOfInput);

        return hashOfInput.equals(saltAndPass[1]);
    }

    // using PBKDF2 from Sun, an alternative is https://github.com/wg/scrypt
    // cf. http://www.unlimitednovelty.com/2012/03/dont-use-bcrypt.html
    /**
     * Hashes Password with Salt
     *
     * @param password
     * @param salt
     * @return
     * @throws Exception
     */
    private static String hash(String password, byte[] salt) throws Exception {
        if (password == null || password.length() == 0) {
            throw new IllegalArgumentException("Empty passwords are not supported.");
        }
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        SecretKey key = f.generateSecret(new PBEKeySpec(
                password.toCharArray(), salt, ITERATIONS, DESIREDKEYLEN)
        );
        
        log.log(Level.INFO, "Returned hash: {0}", Base64.encodeBase64String(key.getEncoded()));
        
        return Base64.encodeBase64String(key.getEncoded());
    }

}
