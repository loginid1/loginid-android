package com.loginid.core.utils

import java.io.ByteArrayInputStream
import java.math.BigInteger

object CryptoUtils {
    /**
     * Converts a DER-encoded ECDSA signature into the concatenated R||S format.
     *
     * @param derSignature The DER-encoded signature as a byte array.
     * @return The signature in the concatenated R||S format.
     */
    fun derToConcatenatedRS(derSignature: ByteArray): ByteArray {
        val input = ByteArrayInputStream(derSignature)
        if (input.read() != 0x30) throw IllegalArgumentException("Invalid DER signature")

        input.read() // skip length
        if (input.read() != 0x02) throw IllegalArgumentException("Invalid R marker")
        val rLength = input.read()
        val rBytes = ByteArray(rLength)
        input.read(rBytes)

        if (input.read() != 0x02) throw IllegalArgumentException("Invalid S marker")
        val sLength = input.read()
        val sBytes = ByteArray(sLength)
        input.read(sBytes)

        val r = BigInteger(1, rBytes)
        val s = BigInteger(1, sBytes)

        val rawR = r.toByteArray().dropWhile { it == 0.toByte() }.toByteArray()
        val rawS = s.toByteArray().dropWhile { it == 0.toByte() }.toByteArray()

        val rPadded = ByteArray(32) { 0 }
        val sPadded = ByteArray(32) { 0 }
        System.arraycopy(rawR, 0, rPadded, 32 - rawR.size, rawR.size)
        System.arraycopy(rawS, 0, sPadded, 32 - rawS.size, rawS.size)

        return rPadded + sPadded
    }
}