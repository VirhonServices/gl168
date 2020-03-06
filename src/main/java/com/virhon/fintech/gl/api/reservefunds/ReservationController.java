package com.virhon.fintech.gl.api.reservefunds;

import com.google.gson.Gson;
import com.virhon.fintech.gl.GsonConverter;
import com.virhon.fintech.gl.api.LedgerError;
import com.virhon.fintech.gl.exception.LedgerException;
import com.virhon.fintech.gl.model.Ledger;
import com.virhon.fintech.gl.model.Reservation;
import com.virhon.fintech.gl.repo.IdentifiedEntity;
import com.virhon.fintech.gl.repo.mysql.MySQLGeneralLedger;
import com.virhon.fintech.gl.signature.SignatureChecker;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;

import static com.virhon.fintech.gl.api.APIConfig.*;

@RestController
@RequestMapping("/v1/gl/{currencyCode}/accounts/{debitAccountUuid}/reservations")
public class ReservationController {
    final static Logger LOGGER = Logger.getLogger(ReservationController.class);

    @Autowired
    MySQLGeneralLedger gl;

    @Autowired
    SignatureChecker checker;

    private Gson gc = GsonConverter.create();

    @PostMapping
    public ResponseEntity<?> reserveFunds(@RequestHeader(value = CLIENT_UUID_HEADER) String hClientUuid,
                                          @RequestHeader(value = SIGNATURE_HEADER) String hSignature,
                                          @RequestHeader(value = DATE_HEADER) String hDate,
                                          @PathVariable String currencyCode,
                                          @PathVariable String debitAccountUuid,
                                          @RequestBody NewReservationRequest request) {
        try {
            final String req = gc.toJson(request);
            checker.validateSignature(hClientUuid, hDate, req, hSignature);
            request.checkNotNullAllFields();
            final Ledger ledger = gl.getLedger(currencyCode);
            final IdentifiedEntity<Reservation> iRes = ledger.reserveFunds(request.getTransferRef(),
                    "Client's UUID must be here","CLientCustomerId", debitAccountUuid,
                    request.getCreditAccountUuid(), request.getAmount(), request.getDescription());
            final NewReservationResponse response = new NewReservationResponse();
            response.setUuid(iRes.getEntity().getUuid());
            response.setTransferRef(iRes.getEntity().getTransferRef());
            response.setClientCustomerId(iRes.getEntity().getClientCustomerId());
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
        } catch (AuthenticationException e) {
            LOGGER.error(e.getMessage());
            final LedgerError error = new LedgerError(401, e.getMessage());
            return new ResponseEntity<LedgerError>(error, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            final LedgerError error = new LedgerError(500, e.getMessage());
            return new ResponseEntity<LedgerError>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}