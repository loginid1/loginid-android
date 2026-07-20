package com.loginid.mfa.models

import io.loginid.client.model.MfaAction
import io.loginid.client.model.MfaNext
import com.squareup.moshi.JsonClass

/**
 * Represents multi-factor authentication (MFA) information for a user.
 *
 * @property username The username of the account undergoing MFA. May be `null` if the username is not yet known or required.
 * @property flow The current MFA flow being executed. May be `null` if the flow has not started.
 * @property next The list of next possible MFA actions to perform. May be `null` or empty if there are no available actions.
 * @property session An opaque object containing session data. May be `null` if there is no active session.
 */
@JsonClass(generateAdapter = true)
internal data class MFAInfo(
    val username: String?,
    val flow: MfaNext.Flow?,
    val next: List<MfaAction>?,
    val session: String?
) {
    /**
     * Initializes a `MFAInfo` from an auto-generated `MfaNext`.
     * @param result The auto-generated result object.
     * @param username The username of the account undergoing MFA.
     */
    internal constructor(result: MfaNext, username: String?) : this(
        username = username,
        flow = result.flow,
        next = result.next,
        session = result.session
    )

    /**
     * Initializes a `MFAInfo` with username and flow.
     * @param username The username of the account undergoing MFA.
     * @param flow The current MFA flow being executed.
     */
    internal constructor(username: String?, flow: MfaNext.Flow?) : this(
        username = username,
        flow = flow,
        next = emptyList(),
        session = null
    )

    /**
     * Initializes a `MFAInfo` from an existing `MFAInfo` and a new session.
     * @param info The existing MFAInfo object.
     * @param session The new session string.
     */
    internal constructor(info: MFAInfo, session: String?) : this(
        username = info.username,
        flow = info.flow,
        next = info.next,
        session = session
    )
}
