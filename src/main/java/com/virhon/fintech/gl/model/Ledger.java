package com.virhon.fintech.gl.model;

import com.virhon.fintech.gl.Config;
import com.virhon.fintech.gl.exception.LedgerException;
import com.virhon.fintech.gl.repo.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public class Ledger {
    private AttrRepo attrRepo;
    private CurPageRepo curPageRepo;
    private HistPageRepo histPageRepo;
    private TransferRepo transferRepo;
    private ReservationRepo reservationRepo;

    final static Logger LOGGER = Logger.getLogger(Ledger.class);

    public Ledger(LedgerRepoFactory factory) throws IOException {
        this.attrRepo = factory.getAccountAttributeRepository();
        this.curPageRepo = factory.getCurrentPageRepository();
        this.histPageRepo = factory.getHistoricalPageRepository();
        this.transferRepo = factory.getTransferRepository();
        this.reservationRepo = factory.getReservationRepository();
    }

    IdentifiedEntity<Transfer> transferFunds(final String     transferRef,
                                             final Long       debitId,
                                             final Long       creditId,
                                             final BigDecimal amount,
                                             final BigDecimal localAmount,
                                             final LocalDate  reportedOn,
                                             final String     description) throws LedgerException {
        LOGGER.info(transferRef.concat(" Transferring ".concat(amount.toString())
                .concat(" from ".concat(debitId.toString().concat(" to ").concat(creditId.toString())))));
        // 1. Check sign of amount
        if (amount.signum() == -1) {
            throw LedgerException.invalidTransferAmount(amount);
        }
        // 2. Get accounts
        final Account debit  = Account.getExistingById(this, debitId);
        final Account credit = Account.getExistingById(this, creditId);
        // 3. Check if the transfer is possible
        if (!debit.canBeOperated()) {
            throw LedgerException.accountCantBeOperated(debitId);
        }
        if (!credit.canBeOperated()) {
            throw LedgerException.accountCantBeOperated(creditId);
        }
        // 4. Do transfer
        final ZonedDateTime postedAt = ZonedDateTime.now();
        final Transfer transfer = new Transfer();
        transfer.setTransferRef(transferRef);
        transfer.setAmount(amount);
        transfer.setLocalAmount(localAmount);
        transfer.setReportedOn(reportedOn);
        transfer.setDescription(description);
        transfer.setPostedAt(postedAt);
        final IdentifiedEntity<Transfer> iTransfer = this.transferRepo.insert(transfer);
        final Long transferId = iTransfer.getId();
        debit.debit(transferId, postedAt, reportedOn, amount);
        credit.credit(transferId, postedAt, reportedOn, amount);
        LOGGER.info(" Transferring ".concat(transferRef).concat(" SUCCEED"));
        return iTransfer;
    }

    IdentifiedEntity<Reservation> reserveFunds(final String     transferRef,
                                               final Long       debitId,
                                               final Long       creditId,
                                               final BigDecimal amount,
                                               final String     description) throws LedgerException {
        // 1. Create a reservation
        final Reservation reservation = new Reservation();
        reservation.setTransferRef(transferRef);
        reservation.setDebitId(debitId);
        reservation.setCreditId(creditId);
        reservation.setAmount(amount);
        reservation.setExpireAt(ZonedDateTime.now().plusSeconds(Config.getInstance().getReservigDuration()));
        reservation.setDescription(description);
        final IdentifiedEntity<Reservation> iReservation = this.reservationRepo.insert(reservation);
        // 2. Reserve funds
        final Account debit = Account.getExistingById(this, debitId);
        final Account credit = Account.getExistingById(this, creditId);
        debit.reserveDebit(amount);
        credit.reserveCredit(amount);
        return iReservation;
    }

    IdentifiedEntity<Transfer> postReservation(Long id, BigDecimal localAmount, LocalDate reportedOn) throws LedgerException {
        // 1. Get the reservation
        final IdentifiedEntity<Reservation> reservation = reservationRepo.getById(id);
        // 2. Make new transfer
        final IdentifiedEntity<Transfer> transfer = transferFunds(reservation.getEntity().getTransferRef(),
                                                                    reservation.getEntity().getDebitId(),
                                                                    reservation.getEntity().getCreditId(),
                                                                    reservation.getEntity().getAmount(),
                                                                    localAmount,
                                                                    reportedOn,
                                                                    reservation.getEntity().getDescription());
        // 3. Delete the reservation
        this.reservationRepo.delete(reservation.getId());
        return transfer;
    }

    void cancelReservation(Long id) {
        this.reservationRepo.delete(id);
    }

    void commit() {
        this.attrRepo.commit();
        this.curPageRepo.commit();
        this.histPageRepo.commit();
        this.transferRepo.commit();
        this.reservationRepo.commit();
    }

    public AttrRepo getAttrRepo() {
        return attrRepo;
    }

    public CurPageRepo getCurPageRepo() {
        return curPageRepo;
    }

    public HistPageRepo getHistPageRepo() {
        return histPageRepo;
    }

    public TransferRepo getTransferRepo() {
        return transferRepo;
    }

    public ReservationRepo getReservationRepo() {
        return reservationRepo;
    }
}