package com.virhon.fintech.gl.repo.mysql.reservation;

import com.virhon.fintech.gl.model.Reservation;
import com.virhon.fintech.gl.repo.IdentifiedEntity;
import com.virhon.fintech.gl.repo.ReservationRepo;
import com.virhon.fintech.gl.repo.mysql.MySQLAbstactRepo;

import java.io.IOException;

public class MySQLReservationRepo extends MySQLAbstactRepo<MySQLReservationDAO> implements ReservationRepo {
    public MySQLReservationRepo(String tablename) throws IOException {
        super(tablename, MySQLReservationDAO.class);
    }
    @Override
    public IdentifiedEntity<Reservation> getById(Long id) {
        final MySQLReservationRecord record = this.getMapper().selectById(this.getTablename(), id);
        if (record != null) {
            final Reservation reservation = getConverter().fromJson(record.getData(), Reservation.class);
            return new IdentifiedEntity<>(id, reservation);
        } else {
            return null;
        }
    }

    @Override
    public IdentifiedEntity<Reservation> getByIdExclusive(Long id) {
        final MySQLReservationRecord record = this.getMapper().selectByIdExclusive(this.getTablename(), id);
        if (record != null) {
            final Reservation reservation = getConverter().fromJson(record.getData(), Reservation.class);
            return new IdentifiedEntity<>(id, reservation);
        } else {
            return null;
        }
    }

    @Override
    public IdentifiedEntity<Reservation> insert(Reservation reservation) {
        final MySQLReservationRecord record = new MySQLReservationRecord();
        final String data = getConverter().toJson(reservation);
        record.setUuid(reservation.getUuid());
        record.setExpireAt(reservation.getExpireAt());
        record.setData(data);
        getMapper().insert(getTablename(), record);
        return new IdentifiedEntity<Reservation>(record.getId(), reservation);
    }

    @Override
    public IdentifiedEntity<Reservation> getByUuid(String uuid) {
        final MySQLReservationRecord record = this.getMapper().selectByUuid(this.getTablename(), uuid);
        if (record != null) {
            final Reservation reservation = getConverter().fromJson(record.getData(), Reservation.class);
            return new IdentifiedEntity<>(record.getId(), reservation);
        } else {
            return null;
        }
    }

    @Override
    public void delete(Long id) {
        getMapper().delete(getTablename(), id);
    }

}
