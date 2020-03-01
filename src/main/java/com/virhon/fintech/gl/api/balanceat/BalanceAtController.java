package com.virhon.fintech.gl.api.balanceat;

import com.virhon.fintech.gl.Tool;
import com.virhon.fintech.gl.api.LedgerError;
import com.virhon.fintech.gl.exception.LedgerException;
import com.virhon.fintech.gl.model.Account;
import com.virhon.fintech.gl.model.AccountAttributes;
import com.virhon.fintech.gl.repo.mysql.MySQLGeneralLedger;
import com.virhon.fintech.gl.model.Ledger;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/v1/gl/{currencyCode}/accounts/{accountUuid}/posting/balance")
public class BalanceAtController {
    final static Logger LOGGER = Logger.getLogger(BalanceAtController.class);

    @Autowired
    MySQLGeneralLedger gl;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> getBalanceAt(@PathVariable String currencyCode,
                                          @PathVariable String accountUuid,
                                          @RequestBody BalanceAtRequestBody request) {
        try {
            request.checkNotNullAllFields();
            final Ledger ledger = gl.getLedger(currencyCode);
            final ZonedDateTime at = Tool.buildByDefault(request.getYear(), request.getMonth(), request.getDay(),
                    request.getHour(), request.getMinute(), request.getSecond(), request.getNanoOfSecond(),
                    request.getZoneId());
            final Account account = ledger.getExistingByUuid(accountUuid);
            if (account == null) {
                throw LedgerException.invalidAccount(accountUuid);
            }
            final AccountAttributes attr = account.getAttributes().getEntity();
            final BalanceAtResponseBody response = new BalanceAtResponseBody();
            response.setClientUuid(attr.getClientUuid());
            response.setClientCustomerId(attr.getClientCustomerId());
            response.setAccNumber(attr.getAccountNumber());
            response.setAccType(attr.getAccountType().toString());
            response.setAt(at.toString());
            response.setIban(attr.getIban());
            response.setBalance(attr.getBalance().abs());
            response.setBalType(attr.getBalanceType().toString());
            response.setRepBalance(attr.getLocalBalance().abs());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (LedgerException e) {
            LOGGER.error(e.getMessage());
            final LedgerError error = new LedgerError(e.getCode(), e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            final LedgerError error = new LedgerError(500, e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
