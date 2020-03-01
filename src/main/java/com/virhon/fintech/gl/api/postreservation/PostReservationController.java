package com.virhon.fintech.gl.api.postreservation;

import com.virhon.fintech.gl.api.LedgerError;
import com.virhon.fintech.gl.api.maketransfer.TransferData;
import com.virhon.fintech.gl.exception.LedgerException;
import com.virhon.fintech.gl.model.Ledger;
import com.virhon.fintech.gl.model.Reservation;
import com.virhon.fintech.gl.model.Transfer;
import com.virhon.fintech.gl.repo.IdentifiedEntity;
import com.virhon.fintech.gl.repo.mysql.MySQLGeneralLedger;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/gl/{currencyCode}/reservations/{reservationUuid}")
public class PostReservationController {
    final static Logger LOGGER = Logger.getLogger(PostReservationController.class);

    @Autowired
    MySQLGeneralLedger gl;

    @PutMapping
    public ResponseEntity<?> postReservation(@PathVariable String currencyCode,
                                             @PathVariable String reservationUuid,
                                             @RequestBody PostReservationRequest request) {
        try {
            request.checkNotNullAllFields();
            request.getReportedOn().checkNotNullAllFields();
            final Ledger ledger = gl.getLedger(currencyCode);
            final IdentifiedEntity<Reservation> iRes = ledger.getReservationRepo().getByUuid(reservationUuid);
            final Reservation res = iRes.getEntity();
            final Transfer tr = ledger.transferFunds(res.getTransferRef(), "Client's UUID must be here",
                    "CLientCustomerId", res.getDebitId(), res.getCreditId(), res.getAmount(),
                    request.getRepAmount(), request.getReportedOn().asLocalDate(), res.getDescription());
            gl.commit();
            final TransferData response = ledger.createTransferResponseBody(tr);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (LedgerException e) {
            LOGGER.error(e.getMessage());
            final LedgerError error = new LedgerError(e.getCode(), e.getMessage());
            return new ResponseEntity<LedgerError>(error, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            final LedgerError error = new LedgerError(500, e.getMessage());
            return new ResponseEntity<LedgerError>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
