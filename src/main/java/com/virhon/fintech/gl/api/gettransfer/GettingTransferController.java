package com.virhon.fintech.gl.api.gettransfer;

import com.virhon.fintech.gl.api.LedgerError;
import com.virhon.fintech.gl.api.maketransfer.TransferData;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/gl/{currencyCode}/transfers/{transferUuid}")
public class GettingTransferController {
    final static Logger LOGGER = Logger.getLogger(GettingTransferController.class);

    @Autowired
    MySQLGeneralLedger gl;

    @GetMapping
    public ResponseEntity<?> getTransfer(@PathVariable String currencyCode,
                                         @PathVariable String transferUuid) {
        try {
            final Ledger ledger = gl.getLedger(currencyCode);
            final IdentifiedEntity<Transfer> transfer = ledger.getTransferRepo().getByUuid(transferUuid);
            if (transfer == null) {
                throw LedgerException.transferNotExist(transferUuid);
            }
            final Transfer tr = transfer.getEntity();
            final Account debit = ledger.getExistingByUuid(tr.getDebitUuid());
            final Account credit = ledger.getExistingByUuid(tr.getCreditUuid());
            final AccountAttributes debAttr = debit.getAttributes().getEntity();
            final AccountAttributes creAttr = credit.getAttributes().getEntity();
            final TransferData response = new TransferData();
            response.setUuid(tr.getTransferUuid());
            response.setTransferRef(tr.getTransferRef());
            response.setPostedAt(tr.getPostedAt().toString());
            response.setReportedOn(tr.getReportedOn().toString());
            response.setAmount(tr.getAmount());
            response.setRepAmount(tr.getLocalAmount());
            response.setDescription(tr.getDescription());
            final TransferData.Account deb = new TransferData.Account();
            deb.setAccUuid(debAttr.getAccountUUID());
            deb.setAccNumber(debAttr.getAccountNumber());
            deb.setIban(debAttr.getIban());
            deb.setAccType(debAttr.getAccountType().toString());
            response.setDebit(deb);
            final TransferData.Account cre = new TransferData.Account();
            cre.setAccUuid(creAttr.getAccountUUID());
            cre.setAccNumber(creAttr.getAccountNumber());
            cre.setIban(creAttr.getIban());
            cre.setAccType(creAttr.getAccountType().toString());
            response.setCredit(cre);
            this.gl.commit();
            return new ResponseEntity<>(response, HttpStatus.CREATED);
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
