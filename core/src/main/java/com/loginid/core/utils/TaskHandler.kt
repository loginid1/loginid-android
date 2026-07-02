package com.loginid.core.utils

import com.loginid.core.errors.LoginIDError
import com.loginid.core.errors.LoginIDException
import kotlin.coroutines.cancellation.CancellationException

/**
 * A utility object for handling suspendable tasks with standardized error handling.
 */
object TaskHandler {

    /**
     * Executes a given suspendable task and handles potential errors by wrapping them
     * in LoginID-specific exceptions.
     *
     * @param T The return type of the task.
     * @param task The suspendable lambda function to execute.
     * @return The result of the task if successful.
     * @throws LoginIDError for known LoginID API errors.
     * @throws LoginIDException for other exceptions that are parsed into LoginID errors.
     * @throws kotlin.coroutines.cancellation.CancellationException if the coroutine is cancelled.
     */
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
