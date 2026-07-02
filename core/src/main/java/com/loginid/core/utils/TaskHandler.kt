package com.loginid.core.utils

import com.loginid.core.errors.LoginIDError
import com.loginid.core.errors.LoginIDException
import kotlin.coroutines.cancellation.CancellationException

object TaskHandler {

    suspend fun <T> executeTask(
        task: suspend () -> T
    ): T {
        return try {
            task()
        } catch (error: CancellationException) {
            throw error
        } catch (error: LoginIDError) {
            throw error
        } catch (error: Exception) {
            throw LoginIDException.parseError(error)
        } catch (_: Throwable) {
            throw LoginIDError.unknownError()
        }
    }
}
