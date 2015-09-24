package com.networkedassets.util.functional;

import java.util.Optional;

/**
 * Utility class for dealing with Options
 */
public class Options {
    public static <T> Optional<T> ofThrowing(Throwing.Supplier<T> supplier) {
        try {
            return Optional.of(supplier.get());
        } catch (Throwable t) {
            return Optional.empty();
        }
    }
}
