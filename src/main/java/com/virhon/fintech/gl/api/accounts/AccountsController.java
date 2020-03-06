package com.virhon.fintech.gl.api.accounts;

import com.google.gson.Gson;
import com.virhon.fintech.gl.GsonConverter;
import com.virhon.fintech.gl.api.LedgerError;
import com.virhon.fintech.gl.exception.LedgerException;
import com.virhon.fintech.gl.model.*;
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
@RequestMapping("/v1/gl/{currencyCode}/accounts")
public class AccountsController {
    final static Logger LOGGER = Logger.getLogger(AccountsController.class);

    @Autowired
    MySQLGeneralLedger gl;

    @Autowired
    SignatureChecker checker;

    private Gson gc = GsonConverter.create();

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> openNewAccount(@RequestHeader(value = CLIENT_UUID_HEADER) String hClientUuid,
                                            @RequestHeader(value = SIGNATURE_HEADER) String hSignature,
                                            @RequestHeader(value = DATE_HEADER) String hDate,
                                            @PathVariable String currencyCode,
                                            @RequestBody NewAccountRequestBody request) {
        try {
            final String req = gc.toJson(request);
            checker.validateSignature(hClientUuid, hDate, req, hSignature);
            request.checkNotNullAllFields();
            final Ledger ledger = gl.getLedger(currencyCode);
            // TODO: 02.03.20 change uuids
            final Account account =
                    ledger.openNew("CLIENT_UUID","clientCustomerId", request.getAccNumber(),
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
        } catch (AuthenticationException e) {
            LOGGER.error(e.getMessage());
            final LedgerError error = new LedgerError(401, e.getMessage());
            return new ResponseEntity<LedgerError>(error, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            LOGGER.error("Account ".concat(request.getAccNumber().concat(" hasn't been opened")));
            return new ResponseEntity<>(new LedgerError(500,"Something went wrong"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}