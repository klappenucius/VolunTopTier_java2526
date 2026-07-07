package com.voluntoptier.project.core;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * Generic pipeline for processing collections in a readable, reusable way.
 * Pipeline stays GENERIC: it must not contain domain-specific logic.
 */
public final class Pipeline<T> {

    private final List<T> data;

    public Pipeline(Collection<T> source) {
        if (source == null) {
            this.data = List.of();
        } else {
            // defensive copy
            this.data = new ArrayList<>(source);
        }
    }

    private Pipeline(List<T> data) {
        this.data = data;
    }

    public Pipeline<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "predicate");
        List<T> filtered = data.stream()
                .filter(predicate)
                .collect(Collectors.toList());
        return new Pipeline<>(filtered);
    }

    public Pipeline<T> sort(Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator, "comparator");
        List<T> sorted = data.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
        return new Pipeline<>(sorted);
    }

    public <R> Pipeline<R> map(Function<? super T, ? extends R> mapper) {
        Objects.requireNonNull(mapper, "mapper");
        List<R> mapped = data.stream()
                .map(mapper)
                .collect(Collectors.toList());
        return new Pipeline<>(mapped);
    }

    public IntPipeline mapToInt(ToIntFunction<? super T> mapper) {
        Objects.requireNonNull(mapper, "mapper");
        return new IntPipeline(data, mapper);
    }

    public Optional<T> findFirst(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "predicate");
        return data.stream().filter(predicate).findFirst();
    }

    public Optional<T> min(Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator, "comparator");
        return data.stream().min(comparator);
    }

    public Optional<T> max(Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator, "comparator");
        return data.stream().max(comparator);
    }

    public boolean anyMatch(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "predicate");
        return data.stream().anyMatch(predicate);
    }

    public boolean allMatch(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "predicate");
        return data.stream().allMatch(predicate);
    }

    public long count() {
        return data.size();
    }

    public void forEach(Consumer<? super T> action) {
        Objects.requireNonNull(action, "action");
        data.forEach(action);
    }

    public List<T> toList() {
        return List.copyOf(data);
    }

    /**
     * Helper inner class for int aggregations.
     */
    public static final class IntPipeline {
        private final List<?> data;
        private final ToIntFunction<Object> mapper;

        @SuppressWarnings("unchecked")
        private <T> IntPipeline(List<T> data, ToIntFunction<? super T> mapper) {
            this.data = data;
            this.mapper = (ToIntFunction<Object>) mapper;
        }

        public int sum() {
            int total = 0;
            for (Object item : data) {
                total += mapper.applyAsInt(item);
            }
            return total;
        }

        public OptionalInt min() {
            if (data.isEmpty()) return OptionalInt.empty();
            int m = Integer.MAX_VALUE;
            for (Object item : data) {
                m = Math.min(m, mapper.applyAsInt(item));
            }
            return OptionalInt.of(m);
        }

        public OptionalInt max() {
            if (data.isEmpty()) return OptionalInt.empty();
            int m = Integer.MIN_VALUE;
            for (Object item : data) {
                m = Math.max(m, mapper.applyAsInt(item));
            }
            return OptionalInt.of(m);
        }
    }
}
