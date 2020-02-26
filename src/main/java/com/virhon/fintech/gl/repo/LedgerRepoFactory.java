package com.virhon.fintech.gl.repo;

import java.io.IOException;

public interface LedgerRepoFactory {
    AttrRepo getAccountAttributeRepository() throws IOException;
    CurPageRepo getCurrentPageRepository() throws IOException;
    HistPageRepo getHistoricalPageRepository() throws IOException;
    ReservationRepo getReservationRepository() throws IOException;
}
