package com.virhon.fintech.gl.api.maketransfer;

import com.virhon.fintech.gl.api.LedgerError;
import com.virhon.fintech.gl.api.accounts.AccountsController;
import com.virhon.fintech.gl.exception.LedgerException;
import com.virhon.fintech.gl.model.*;
import com.virhon.fintech.gl.repo.IdentifiedEntity;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/gl/{currencyCode}/accounts/{debitAccountUuid}/transfers")
public class NewTransferController {
    final static Logger LOGGER = Logger.getLogger(AccountsController.class);

    @Autowired
    GeneralLedger gl;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> makeTransfer(@PathVariable String currencyCode,
                                          @PathVariable String debitAccountUuid,
                                          @RequestBody NewTransferRequestBody request) {
        try {
            final Ledger ledger = gl.getLedger(currencyCode);
            final Account debit = ledger.getExistingByUuid(debitAccountUuid);
            final AccountAttributes debAttr = debit.getAttributes().getEntity();
            final Account credit = ledger.getExistingByUuid(request.getCreditAccountUuid());
            final AccountAttributes creAttr = credit.getAttributes().getEntity();
            final IdentifiedEntity<Transfer> transfer = ledger.transferFunds(request.getTransferRef(),
                    debit.getAccountId(), credit.getAccountId(), request.getAmount(), request.getRepAmount(),
                    request.getReportedOn().toLocalDate(), request.getDescription());
            final NewTransferResponseBody response = new NewTransferResponseBody();
            final Transfer tr = transfer.getEntity();
            response.setUuid(tr.getUuid());
            response.setTransferRef(tr.getTransferRef());
            response.setPostedAt(tr.getPostedAt().toString());
            response.setReportedOn(tr.getReportedOn().toString());
            response.setAmount(tr.getAmount());
            response.setRepAmount(tr.getLocalAmount());
            response.setDescription(tr.getDescription());
            final NewTransferResponseBody.Account deb = new NewTransferResponseBody.Account();
            deb.setAccUuid(debAttr.getAccountUUID());
            deb.setAccNumber(debAttr.getAccountNumber());
            deb.setIban(debAttr.getIban());
            deb.setAccType(debAttr.getAccountType().toString());
            response.setDebit(deb);
            final NewTransferResponseBody.Account cre = new NewTransferResponseBody.Account();
            cre.setAccUuid(creAttr.getAccountUUID());
            cre.setAccNumber(creAttr.getAccountNumber());
            cre.setIban(creAttr.getIban());
            cre.setAccType(creAttr.getAccountType().toString());
            response.setCredit(cre);
            LOGGER.info("Transfer ".concat(tr.getUuid()).concat(" has been succeed"));
            return new ResponseEntity<>(response, HttpStatus.CREATED);
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
