package com.virhon.fintech.gl.api.accounts;

import com.virhon.fintech.gl.api.LedgerError;
import com.virhon.fintech.gl.model.*;
import com.virhon.fintech.gl.repo.IdentifiedEntity;
import com.virhon.fintech.gl.repo.StorageConnection;
import com.virhon.fintech.gl.repo.mysql.MySQLStorageConnection;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/gl/accounts")
public class AccountsController {
    final static Logger LOGGER = Logger.getLogger(AccountsController.class);

    @Autowired
    GeneralLedger gl;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> openNewAccount(@RequestBody NewAccountRequestBody request) {
        final String cur = request.getCurrency();
        final Ledger ledger = gl.getLedgers().get(cur);
        try {
            final Account account =
                    ledger.openNew(request.getAccNumber(), request.getIban(), AccountType.valueOf(request.getAccType()));
            final IdentifiedEntity<AccountAttributes> attr = ledger.getAttrRepo().getById(account.getAccountId());
            MySQLStorageConnection.getInstance().commit();
            final NewAccountResponseBody response = new NewAccountResponseBody();
            response.setUuid(attr.getEntity().getAccountUUID());
            response.setAccNumber(attr.getEntity().getAccountNumber());
            response.setAccType(attr.getEntity().getAccountType().toString());
            response.setIban(attr.getEntity().getIban());
            response.setCurrency(cur.toUpperCase());
            response.setOpenedAt(attr.getEntity().getOpenedAt().toString());
            LOGGER.info("Account ".concat(attr.getEntity().getIban().concat(" has been opened")));
            final ResponseEntity<NewAccountResponseBody> res = new ResponseEntity<>(response, HttpStatus.CREATED);
            return res;
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
