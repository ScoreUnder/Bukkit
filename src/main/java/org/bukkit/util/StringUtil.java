package org.bukkit.util;

import java.util.Iterator;

import org.apache.commons.lang.Validate;

public class StringUtil {

    /**
     * Removes all elements of the given list which do not start (case insensitive) with the specified search token.
     *
     * @param token String to search for
     * @param collection An iterable collection of strings to filter. The collection will be modified if there is a non-match.
     * @return the collection of strings provided
     * @throws UnsupportedOperationException if the list is immutable and contains a string which does not start with the specified search string.
     * @throws IllegalArgumentException if token is null
     * @throws IlelgalArgumentException if collection is null
     * @throws IllegalArgumentException if collection contains a null element. <b>Note: the collection may be modified before this is thrown</b>
     */
    public static <T extends Iterable<String>> T retainPartialMatches(final String token, final T collection) throws UnsupportedOperationException, IllegalArgumentException {
        Validate.notNull(token, "Search token cannot be null");
        Validate.notNull(collection, "Collection cannot be null");
        Iterator<String> iterator = collection.iterator();
        while (iterator.hasNext()) {
            if (!startsWithIgnoreCase(iterator.next(), token)) {
                iterator.remove();
            }
        }

        return collection;
    }

    /**
     * This method uses a substring to check case-insensitive equality. This means the internal array does not need to be copied like a toLowerCase() call would.
     *
     * @param string String to check
     * @param prefix Prefix of string to compare
     * @return true if provided string starts with, ignoring case, the prefix provided
     * @throws NullPointerException if prefix is null
     * @throws IllegalArgumentException if string is null
     */
    public static boolean startsWithIgnoreCase(final String string, final String prefix) throws IllegalArgumentException, NullPointerException {
        Validate.notNull(string, "Cannot check a null string for a match");
        if (string.length() < prefix.length()) {
            return false;
        }
        return string.substring(0, prefix.length()).equalsIgnoreCase(prefix);
    }
}
