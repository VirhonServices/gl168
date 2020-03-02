package com.virhon.fintech.gl.api.postingperiod;

import com.virhon.fintech.gl.api.LedgerError;
import com.virhon.fintech.gl.api.reportingperiod.PeriodResponse;
import com.virhon.fintech.gl.exception.LedgerException;
import com.virhon.fintech.gl.model.Account;
import com.virhon.fintech.gl.model.AccountAttributes;
import com.virhon.fintech.gl.model.Ledger;
import com.virhon.fintech.gl.repo.mysql.MySQLGeneralLedger;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/gl/{currencyCode}/accounts/{accountUuid}/posting")
public class PostingPeriodController {
    final static Logger LOGGER = Logger.getLogger(PostingPeriodController.class);

    @Autowired
    MySQLGeneralLedger gl;

    @PostMapping
    public ResponseEntity<?> getPostingPeriod(@PathVariable String currencyCode,
                                              @PathVariable String accountUuid,
                                              @RequestBody PostingPeriodRequest request) {
        try {
            request.checkNotNullAllFields();
            request.getStartedAt().checkNotNullAllFields();
            request.getFinishedAt().checkNotNullAllFields();
            final Ledger ledger = gl.getLedger(currencyCode);
            final Account account = ledger.getExistingByUuid(accountUuid);
            final AccountAttributes attr = account.getAttributes().getEntity();
            final Ledger.PostingCollection collection = ledger.collectPostingData(account.getAccountId(),
                    request.getStartedAt().asDateTime(), request.getFinishedAt().asDateTime());
            final PeriodResponse response = new PeriodResponse();
            response.setClientCustomerId(attr.getClientCustomerId());
            response.setAccType(attr.getAccountType().toString());
            response.setAccNumber(attr.getAccountNumber());
            response.setIban(attr.getIban());
            final PeriodResponse.Balance open = new PeriodResponse.Balance();
            final BigDecimal startBalance = collection.getStartBalance();
            open.setBalance(startBalance.abs());
            open.setBalType(attr.getBalanceType(startBalance).toString());
            open.setRepBalance(collection.getStartRepBalance().abs());
            response.setOpen(open);
            final PeriodResponse.Balance closed = new PeriodResponse.Balance();
            final BigDecimal finishBalance = collection.getFinishBalance();
            closed.setBalance(finishBalance.abs());
            closed.setBalType(attr.getBalanceType(finishBalance).toString());
            closed.setRepBalance(collection.getFinishRepBalance().abs());
            response.setClosed(closed);
            final List<PeriodResponse.TransferResponse> tResponses = new ArrayList<>();
            collection.getTransfers().forEach(t -> {
                final PeriodResponse.TransferResponse trResponse =
                        PeriodResponse.TransferResponse.createFrom(t);
                trResponse.setTransferType(attr.getBalanceType(t.getAmount()).toString());
                tResponses.add(trResponse);
            });
            response.setTransfers(tResponses);
            return new ResponseEntity<PeriodResponse>(response, HttpStatus.OK);
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