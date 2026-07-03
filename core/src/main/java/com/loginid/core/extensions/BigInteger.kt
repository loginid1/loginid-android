package com.loginid.core.extensions

import java.math.BigInteger

/**
 * Converts a [BigInteger] to an unsigned byte array representation.
 *
 * This function handles the case where `BigInteger.toByteArray()` may prepend a zero byte
 * to indicate a positive number when the most significant bit is set.
 *
 * @return The unsigned byte array representation of the [BigInteger].
 */
internal fun BigInteger.toUnsignedByteArray(): ByteArray {
    val fullBytes = this.toByteArray()
    return if (fullBytes[0] == 0.toByte()) fullBytes.copyOfRange(1, fullBytes.size) else fullBytes
}