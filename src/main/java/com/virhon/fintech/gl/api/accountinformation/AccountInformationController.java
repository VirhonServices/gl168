package com.virhon.fintech.gl.api.accountinformation;

import com.virhon.fintech.gl.api.LedgerError;
import com.virhon.fintech.gl.exception.LedgerException;
import com.virhon.fintech.gl.model.AccountAttributes;
import com.virhon.fintech.gl.model.AccountType;
import com.virhon.fintech.gl.repo.mysql.MySQLGeneralLedger;
import com.virhon.fintech.gl.model.Ledger;
import com.virhon.fintech.gl.repo.IdentifiedEntity;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.ZonedDateTime;

@RestController
@RequestMapping("/v1/gl/{currencyCode}/accounts/{accountUuid}")
public class AccountInformationController {
    final static Logger LOGGER = Logger.getLogger(AccountInformationController.class);

    @Autowired
    MySQLGeneralLedger gl;

    @GetMapping
    public ResponseEntity<?> get(@PathVariable String currencyCode, @PathVariable String accountUuid) {
        try {
            final String cur = currencyCode.toUpperCase();
            final Ledger ledger = gl.getLedger(cur);
            final IdentifiedEntity<AccountAttributes> attr = ledger.getAttrRepo().getByUuid(accountUuid);
            if (attr != null) {
                final AccountInformationResponseBody response = new AccountInformationResponseBody();
                response.setUuid(accountUuid);
                response.setClientCustomerId(attr.getEntity().getClientCustomerId());
                response.setAccType(attr.getEntity().getAccountType().toString());
                response.setAccNumber(attr.getEntity().getAccountNumber());
                response.setIban(attr.getEntity().getIban());
                response.setCurrency(cur);
                final BigDecimal balance = attr.getEntity().getBalance();
                response.setBalance(balance);
                response.setRepBalance(attr.getEntity().getLocalBalance());
                response.setOpenedAt(attr.getEntity().getOpenedAt().toString());
                final ZonedDateTime closedAt = attr.getEntity().getClosedAt();
                if (closedAt != null) {
                    response.setClosedAt(closedAt.toString());
                }
                if (attr.getEntity().getAccountType() == AccountType.PASSIVE) {
                    final BigDecimal available =
                            attr.getEntity().getBalance().subtract(attr.getEntity().getReservedDebit());
                    response.setAvailable(available);
                }
                if (balance.signum() == -1) {
                    response.setBalType("CREDIT");
                } else  if (balance.signum() == 1) {
                    response.setBalType("DEBIT");
                } else {
                    if (attr.getEntity().getAccountType() == AccountType.ACTIVE) {
                        response.setBalType("DEBIT");
                    } else {
                        response.setBalType("CREDIT");
                    }
                }
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                throw LedgerException.invalidAccount(accountUuid);
            }
        } catch (LedgerException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<LedgerError>(new LedgerError(e.getCode(), e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(new LedgerError(500,"Something went wrong"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
