package com.virhon.fintech.gl.api.gettransfer;

import com.virhon.fintech.gl.api.LedgerError;
import com.virhon.fintech.gl.api.maketransfer.TransferData;
import com.virhon.fintech.gl.exception.LedgerException;
import com.virhon.fintech.gl.model.Ledger;
import com.virhon.fintech.gl.model.Page;
import com.virhon.fintech.gl.model.Transfer;
import com.virhon.fintech.gl.repo.TransferPages;
import com.virhon.fintech.gl.repo.mysql.MySQLGeneralLedger;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/v1/gl/{currencyCode}/transfers/{transferUuid}")
public class GettingTransferController {
    final static Logger LOGGER = Logger.getLogger(GettingTransferController.class);

    @Autowired
    MySQLGeneralLedger gl;

    @GetMapping
    public ResponseEntity<?> getTransfer(@PathVariable(required = true) String currencyCode,
                                         @PathVariable(required = true) String transferUuid) {
        try {
            final Ledger ledger = gl.getLedger(currencyCode);
            final TransferPages pages = ledger.getTransferRepo().get(transferUuid);
            if (pages == null) {
                throw LedgerException.transferNotExist(transferUuid);
            }
            final Page debitPage = ledger.getPage(pages.getDebitPageUuid());
            final Optional<Transfer> tr = debitPage.locate(transferUuid);
            if (!tr.isPresent()) {
                throw LedgerException.transferNotExist(transferUuid);
            }
            final Transfer transfer = tr.get();
            final TransferData response = ledger.createTransferResponseBody(transfer);
            this.gl.commit();
            final ResponseEntity<TransferData> result = new ResponseEntity<TransferData>(response, HttpStatus.OK);
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
