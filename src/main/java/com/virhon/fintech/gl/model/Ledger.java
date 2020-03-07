package com.virhon.fintech.gl.model;

import com.virhon.fintech.gl.Config;
import com.virhon.fintech.gl.api.maketransfer.TransferData;
import com.virhon.fintech.gl.exception.LedgerException;
import com.virhon.fintech.gl.repo.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Ledger {
    private AttrRepo attrRepo;
    private CurPageRepo curPageRepo;
    private HistPageRepo histPageRepo;
    private ReservationRepo reservationRepo;
    private TransferRepo transferRepo;

    final static Logger LOGGER = Logger.getLogger(Ledger.class);

    public Ledger(LedgerRepoFactory factory) throws IOException {
        this.attrRepo = factory.getAccountAttributeRepository();
        this.curPageRepo = factory.getCurrentPageRepository();
        this.histPageRepo = factory.getHistoricalPageRepository();
        this.reservationRepo = factory.getReservationRepository();
        this.transferRepo = factory.getTransferRepo();
    }

    public Account getExistingById(Long accountId) throws LedgerException {
        if (this.getAttrRepo().getById(accountId)==null) {
            throw LedgerException.invalidAccount(accountId.toString());
        }
        final Account account = new Account(this);
        account.setAccountId(accountId);
        return account;
    }

    public Account getExistingByUuid(String uuid) throws LedgerException {
        final IdentifiedEntity<AccountAttributes> aa = this.getAttrRepo().getByUuid(uuid);
        if (aa==null) {
            throw LedgerException.invalidAccount(uuid);
        }
        final Account account = new Account(this);
        account.setAccountId(aa.getId());
        return account;
    }

    public Account getExistingByAccountNumber(String accountNumber) throws LedgerException {
        final IdentifiedEntity<AccountAttributes> aa = this.getAttrRepo().getByAccountNumber(accountNumber);
        if (aa==null) {
            throw LedgerException.invalidAccount(accountNumber);
        }
        final Account account = new Account(this);
        account.setAccountId(aa.getId());
        return account;
    }

    public Account getExistingByIban(String iban) throws LedgerException {
        final IdentifiedEntity<AccountAttributes> aa = this.getAttrRepo().getByIban(iban);
        if (aa==null) {
            throw LedgerException.invalidAccount(iban);
        }
        final Account account = new Account(this);
        account.setAccountId(aa.getId());
        return account;
    }

    /**
     *
     * @param clientUuid
     * @param clientCustomerId
     * @param accountNumber
     * @param iban
     * @param accountType
     * @return
     */
    public Account openNew(String          clientUuid,
                           String          clientCustomerId,
                           String          accountNumber,
                           String          iban,
                           AccountType     accountType) {
        final AccountAttributes attributes = AccountAttributes.createNew(clientUuid, clientCustomerId, accountNumber,
                iban, accountType);
        final Page page = Page.create(attributes.getAccountUUID(), BigDecimal.ZERO, BigDecimal.ZERO);
        final Account account = new Account(this);
        final Long accountId = getAttrRepo().insert(attributes);
        account.setAccountId(accountId);
        final IdentifiedEntity<Page> identifiedPage = new IdentifiedEntity<Page>(accountId, page);
        getCurPageRepo().put(identifiedPage);
        return account;
    }

    /**
     *
     * @param transferRef
     * @param debitId
     * @param creditId
     * @param amount
     * @param localAmount
     * @param reportedOn
     * @param description
     * @return
     * @throws LedgerException
     */
    public Transfer transferFunds(final String          transferRef,
                                  final String          clientUuid,
                                  final String          clientCustomerId,
                                  final Long            debitId,
                                  final Long            creditId,
                                  final BigDecimal      amount,
                                  final BigDecimal      localAmount,
                                  final ZonedDateTime   postedAt,
                                  final LocalDate       reportedOn,
                                  final String          description) throws LedgerException {
        LOGGER.info(transferRef.concat(" Transferring ".concat(amount.toString())
                .concat(" from ".concat(debitId.toString().concat(" to ").concat(creditId.toString())))));
        // Check sign of amount
        if (amount.signum() == -1) {
            throw LedgerException.invalidTransferAmount(amount);
        }
        // Get accounts
        final Account debit  = getExistingById(debitId);
        final Account credit = getExistingById(creditId);
        // Check if the transfer is possible
        if (!debit.canBeOperated()) {
            throw LedgerException.accountCantBeOperated(debitId);
        }
        if (!credit.canBeOperated()) {
            throw LedgerException.accountCantBeOperated(creditId);
        }
        // Do transfer
        final Transfer transfer = new Transfer();
        transfer.setTransferRef(transferRef);
        transfer.setTransferUuid(UUID.randomUUID().toString());
        transfer.setClientUuid(clientUuid);
        transfer.setClientCustomerId(clientCustomerId);
        transfer.setAmount(amount);
        transfer.setLocalAmount(localAmount);
        transfer.setReportedOn(reportedOn);
        transfer.setDescription(description);
        transfer.setPostedAt(postedAt);
        transfer.setDebitPageUuid(debit.getCurrentPage().getEntity().getUuid());
        transfer.setCreditPageUuid(credit.getCurrentPage().getEntity().getUuid());
        debit.debit(transfer);
        credit.credit(transfer);
        this.transferRepo.reg(transfer.getTransferUuid(), transfer.getClientUuid(), transfer.getClientCustomerId(),
                transfer.getDebitPageUuid(), transfer.getCreditPageUuid());
        LOGGER.info(" Transferring ".concat(transferRef).concat(" SUCCEED"));
        return transfer;
    }

    /**
     *
     * @param transferRef
     * @param debitId
     * @param creditId
     * @param amount
     * @param localAmount
     * @param reportedOn
     * @param description
     * @return
     * @throws LedgerException
     */
    public Transfer transferFunds(final String          transferRef,
                                  final String          clientUuid,
                                  final String          clientCustomerId,
                                  final Long            debitId,
                                  final Long            creditId,
                                  final BigDecimal      amount,
                                  final BigDecimal      localAmount,
                                  final LocalDate       reportedOn,
                                  final String          description) throws LedgerException {
        return transferFunds(transferRef, clientUuid, clientCustomerId, debitId, creditId, amount,
                localAmount, ZonedDateTime.now(), reportedOn, description);
    }


    /**
     *
     * @param transferRef
     * @param debitUuid
     * @param creditUuid
     * @param amount
     * @param localAmount
     * @param reportedOn
     * @param description
     * @return
     */
    public Transfer transferFunds(final String     transferRef,
                                  final String     clientUuid,
                                  final String     clientCustomerId,
                                  final String     debitUuid,
                                  final String     creditUuid,
                                  final BigDecimal amount,
                                  final BigDecimal localAmount,
                                  final LocalDate  reportedOn,
                                  final String     description) throws LedgerException {
        final Account debit = getExistingByUuid(debitUuid);
        final Account credit = getExistingByUuid(creditUuid);
        return transferFunds(transferRef, clientUuid, clientCustomerId, debit.getAccountId(), credit.getAccountId(),
                amount, localAmount, reportedOn, description);
    }

    /**
     *
     * @param transferRef
     * @param debitUuid
     * @param creditUuid
     * @param amount
     * @param localAmount
     * @param postedAt
     * @param reportedOn
     * @param description
     * @return
     * @throws LedgerException
     */
    public Transfer transferFunds(final String          transferRef,
                                  final String          clientUuid,
                                  final String          clientCustomerId,
                                  final String          debitUuid,
                                  final String          creditUuid,
                                  final BigDecimal      amount,
                                  final BigDecimal      localAmount,
                                  final ZonedDateTime   postedAt,
                                  final LocalDate       reportedOn,
                                  final String          description) throws LedgerException {
        final Account debit = getExistingByUuid(debitUuid);
        final Account credit = getExistingByUuid(creditUuid);
        return transferFunds(transferRef, clientUuid, clientCustomerId, debit.getAccountId(), credit.getAccountId(),
                amount, localAmount, postedAt, reportedOn, description);
    }

    /**
     *
     * @param transferRef
     * @param debitUuid
     * @param creditUuid
     * @param amount
     * @param description
     * @return
     * @throws LedgerException
     */
    public IdentifiedEntity<Reservation> reserveFunds(final String     transferRef,
                                                      final String     clientUuid,
                                                      final String     clientCustomerId,
                                                      final String     debitUuid,
                                                      final String     creditUuid,
                                                      final BigDecimal amount,
                                                      final String     description) throws LedgerException {
        final Account debit = getExistingByUuid(debitUuid);
        final Account credit = getExistingByUuid(creditUuid);
        return reserveFunds(transferRef, clientUuid, clientCustomerId,
                debit.getAccountId(), credit.getAccountId(), amount, description);
    }

    /**
     *
     * @param transferRef
     * @param debitId
     * @param creditId
     * @param amount
     * @param description
     * @return
     * @throws LedgerException
     */
    public IdentifiedEntity<Reservation> reserveFunds(final String     transferRef,
                                                      final String     clientUuid,
                                                      final String     clientCustomerId,
                                                      final Long       debitId,
                                                      final Long       creditId,
                                                      final BigDecimal amount,
                                                      final String     description) throws LedgerException {
        // 1. Create a reservation
        final Reservation reservation = new Reservation();
        reservation.setUuid(UUID.randomUUID().toString());
        reservation.setClientUuid(clientUuid);
        reservation.setClientCustomerId(clientCustomerId);
        reservation.setTransferRef(transferRef);
        reservation.setDebitId(debitId);
        reservation.setCreditId(creditId);
        reservation.setAmount(amount);
        reservation.setExpireAt(ZonedDateTime.now().plusSeconds(Config.getInstance().getReservigDuration()));
        reservation.setDescription(description);
        final IdentifiedEntity<Reservation> iReservation = this.reservationRepo.insert(reservation);
        // 2. Reserve funds
        final Account debit = getExistingById(debitId);
        final Account credit = getExistingById(creditId);
        debit.reserveDebit(amount);
        credit.reserveCredit(amount);
        return iReservation;
    }


    /**
     *
     * @param uuid
     * @param localAmount
     * @param reportedOn
     * @return
     * @throws LedgerException
     */
    public Transfer postReservation(final String       uuid,
                                    final BigDecimal   localAmount,
                                    final LocalDate    reportedOn) throws LedgerException {
        // 1. Get the reservation
        final IdentifiedEntity<Reservation> reservation = reservationRepo.getByUuid(uuid);
        return postReservation(reservation.getId(), localAmount, reportedOn);
    }

    /**
     *
     * @param id
     * @param localAmount
     * @param reportedOn
     * @return
     * @throws LedgerException
     */
    public Transfer postReservation(final Long         id,
                                    final BigDecimal   localAmount,
                                    final LocalDate    reportedOn) throws LedgerException {
        // 1. Get the reservation
        final IdentifiedEntity<Reservation> reservation = reservationRepo.getById(id);
        final String clientUuid = reservation.getEntity().getClientUuid();
        final String clientCustomerId = reservation.getEntity().getClientCustomerId();
        final Long debitId = reservation.getEntity().getDebitId();
        final Long creditId = reservation.getEntity().getCreditId();
        final BigDecimal amount = reservation.getEntity().getAmount();
        // 2. Make new transfer
        final Transfer transfer = transferFunds(reservation.getEntity().getTransferRef(),
                                                                  clientUuid,
                                                                  clientCustomerId,
                                                                  debitId,
                                                                  creditId,
                                                                  amount,
                                                                  localAmount,
                                                                  reportedOn,
                                                                  reservation.getEntity().getDescription());
        // 3. Delete the reservation
        final Account debit = getExistingById(debitId);
        final Account credit = getExistingById(creditId);
        debit.reserveDebit(amount.negate());
        credit.reserveCredit(amount.negate());
        this.reservationRepo.delete(reservation.getId());
        return transfer;
    }

    void cancelReservation(Long id) {
        this.reservationRepo.delete(id);
    }

    public AttrRepo getAttrRepo() {
        return this.attrRepo;
    }

    public CurPageRepo getCurPageRepo() {
        return this.curPageRepo;
    }

    public HistPageRepo getHistPageRepo() {
        return this.histPageRepo;
    }

    public ReservationRepo getReservationRepo() {
        return this.reservationRepo;
    }

    public TransferRepo getTransferRepo() {
        return this.transferRepo;
    }

    public Page getPage(final String uuid) throws LedgerException {
        IdentifiedEntity<Page> page = this.getCurPageRepo().getByUuid(uuid);
        if (page == null) {
            page = this.getHistPageRepo().getByUuid(uuid);
        }
        if (page == null) {
            throw LedgerException.pageNotExist(uuid);
        }
        return page.getEntity();
    }

    /**
     *
     * @param tr
     * @return
     * @throws LedgerException
     */
    public TransferData createTransferResponseBody(Transfer tr) throws LedgerException {
        final Page debitPage = getPage(tr.getDebitPageUuid());
        final Account debit = this.getExistingByUuid(debitPage.getAccountUuid());
        final AccountAttributes debAttr = debit.getAttributes().getEntity();
        final Page creditPage = getPage(tr.getCreditPageUuid());
        final Account credit = this.getExistingByUuid(creditPage.getAccountUuid());
        final AccountAttributes creAttr = credit.getAttributes().getEntity();
        final TransferData response = new TransferData();
        response.setUuid(tr.getTransferUuid());
        response.setTransferRef(tr.getTransferRef());
        response.setClientCustomerId(tr.getClientCustomerId());
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
        return response;
    }

    private List<Page> collectReportingPages(Long accountId, LocalDate beginOn, LocalDate finishOn) {
        final Page current = this.curPageRepo.getById(accountId).getEntity();
        final List<IdentifiedEntity<Page>> history = this.histPageRepo.getHistoryPeriod(accountId, beginOn, finishOn);
        final List<Page> result = new ArrayList<>();
        history.forEach(p -> result.add(p.getEntity()));
        if (current.getRepStartedOn().compareTo(beginOn) <= 0 || current.getRepStartedOn().compareTo(finishOn) <= 0) {
            result.add(current);
        }
        return result;
    }

    private List<Page> collectPostingPages(Long accountId, ZonedDateTime beginAt, ZonedDateTime finishAt) {
        final Page current = this.curPageRepo.getById(accountId).getEntity();
        final List<IdentifiedEntity<Page>> history = this.histPageRepo.getHistoryPostingPeriod(accountId, beginAt, finishAt);
        final List<Page> result = new ArrayList<>();
        history.forEach(p -> result.add(p.getEntity()));
        if (current.getStartedAt().compareTo(beginAt) <= 0 || current.getStartedAt().compareTo(finishAt) <= 0) {
            result.add(current);
        }
        return result;
    }

    public static class Balances {
        private BigDecimal startBalance;
        private BigDecimal startRepBalance;
        private BigDecimal finishBalance;
        private BigDecimal finishRepBalance;
        private List<Transfer> transfers = new ArrayList<>();

        public BigDecimal getStartRepBalance() {
            return startRepBalance;
        }

        public void setStartRepBalance(BigDecimal startRepBalance) {
            this.startRepBalance = startRepBalance;
        }

        public BigDecimal getFinishRepBalance() {
            return finishRepBalance;
        }

        public void setFinishRepBalance(BigDecimal finishRepBalance) {
            this.finishRepBalance = finishRepBalance;
        }

        public BigDecimal getStartBalance() {
            return startBalance;
        }

        public void setStartBalance(BigDecimal startBalance) {
            this.startBalance = startBalance;
        }

        public BigDecimal getFinishBalance() {
            return finishBalance;
        }

        public void setFinishBalance(BigDecimal finishBalance) {
            this.finishBalance = finishBalance;
        }

        public List<Transfer> getTransfers() {
            return transfers;
        }

        public void setTransfers(List<Transfer> transfers) {
            this.transfers = transfers;
        }
    }

    public static class ReportingCollection extends Balances {
        private LocalDate startedOn;
        private LocalDate finishedOn;

        public LocalDate getStartedOn() {
            return startedOn;
        }

        public void setStartedOn(LocalDate startedOn) {
            this.startedOn = startedOn;
        }

        public LocalDate getFinishedOn() {
            return finishedOn;
        }

        public void setFinishedOn(LocalDate finishedOn) {
            this.finishedOn = finishedOn;
        }
    }

    public static class PostingCollection extends Balances {
        private ZonedDateTime startedAt;
        private ZonedDateTime finishedAt;

        public ZonedDateTime getStartedAt() {
            return startedAt;
        }

        public void setStartedAt(ZonedDateTime startedAt) {
            this.startedAt = startedAt;
        }

        public ZonedDateTime getFinishedAt() {
            return finishedAt;
        }

        public void setFinishedAt(ZonedDateTime finishedAt) {
            this.finishedAt = finishedAt;
        }
    }

    /**
     *
     * @param accountId
     * @param beginOn
     * @param finishOn
     * @return
     */
    public ReportingCollection collectReportingData(Long accountId,
                                                    LocalDate beginOn,
                                                    LocalDate finishOn) {
        final ReportingCollection collection = new ReportingCollection();
        collection.setStartedOn(beginOn);
        collection.setFinishedOn(finishOn);
        collection.setStartBalance(BigDecimal.ZERO);
        collection.setStartRepBalance(BigDecimal.ZERO);
        collection.setFinishBalance(BigDecimal.ZERO);
        collection.setFinishRepBalance(BigDecimal.ZERO);
        collection.setTransfers(new ArrayList<>());
        final List<Page> pages = collectReportingPages(accountId, beginOn, finishOn);
        if (!pages.isEmpty()) {
            final List<Transfer> previousTransfers = pages.stream()
                    .map(p -> p.getTransfers())
                    .flatMap(posts -> posts.stream())
                    .filter(post -> post.getReportedOn().compareTo(beginOn) < 0)
                    .sorted((p1, p2) -> p1.getPostedAt().compareTo(p2.getPostedAt()))
                    .collect(Collectors.toList());
            final BigDecimal preTurnover = previousTransfers.stream().map(p -> p.getAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            final BigDecimal preRepTurnover = previousTransfers.stream().map(p -> p.getLocalAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            final BigDecimal startBalance = pages.get(0).getStartBalance().add(preTurnover);
            final BigDecimal startRepBalance = pages.get(0).getStartRepBalance().add(preRepTurnover);
            final List<Transfer> periodTransfers = pages.stream()
                    .map(p -> p.getTransfers())
                    .flatMap(posts -> posts.stream())
                    .filter(post -> post.getReportedOn().compareTo(beginOn) >= 0 &&
                            post.getReportedOn().compareTo(finishOn) <= 0)
                    .sorted((p1, p2) -> p1.getPostedAt().compareTo(p2.getPostedAt()))
                    .collect(Collectors.toList());
            final BigDecimal periodTurnover = periodTransfers.stream().map(p -> p.getAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            final BigDecimal periodRepTurnover = periodTransfers.stream().map(p -> p.getLocalAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            final BigDecimal finishBalance = startBalance.add(periodTurnover);
            final BigDecimal finishRepBalance = startRepBalance.add(periodRepTurnover);
            collection.setStartedOn(beginOn);
            collection.setFinishedOn(finishOn);
            collection.setStartBalance(startBalance);
            collection.setStartRepBalance(startRepBalance);
            collection.setFinishBalance(finishBalance);
            collection.setFinishRepBalance(finishRepBalance);
            collection.setTransfers(periodTransfers);
        }
        return collection;
    }

    public PostingCollection collectPostingData(Long accountId,
                                                ZonedDateTime startAt,
                                                ZonedDateTime finishAt) {
        final PostingCollection collection = new PostingCollection();
        collection.setStartedAt(startAt);
        collection.setFinishedAt(finishAt);
        collection.setStartBalance(BigDecimal.ZERO);
        collection.setStartRepBalance(BigDecimal.ZERO);
        collection.setFinishBalance(BigDecimal.ZERO);
        collection.setFinishRepBalance(BigDecimal.ZERO);
        collection.setTransfers(new ArrayList<>());
        final List<Page> pages = collectPostingPages(accountId, startAt, finishAt);
        if (!pages.isEmpty()) {
            final List<Transfer> previousTransfers = pages.stream()
                    .map(p -> p.getTransfers())
                    .flatMap(posts -> posts.stream())
                    .filter(post -> post.getPostedAt().compareTo(startAt) < 0)
                    .sorted((p1, p2) -> p1.getPostedAt().compareTo(p2.getPostedAt()))
                    .collect(Collectors.toList());
            final BigDecimal preTurnover = previousTransfers.stream().map(p -> p.getAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            final BigDecimal preRepTurnover = previousTransfers.stream().map(p -> p.getLocalAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            final BigDecimal startBalance = pages.get(0).getStartBalance().add(preTurnover);
            final BigDecimal startRepBalance = pages.get(0).getStartRepBalance().add(preRepTurnover);
            final List<Transfer> periodTransfers = pages.stream()
                    .map(p -> p.getTransfers())
                    .flatMap(posts -> posts.stream())
                    .filter(post -> post.getPostedAt().compareTo(startAt) >= 0 &&
                            post.getPostedAt().compareTo(finishAt) <= 0)
                    .sorted((p1, p2) -> p1.getPostedAt().compareTo(p2.getPostedAt()))
                    .collect(Collectors.toList());
            final BigDecimal periodTurnover = periodTransfers.stream().map(p -> p.getAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            final BigDecimal periodRepTurnover = periodTransfers.stream().map(p -> p.getLocalAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            final BigDecimal finishBalance = startBalance.add(periodTurnover);
            final BigDecimal finishRepBalance = startRepBalance.add(periodRepTurnover);
            collection.setStartedAt(startAt);
            collection.setFinishedAt(finishAt);
            collection.setStartBalance(startBalance);
            collection.setStartRepBalance(startRepBalance);
            collection.setFinishBalance(finishBalance);
            collection.setFinishRepBalance(finishRepBalance);
            collection.setTransfers(periodTransfers);
        }
        return collection;
    }
}