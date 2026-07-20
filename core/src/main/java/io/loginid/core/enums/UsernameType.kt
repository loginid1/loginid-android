package io.loginid.core.enums

/**
 * Defines how LoginID validates and interprets a username value.
 */
enum class UsernameType(val value: String) {
    /**
     * No predefined username format is enforced.
     *
     * Usernames must be 5–128 characters long and may contain
     * letters, digits, `_`, `-`, `+`, `/`, and `=`.
     */
    OTHER("other"),

    /**
     * The username must be a valid email address.
     *
     * Email addresses are validated according to RFC 5322 rules.
     */
    EMAIL("email"),

    /**
     * The username must be a valid phone number.
     *
     * Phone numbers may be provided in E.164 format or in supported
     * human-friendly formats that can be normalized to E.164.
     */
    PHONE("phone")
}
