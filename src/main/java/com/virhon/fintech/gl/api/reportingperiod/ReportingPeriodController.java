package com.virhon.fintech.gl.api.reportingperiod;

import com.google.gson.Gson;
import com.virhon.fintech.gl.GsonConverter;
import com.virhon.fintech.gl.api.LedgerError;
import com.virhon.fintech.gl.exception.LedgerException;
import com.virhon.fintech.gl.model.Account;
import com.virhon.fintech.gl.model.AccountAttributes;
import com.virhon.fintech.gl.model.Ledger;
import com.virhon.fintech.gl.repo.mysql.MySQLGeneralLedger;
import com.virhon.fintech.gl.signature.SignatureChecker;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.virhon.fintech.gl.api.APIConfig.*;

@RestController
@RequestMapping("/v1/gl/{currencyCode}/accounts/{accountUuid}/reporting")
public class ReportingPeriodController {
    final static Logger LOGGER = Logger.getLogger(ReportingPeriodController.class);

    @Autowired
    MySQLGeneralLedger gl;

    @Autowired
    SignatureChecker checker;

    private Gson gc = GsonConverter.create();

    @PostMapping
    public ResponseEntity<?> getReportingPeriod(@RequestHeader(value = CLIENT_UUID_HEADER) String hClientUuid,
                                                @RequestHeader(value = SIGNATURE_HEADER) String hSignature,
                                                @RequestHeader(value = DATE_HEADER) String hDate,
                                                @PathVariable String currencyCode,
                                                @PathVariable String accountUuid,
                                                @RequestBody ReportingPeriodRequest request) {
        try {
            final String req = gc.toJson(request);
            checker.validateSignature(hClientUuid, hDate, req, hSignature);
            request.checkNotNullAllFields();
            request.getBeginOn().checkNotNullAllFields();
            request.getFinishOn().checkNotNullAllFields();
            final Ledger ledger = gl.getLedger(currencyCode);
            final Account account = ledger.getExistingByUuid(accountUuid);
            final AccountAttributes attr = account.getAttributes().getEntity();
            final Ledger.ReportingCollection collection = ledger.collectReportingData(account.getAccountId(),
                    request.getBeginOn().asLocalDate(), request.getFinishOn().asLocalDate());
            final PeriodResponse response = new PeriodResponse();
            response.setClientCustomerId(attr.getClientCustomerId());
            response.setAccType(attr.getAccountType().toString());
            response.setAccNumber(attr.getAccountNumber());
            response.setIban(attr.getIban());
            final PeriodResponse.Balance open = new PeriodResponse.Balance();
            final BigDecimal startBalance = collection.getStartBalance();
            open.setBalance(startBalance.abs());
            open.setBalType(attr.getBalanceType(startBalance).toString());
            open.setRepBalance(collection.getStartRepBalance().abs());
            response.setOpen(open);
            final PeriodResponse.Balance closed = new PeriodResponse.Balance();
            final BigDecimal finishBalance = collection.getFinishBalance();
            closed.setBalance(finishBalance.abs());
            closed.setBalType(attr.getBalanceType(finishBalance).toString());
            closed.setRepBalance(collection.getFinishRepBalance().abs());
            response.setClosed(closed);
            final List<PeriodResponse.TransferResponse> tResponses = new ArrayList<>();
            collection.getTransfers().forEach(t -> {
                        final PeriodResponse.TransferResponse trResponse =
                                PeriodResponse.TransferResponse.createFrom(t);
                        trResponse.setTransferType(attr.getBalanceType(t.getAmount()).toString());
                        tResponses.add(trResponse);
                    });
            response.setTransfers(tResponses);
            return new ResponseEntity<>(response, HttpStatus.OK);
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