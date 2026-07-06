package com.loginid.core.extensions

/**
 * Returns the first element from the current collection that also exists in the
 * given `ordered` collection, preserving the order of the `ordered` collection.
 *
 * @param ordered The collection whose element order determines priority for the match.
 * @return The first matching element found in both collections, or `null` if no match exists.
 */
fun <T> Collection<T>.firstMatch(ordered: Collection<T>): T? {
    val set = this.toSet()
    return ordered.firstOrNull { set.contains(it) }
}
