package com.virhon.fintech.gl.repo;

import java.io.IOException;

public interface LedgerFactory {
    AttrRepo getAccountAttributeRepository() throws IOException;
    CurPageRepo getCurrentPageRepository() throws IOException;
    HistPageRepo getHistoricalPageRepository() throws IOException;
    ReservationRepo getReservationRepository() throws IOException;
    TransferRepo getTransferRepository() throws IOException;
}
