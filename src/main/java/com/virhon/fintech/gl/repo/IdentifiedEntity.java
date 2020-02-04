package com.virhon.fintech.gl.repo;

public class IdentifiedEntity<T> {
    private Long id;
    private T entity;

    public IdentifiedEntity(Long id, T entity) {
        this.id = id;
        this.entity = entity;
    }

    public Long getId() {
        return id;
    }

    public T getEntity() {
        return entity;
    }
}
