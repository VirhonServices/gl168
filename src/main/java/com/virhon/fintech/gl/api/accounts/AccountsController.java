package com.virhon.fintech.gl.api.accounts;

import com.virhon.fintech.gl.api.LedgerError;
import com.virhon.fintech.gl.exception.LedgerException;
import com.virhon.fintech.gl.model.*;
import com.virhon.fintech.gl.repo.IdentifiedEntity;
import com.virhon.fintech.gl.repo.mysql.MySQLGeneralLedger;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/gl/{currencyCode}/accounts")
public class AccountsController {
    final static Logger LOGGER = Logger.getLogger(AccountsController.class);

    @Autowired
    MySQLGeneralLedger gl;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> openNewAccount(@PathVariable String currencyCode,
                                            @RequestBody NewAccountRequestBody request) {
        try {
            request.checkNotNullAllFields();
            final Ledger ledger = gl.getLedger(currencyCode);
            // TODO: 02.03.20 change uuids
            final Account account =
                    ledger.openNew(request.getAccNumber(),"CLIENT_UUID", "clientCustomerId",
                            request.getIban(), AccountType.valueOf(request.getAccType()));
            final IdentifiedEntity<AccountAttributes> attr = ledger.getAttrRepo().getById(account.getAccountId());
            this.gl.commit();
            final NewAccountResponseBody response = new NewAccountResponseBody();
            response.setUuid(attr.getEntity().getAccountUUID());
            response.setClientCustomerId(attr.getEntity().getClientCustomerId());
            response.setAccNumber(attr.getEntity().getAccountNumber());
            response.setAccType(attr.getEntity().getAccountType().toString());
            response.setIban(attr.getEntity().getIban());
            response.setCurrency(currencyCode.toUpperCase());
            response.setOpenedAt(attr.getEntity().getOpenedAt().toString());
            LOGGER.info("Account ".concat(attr.getEntity().getIban().concat(" has been opened")));
            final ResponseEntity<NewAccountResponseBody> res = new ResponseEntity<>(response, HttpStatus.CREATED);
            return res;
        } catch (LedgerException e) {
            LOGGER.error("Account ".concat(request.getAccNumber().concat(" hasn't been opened")));
            return new ResponseEntity<LedgerError>(new LedgerError(e.getCode(), e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Account ".concat(request.getAccNumber().concat(" hasn't been opened")));
            return new ResponseEntity<>(new LedgerError(900,"Invalid account type "
                    .concat(request.getAccType())), HttpStatus.BAD_REQUEST);
        } catch (NullPointerException e) {
            LOGGER.error("Account ".concat(request.getAccNumber().concat(" hasn't been opened")));
            return new ResponseEntity<>(new LedgerError(910, e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            LOGGER.error("Account ".concat(request.getAccNumber().concat(" hasn't been opened")));
            return new ResponseEntity<>(new LedgerError(500,"Something went wrong"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}