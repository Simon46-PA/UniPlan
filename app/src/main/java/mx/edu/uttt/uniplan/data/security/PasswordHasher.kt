package mx.edu.uttt.uniplan.data.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Mecanismo de seguridad: las contraseñas se guardan como hash PBKDF2 (HMAC-SHA256) con una sal
 * aleatoria por usuario. Nunca se almacena la contraseña original.
 */
object PasswordHasher {
    private const val ITERATIONS = 10_000
    private const val KEY_LENGTH_BITS = 256
    private const val SALT_BYTES = 16

    fun newSalt(): String {
        val bytes = ByteArray(SALT_BYTES)
        SecureRandom().nextBytes(bytes)
        return Base64.getEncoder().encodeToString(bytes)
    }

    fun hash(password: String, salt: String): String {
        val spec = PBEKeySpec(
            password.toCharArray(),
            Base64.getDecoder().decode(salt),
            ITERATIONS,
            KEY_LENGTH_BITS
        )
        val bytes = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
        return Base64.getEncoder().encodeToString(bytes)
    }

    /** Comparación en tiempo constante para no filtrar información por tiempos de respuesta. */
    fun matches(password: String, salt: String, expectedHash: String): Boolean =
        MessageDigest.isEqual(
            hash(password, salt).toByteArray(),
            expectedHash.toByteArray()
        )
}
