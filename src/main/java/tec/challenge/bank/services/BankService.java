package tec.challenge.bank.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.annotation.PostConstruct;
import tec.challenge.bank.controllers.dtos.CreateCurrentAccountDto;
import tec.challenge.bank.controllers.dtos.CreateSavingAccountDto;
import tec.challenge.bank.models.Bank;
import tec.challenge.bank.models.CurrentAccount;
import tec.challenge.bank.models.SavingAccount;
import tec.challenge.bank.models.TransactionBank;
import tec.challenge.bank.repository.BankRepository;
import tec.challenge.bank.repository.CurrentAccountRepository;
import tec.challenge.bank.repository.SavingAccountRepository;
import tec.challenge.bank.repository.TransactionBankRepository;

@Service
public class BankService implements IBankService {

  @Autowired
  CurrentAccountRepository currentAccountRepository;
  @Autowired
  SavingAccountRepository savingAccountRepository;
  @Autowired
  BankRepository bankRepository;
  @Autowired
  TransactionBankRepository transactionBankRepository;

  private Optional<Bank> bank;

  @PostConstruct
  private void init() {
    bank = bankRepository.findById(1L);

    if (bank.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bank not found");
    }
  }

  @Override
  public void createAccount(Record dto) {
    if (isWhatTypeAccount(dto)) {
      createCurrentAccount((CreateCurrentAccountDto) dto);
    } else {
      createSavingAccount((CreateSavingAccountDto) dto);
    }
  }

  @Override
  public Optional<CurrentAccount> consultCurrentAccount(Long id) {
    return currentAccountRepository.findById(id);
  }

  @Override
  public Optional<SavingAccount> consultSavingAccount(Long id) {
    return savingAccountRepository.findById(id);
  }

  @Override
  public List<CurrentAccount> getAllCurrentAccounts() {
    return currentAccountRepository.findByBank(bank.get());
  }

  @Override
  public List<SavingAccount> getAllSavingAccounts() {
    return savingAccountRepository.findByBank(bank.get());
  }

  @Override
  @Transactional
  public void editCurrentAccount(Long id, CreateCurrentAccountDto currentAccount) {

    CurrentAccount account = currentAccountRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

    account.setNameClient(currentAccount.nameClient());
    // account.setSaldo(currentAccount.saldo());
    account.setCpf(currentAccount.cpf());

    currentAccountRepository.save(account);
  }

  @Override
  @Transactional
  public void editSavingAccount(Long id, CreateSavingAccountDto savingAccount) {

    SavingAccount account = savingAccountRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

    account.setNameClient(savingAccount.nameClient());
    // account.setSaldo(savingAccount.saldo());
    account.setCpf(savingAccount.cpf());

    savingAccountRepository.save(account);
  }

  @Override
  @Transactional
  public void deleteCurrentAccount(Long id) {
    CurrentAccount account = currentAccountRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
    currentAccountRepository.delete(account);
  }

  @Override
  @Transactional
  public void deleteSavingAccount(Long id) {
    SavingAccount account = savingAccountRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
    savingAccountRepository.delete(account);
  }

  @Override
  @Transactional
  public void depositAtCurrentAccount(Long id, Float balance) {

    CurrentAccount account = currentAccountRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
    Float currentBalance = account.getSaldo();
    account.setSaldo(currentBalance + balance);
    currentAccountRepository.save(account);
  }

  @Override
  public void depositAtSavingAccount(Long id, Float balance) {
    SavingAccount account = savingAccountRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
    Float savingBalance = account.getSaldo();
    account.setSaldo(savingBalance + balance);
    savingAccountRepository.save(account);
  }

  @Override
  public void withdrawAtCurrentAccount(Long id, Float balance) {
    CurrentAccount account = currentAccountRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
    Float currentBalance = account.getSaldo();
    if (!suficientBalance(currentBalance, balance)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente para realizar esta operação.");
    }

    account.setSaldo(currentBalance - balance);
    currentAccountRepository.save(account);
  }

  @Override
  public void withdrawAtSavingAccount(Long id, Float balance) {
    SavingAccount account = savingAccountRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
    Float savingBalance = account.getSaldo();
    if (!suficientBalance(savingBalance, balance)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente para realizar esta operação.");
    }

    account.setSaldo(savingBalance - balance);
    savingAccountRepository.save(account);
  }

  @Override
  public void transfer(Long sender_id, Long recipient_id, String typeAccountSender, String typeAccountRecipient,
      Float balance, String observation) {
    if (typeAccountSender.equals("current")) {
      transferFromCurrentAccount(sender_id, recipient_id, balance, observation, typeAccountRecipient);
    } else {
      transferFromSavingAccount(sender_id, recipient_id, balance, observation, typeAccountRecipient);
    }
  }

  private void transferFromCurrentAccount(Long sender_id, Long recipient_id, Float balance, String observation,
      String typeAccountRecipient) {
    CurrentAccount sendAccount = currentAccountRepository.findById(sender_id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sender Account not found"));
    validateBalance(sendAccount.getSaldo(), balance);
    sendAccount.setSaldo(sendAccount.getSaldo() - balance);
    currentAccountRepository.save(sendAccount);
    createTransferTransactionBank("transfer", LocalDateTime.now(), observation, balance, "current", true, sender_id,
        recipient_id);

    handleRecipientAccount(recipient_id, balance, observation, typeAccountRecipient, sender_id);
  }

  private void transferFromSavingAccount(Long sender_id, Long recipient_id, Float balance, String observation,
      String typeAccountRecipient) {
    SavingAccount sendAccount = savingAccountRepository.findById(sender_id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sender Account not found"));
    validateBalance(sendAccount.getSaldo(), balance);
    sendAccount.setSaldo(sendAccount.getSaldo() - balance);
    savingAccountRepository.save(sendAccount);
    createTransferTransactionBank("transfer", LocalDateTime.now(), observation, balance, "saving", true, sender_id,
        recipient_id);

    handleRecipientAccount(recipient_id, balance, observation, typeAccountRecipient, sender_id);
  }

  private void handleRecipientAccount(Long recipient_id, Float balance, String observation, String typeAccountRecipient,
      Long sender_id) {
    if (typeAccountRecipient.equals("current")) {
      CurrentAccount recipientAccount = currentAccountRepository.findById(recipient_id)
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipient Account not found"));
      recipientAccount.setSaldo(recipientAccount.getSaldo() + balance);
      currentAccountRepository.save(recipientAccount);
      createTransferTransactionBank("transfer", LocalDateTime.now(), observation, balance, "current", false, sender_id,
          recipient_id);
    } else {
      SavingAccount recipientAccount = savingAccountRepository.findById(recipient_id)
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipient Account not found"));
      recipientAccount.setSaldo(recipientAccount.getSaldo() + balance);
      savingAccountRepository.save(recipientAccount);
      createTransferTransactionBank("transfer", LocalDateTime.now(), observation, balance, "saving", false, sender_id,
          recipient_id);
    }
  }

  private void validateBalance(Float currentBalance, Float balance) {
    if (!suficientBalance(currentBalance, balance)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente para realizar esta operação.");
    }
  }

  private void createTransferTransactionBank(String operation, LocalDateTime dateTimeOperation, String observation,
      Float value, String typeAccount, Boolean sender, Long sender_id, Long recipient_id) {
    TransactionBank newTransaction = new TransactionBank();
    if (typeAccount.equals("current")) {
      Optional<CurrentAccount> accountOpt = sender ? currentAccountRepository.findById(sender_id)
          : currentAccountRepository.findById(recipient_id);
      CurrentAccount account = accountOpt
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
      if (sender) {
        newTransaction.setCurrentAccount(account);
        newTransaction.setDescription("Transferiu R$" + value + " para a conta ID: " + recipient_id);
      } else {
        newTransaction.setCurrentAccount(account);
        newTransaction.setDescription("Recebeu R$" + value + " da conta ID: " + sender_id);
      }
    } else {
      Optional<SavingAccount> accountOpt = sender ? savingAccountRepository.findById(sender_id)
          : savingAccountRepository.findById(recipient_id);
      SavingAccount account = accountOpt
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
      if (sender) {
        newTransaction.setSavingAccount(account);
        newTransaction.setDescription("Transferiu R$" + value + " para a conta ID: " + recipient_id);
      } else {
        newTransaction.setSavingAccount(account);
        newTransaction.setDescription("Recebeu R$" + value + " da conta ID: " + sender_id);
      }
    }

    newTransaction.setDateTimeOperation(dateTimeOperation);
    newTransaction.setObservation(observation);
    newTransaction.setOperation(operation);
    newTransaction.setValue(value);
    transactionBankRepository.save(newTransaction);
  }

  @Override
  public void statement() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'statement'");
  }

  private Boolean isWhatTypeAccount(Record type) {
    return (type instanceof CreateCurrentAccountDto);
  }

  private CurrentAccount createCurrentAccount(CreateCurrentAccountDto currentAccount) {
    var existingCurrentAccount = currentAccountRepository.findByCpf(currentAccount.cpf());

    if (existingCurrentAccount.isPresent()) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Current Account already exists");
    }

    var newCurrentAccount = new CurrentAccount();
    newCurrentAccount.setCpf(currentAccount.cpf());
    newCurrentAccount.setBank(bank.get());
    newCurrentAccount.setNameClient(currentAccount.nameClient());
    newCurrentAccount.setSaldo(currentAccount.saldo());
    currentAccountRepository.save(newCurrentAccount);

    return newCurrentAccount;
  }

  private SavingAccount createSavingAccount(CreateSavingAccountDto savingAccount) {
    var existingSavingAccount = savingAccountRepository.findByCpf(savingAccount.cpf());

    if (existingSavingAccount.isPresent()) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Saving Account already exists");
    }

    var newSavingAccount = new SavingAccount();
    newSavingAccount.setCpf(savingAccount.cpf());
    newSavingAccount.setCpf(savingAccount.cpf());
    newSavingAccount.setBank(bank.get());
    newSavingAccount.setNameClient(savingAccount.nameClient());
    newSavingAccount.setSaldo(savingAccount.saldo());

    savingAccountRepository.save(newSavingAccount);

    return newSavingAccount;
  }

  private Boolean suficientBalance(Float currentBalance, Float balance) {
    if (currentBalance - balance >= 0) {
      return true;
    }
    return false;
  }

}
