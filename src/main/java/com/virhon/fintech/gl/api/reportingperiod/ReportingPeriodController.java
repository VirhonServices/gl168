package com.virhon.fintech.gl.api.reportingperiod;

import com.virhon.fintech.gl.api.LedgerError;
import com.virhon.fintech.gl.exception.LedgerException;
import com.virhon.fintech.gl.model.Account;
import com.virhon.fintech.gl.model.AccountAttributes;
import com.virhon.fintech.gl.model.Ledger;
import com.virhon.fintech.gl.repo.mysql.MySQLGeneralLedger;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/v1/gl/{currencyCode}/accounts/{accountUuid}/reporting/balance")
public class ReportingPeriodController {
    final static Logger LOGGER = Logger.getLogger(ReportingPeriodController.class);

    @Autowired
    MySQLGeneralLedger gl;

    @PostMapping
    public ResponseEntity<?> get(@PathVariable String currencyCode,
                                 @PathVariable String accountUuid,
                                 @RequestBody ReportingPeriodRequest request) {
        try {
            final Ledger ledger = gl.getLedger(currencyCode);
            final Account account = ledger.getExistingByUuid(accountUuid);
            final AccountAttributes attr = account.getAttributes().getEntity();
            final Ledger.ReportingCollection collection = ledger.collectReportingData(account.getAccountId(),
                    request.getBeginOn().toLocalDate(), request.getFinishOn().toLocalDate());
            final ReportingPeriodResponse response = new ReportingPeriodResponse();
            response.setAccType(attr.getAccountType().toString());
            response.setAccNumber(attr.getAccountNumber());
            response.setIban(attr.getIban());

            final ReportingPeriodResponse.Balance open = new ReportingPeriodResponse.Balance();
            final BigDecimal startBalance = collection.getStartBalance();
            open.setBalance(startBalance.abs());
            open.setBalType(attr.getBalanceType(startBalance).toString());
            open.setRepBalance(collection.getStartRepBalance().abs());
            response.setOpen(open);

            final ReportingPeriodResponse.Balance closed = new ReportingPeriodResponse.Balance();
            final BigDecimal finishBalance = collection.getFinishBalance();
            closed.setBalance(finishBalance.abs());
            closed.setBalType(attr.getBalanceType(finishBalance).toString());
            closed.setRepBalance(collection.getFinishRepBalance().abs());
            response.setClosed(closed);

            return new ResponseEntity<>(response, HttpStatus.OK);
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
