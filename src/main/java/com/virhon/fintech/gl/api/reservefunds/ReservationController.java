package com.virhon.fintech.gl.api.reservefunds;

import com.virhon.fintech.gl.api.LedgerError;
import com.virhon.fintech.gl.exception.LedgerException;
import com.virhon.fintech.gl.model.Ledger;
import com.virhon.fintech.gl.model.Reservation;
import com.virhon.fintech.gl.repo.IdentifiedEntity;
import com.virhon.fintech.gl.repo.mysql.MySQLGeneralLedger;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/gl/{currencyCode}/accounts/{debitAccountUuid}/reservations")
public class ReservationController {
    final static Logger LOGGER = Logger.getLogger(ReservationController.class);

    @Autowired
    MySQLGeneralLedger gl;

    @PostMapping
    public ResponseEntity<?> reserveFunds(@PathVariable String currencyCode,
                                          @PathVariable String debitAccountUuid,
                                          @RequestBody NewReservationRequest request) {
        try {
            request.checkNotNullAllFields();
            final Ledger ledger = gl.getLedger(currencyCode);
            final IdentifiedEntity<Reservation> iRes = ledger.reserveFunds(request.getTransferRef(), debitAccountUuid,
                    request.getCreditAccountUuid(), request.getAmount(), request.getDescription());
            final NewReservationResponse response = new NewReservationResponse();
            response.setUuid(iRes.getEntity().getUuid());
            response.setTransferRef(iRes.getEntity().getTransferRef());
            response.setDebitAccountUuid(debitAccountUuid);
            response.setCreditAccountUuid(request.getCreditAccountUuid());
            response.setAmount(iRes.getEntity().getAmount());
            response.setDescription(iRes.getEntity().getDescription());
            response.setExpireAt(iRes.getEntity().getExpireAt().toString());
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