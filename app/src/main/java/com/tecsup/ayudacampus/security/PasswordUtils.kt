
package com.tecsup.ayudacampus.security

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordUtils {

    private const val ITERACIONES = 210_000
    private const val LONGITUD_CLAVE = 256

    fun generarHash(clave: String): String {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)

        val hash = derivarClave(clave, salt, ITERACIONES)

        return listOf(
            "pbkdf2",
            ITERACIONES.toString(),
            Base64.encodeToString(salt, Base64.NO_WRAP),
            Base64.encodeToString(hash, Base64.NO_WRAP)
        ).joinToString(":")
    }

    fun verificar(clave: String, valorGuardado: String): Boolean {
        if (!valorGuardado.startsWith("pbkdf2:")) {
            // Compatibilidad temporal con las cuentas de práctica.
            return clave == valorGuardado
        }

        return try {
            val partes = valorGuardado.split(":")
            if (partes.size != 4) return false

            val iteraciones = partes[1].toInt()
            if (iteraciones !in 100_000..1_000_000) return false

            val salt = Base64.decode(partes[2], Base64.NO_WRAP)
            val hashGuardado = Base64.decode(partes[3], Base64.NO_WRAP)

            val hashCalculado = derivarClave(
                clave,
                salt,
                iteraciones,
                hashGuardado.size * 8
            )

            java.security.MessageDigest.isEqual(
                hashCalculado,
                hashGuardado
            )
        } catch (e: Exception) {
            false
        }
    }

    private fun derivarClave(
        clave: String,
        salt: ByteArray,
        iteraciones: Int,
        longitud: Int = LONGITUD_CLAVE
    ): ByteArray {
        val especificacion = PBEKeySpec(
            clave.toCharArray(),
            salt,
            iteraciones,
            longitud
        )

        return try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(especificacion)
                .encoded
        } finally {
            especificacion.clearPassword()
        }
    }
}
