package com.virhon.fintech.gl.api.maketransfer;

import com.virhon.fintech.gl.api.LedgerError;
import com.virhon.fintech.gl.exception.LedgerException;
import com.virhon.fintech.gl.model.Account;
import com.virhon.fintech.gl.model.AccountAttributes;
import com.virhon.fintech.gl.model.Ledger;
import com.virhon.fintech.gl.model.Transfer;
import com.virhon.fintech.gl.repo.IdentifiedEntity;
import com.virhon.fintech.gl.repo.mysql.MySQLGeneralLedger;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/gl/{currencyCode}/accounts/{debitAccountUuid}/transfers")
public class NewTransferController {
    final static Logger LOGGER = Logger.getLogger(NewTransferController.class);

    @Autowired
    MySQLGeneralLedger gl;

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<?> makeTransfer(@PathVariable(required = true) String currencyCode,
                                          @PathVariable(required = true) String debitAccountUuid,
                                          @Valid @RequestBody NewTransferRequestBody request) {
        try {
            final Ledger ledger = gl.getLedger(currencyCode);
            final Account debit = ledger.getExistingByUuid(debitAccountUuid);
            final AccountAttributes debAttr = debit.getAttributes().getEntity();
            final Account credit = ledger.getExistingByUuid(request.getCreditAccountUuid());
            final AccountAttributes creAttr = credit.getAttributes().getEntity();
            final IdentifiedEntity<Transfer> transfer = ledger.transferFunds(request.getTransferRef(),
                    debit.getAccountId(), credit.getAccountId(), request.getAmount(), request.getRepAmount(),
                    request.getReportedOn().toLocalDate(), request.getDescription());
            final TransferResponseBody response = new TransferResponseBody();
            final Transfer tr = transfer.getEntity();
            response.setUuid(tr.getTransferUuid());
            response.setTransferRef(tr.getTransferRef());
            response.setPostedAt(tr.getPostedAt().toString());
            response.setReportedOn(tr.getReportedOn().toString());
            response.setAmount(tr.getAmount());
            response.setRepAmount(tr.getLocalAmount());
            response.setDescription(tr.getDescription());
            final TransferResponseBody.Account deb = new TransferResponseBody.Account();
            deb.setAccUuid(debAttr.getAccountUUID());
            deb.setAccNumber(debAttr.getAccountNumber());
            deb.setIban(debAttr.getIban());
            deb.setAccType(debAttr.getAccountType().toString());
            response.setDebit(deb);
            final TransferResponseBody.Account cre = new TransferResponseBody.Account();
            cre.setAccUuid(creAttr.getAccountUUID());
            cre.setAccNumber(creAttr.getAccountNumber());
            cre.setIban(creAttr.getIban());
            cre.setAccType(creAttr.getAccountType().toString());
            response.setCredit(cre);
            this.gl.commit();
            LOGGER.info("Transfer ".concat(tr.getTransferUuid()).concat(" has been succeed"));
            final ResponseEntity<TransferResponseBody> result =
                    new ResponseEntity<TransferResponseBody>(response, HttpStatus.OK);
            return result;
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
