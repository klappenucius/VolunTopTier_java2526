package com.voluntoptier.project.core;

import com.voluntoptier.project.exceptions.ImpossibleInputDataException;

/**
 * Generic base wrapper for validating a single entity instance.
 * Wrappers contain business rules for ONE object (not collections, not permissions).
 */
public abstract class Wrapper<T> {

    protected final T entity;

    protected Wrapper(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Wrapped entity cannot be null.");
        }
        this.entity = entity;
    }

    public T get() {
        return entity;
    }

    /**
     * Validate the entity according to business rules.
     * @throws ImpossibleInputDataException if entity is invalid
     */
    public abstract void validate() throws ImpossibleInputDataException;

    /**
     * Convenience method for validation checks.
     */
    public boolean isValid() {
        try {
            validate();
            return true;
        } catch (ImpossibleInputDataException e) {
            return false;
        }
    }
}
