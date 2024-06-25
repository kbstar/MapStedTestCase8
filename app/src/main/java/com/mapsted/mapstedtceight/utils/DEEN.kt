package com.mapsted.mapstedtceight.utils

import android.util.Base64
import java.nio.charset.StandardCharsets.UTF_8
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * AES Encryption Decryption
 * @param secretKey size should be 128-Bit (16 chars) or 192-Bit (24 chars) or 256-Bit (32 chars)
 * @param initialVector size should be 128-Bit (16 chars)
 *
 * for more information take a look into {@link javax.crypto.Cipher}
 */
class DEEN(private val secretKey: String, private val initialVector: String) {

    private val algo = "AES"
    private val transformation = "AES/CBC/PKCS5PADDING"

    @Throws(Exception::class)
    private fun getCipher(mode: Int): Cipher {
        //size should be 128-Bit (16 chars)
        if (initialVector.length != 16) {
            throw Exception("Invalid length of Initial Vector - size should be 128-Bit (16 chars)")
        }

        if (!(secretKey.length == 16 || secretKey.length == 24 || secretKey.length == 32)) {
            throw Exception("Invalid length of Secret Key - size should be 128-Bit (16 chars) or 192-Bit (24 chars) or 256-Bit (32 chars)")
        }

        val cipher = Cipher.getInstance(transformation).apply {
            val secretKeySpec = SecretKeySpec(secretKey.toByteArray(UTF_8), algo)
            val ivParameterSpec = IvParameterSpec(initialVector.toByteArray(UTF_8))
            init(mode, secretKeySpec, ivParameterSpec)
        }
        return cipher
    }

    @Throws(Exception::class)
    fun encrypt(data: String): String {
        val cipher = getCipher(Cipher.ENCRYPT_MODE)
        val resultBytes = cipher.doFinal(data.toByteArray())
        return String(Base64.encode(resultBytes, Base64.DEFAULT), UTF_8).trim()
    }

    @Throws(Exception::class)
    fun decrypt(data: String): String {
        val cipher = getCipher(Cipher.DECRYPT_MODE)
        val resultBytes = cipher.doFinal(Base64.decode(data, Base64.DEFAULT))
        return String(resultBytes, UTF_8).trim()
    }
}