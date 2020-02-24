package com.virhon.fintech.gl.repo;

import com.virhon.fintech.gl.model.Reservation;

public interface ReservationRepo {
    IdentifiedEntity<Reservation> getById(Long id);
    IdentifiedEntity<Reservation> getByIdExclusive(Long id);
    IdentifiedEntity<Reservation> getByUuid(String uuid);
    IdentifiedEntity<Reservation> insert(Reservation reservation);
    void delete(Long id);
}
