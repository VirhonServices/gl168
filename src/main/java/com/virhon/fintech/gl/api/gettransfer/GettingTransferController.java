package com.virhon.fintech.gl.api.gettransfer;

import com.virhon.fintech.gl.api.LedgerError;
import com.virhon.fintech.gl.api.maketransfer.TransferData;
import com.virhon.fintech.gl.exception.AccessDenied;
import com.virhon.fintech.gl.exception.LedgerException;
import com.virhon.fintech.gl.model.Ledger;
import com.virhon.fintech.gl.model.Page;
import com.virhon.fintech.gl.model.Transfer;
import com.virhon.fintech.gl.repo.TransferPages;
import com.virhon.fintech.gl.repo.mysql.MySQLGeneralLedger;
import com.virhon.fintech.gl.security.Authorizer;
import com.virhon.fintech.gl.security.SignatureChecker;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.util.Optional;

import static com.virhon.fintech.gl.api.APIConfig.*;

@RestController
@RequestMapping("/v1/gl/{currencyCode}/transfers/{transferUuid}")
public class GettingTransferController {
    final static Logger LOGGER = Logger.getLogger(GettingTransferController.class);

    @Autowired
    MySQLGeneralLedger gl;

    @Autowired
    SignatureChecker checker;

    @Autowired
    private Authorizer authorizer;

    @GetMapping
    public ResponseEntity<?> getTransfer(@RequestHeader(value = CLIENT_UUID_HEADER) String hClientUuid,
                                         @RequestHeader(value = SIGNATURE_HEADER) String hSignature,
                                         @RequestHeader(value = DATE_HEADER) String hDate,
                                         @PathVariable(required = true) String currencyCode,
                                         @PathVariable(required = true) String transferUuid) {
        try {
            authorizer.checkPresense(hClientUuid);
            checker.validateSignature(hClientUuid, hDate, "", hSignature);
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
            transfer.checkAccess(hClientUuid);
            final TransferData response = ledger.createTransferResponseBody(transfer);
            this.gl.commit();
            final ResponseEntity<TransferData> result = new ResponseEntity<TransferData>(response, HttpStatus.OK);
            return result;
        } catch (LedgerException e) {
            LOGGER.error(e.getMessage());
            final LedgerError error = new LedgerError(e.getCode(), e.getMessage());
            return new ResponseEntity<LedgerError>(error, HttpStatus.BAD_REQUEST);
        } catch (AuthenticationException e) {
            LOGGER.error(e.getMessage());
            final LedgerError error = new LedgerError(401, e.getMessage());
            return new ResponseEntity<LedgerError>(error, HttpStatus.UNAUTHORIZED);
        } catch (AccessDenied e) {
            LOGGER.error(e.getMessage());
            final LedgerError error = new LedgerError(403, e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            final LedgerError error = new LedgerError(500, e.getMessage());
            return new ResponseEntity<LedgerError>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
