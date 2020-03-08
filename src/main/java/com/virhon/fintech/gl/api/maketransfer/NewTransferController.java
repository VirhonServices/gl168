package com.virhon.fintech.gl.api.maketransfer;

import com.google.gson.Gson;
import com.virhon.fintech.gl.GsonConverter;
import com.virhon.fintech.gl.api.LedgerError;
import com.virhon.fintech.gl.exception.AccessDenied;
import com.virhon.fintech.gl.exception.LedgerException;
import com.virhon.fintech.gl.model.Account;
import com.virhon.fintech.gl.model.Ledger;
import com.virhon.fintech.gl.model.Transfer;
import com.virhon.fintech.gl.repo.mysql.MySQLGeneralLedger;
import com.virhon.fintech.gl.security.Authorizer;
import com.virhon.fintech.gl.security.SignatureChecker;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import javax.validation.Valid;

import static com.virhon.fintech.gl.api.APIConfig.*;

@RestController
@RequestMapping("/v1/gl/{currencyCode}/accounts/{debitAccountUuid}/transfers")
public class NewTransferController {
    final static Logger LOGGER = Logger.getLogger(NewTransferController.class);

    @Autowired
    private MySQLGeneralLedger gl;

    @Autowired
    private SignatureChecker checker;

    @Autowired
    private Authorizer authorizer;

    private Gson gc = GsonConverter.create();

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> makeTransfer(@RequestHeader(value = CLIENT_UUID_HEADER) String hClientUuid,
                                          @RequestHeader(value = SIGNATURE_HEADER) String hSignature,
                                          @RequestHeader(value = DATE_HEADER) String hDate,
                                          @PathVariable(required = true) String currencyCode,
                                          @PathVariable(required = true) String debitAccountUuid,
                                          @Valid @RequestBody NewTransferRequestBody request) {
        try {
            authorizer.checkPresense(hClientUuid);
            final String req = gc.toJson(request);
            checker.validateSignature(hClientUuid, hDate, req, hSignature);
            request.checkNotNullAllFields();
            final Ledger ledger = gl.getLedger(currencyCode);
            final Account debit = ledger.getExistingByUuid(debitAccountUuid);
            debit.checkAccess(hClientUuid);
            final Account credit = ledger.getExistingByUuid(request.getCreditAccountUuid());
            final Transfer tr = ledger.transferFunds(request.getTransferRef(), hClientUuid,
                    request.getClientCustomerId(), debit.getAccountId(),
                    credit.getAccountId(), request.getAmount(), request.getRepAmount(),
                    request.getReportedOn().asLocalDate(), request.getDescription());
            final TransferData response = ledger.createTransferResponseBody(tr);
            this.gl.commit();
            LOGGER.info("Transfer ".concat(tr.getTransferUuid()).concat(" has been succeed"));
            final ResponseEntity<TransferData> result = new ResponseEntity<TransferData>(response, HttpStatus.CREATED);
            return result;
        } catch (LedgerException e) {
            LOGGER.error(e.getMessage());
            final LedgerError error = new LedgerError(e.getCode(), e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        } catch (AuthenticationException e) {
            LOGGER.error(e.getMessage());
            final LedgerError error = new LedgerError(401, e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        } catch (AccessDenied e) {
            LOGGER.error(e.getMessage());
            final LedgerError error = new LedgerError(403, e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            final LedgerError error = new LedgerError(500, e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
