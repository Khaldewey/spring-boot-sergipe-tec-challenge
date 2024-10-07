package tec.challenge.bank.services;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


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

  
  public void initBank() {
    bank = bankRepository.findById(1L);
    if (bank.isEmpty()) {
        System.out.println("Banco não encontrado");
    } else {
        System.out.println("Banco encontrado: " + bank.get().getId());
    }
}

  @Override
  public List<TransactionBank> transactionByCurrentAccount(CurrentAccount account) {
    List<TransactionBank> transactions = transactionBankRepository.findByCurrentAccount(account);
    return transactions;
  }

  @Override
  public List<TransactionBank> transactionBySavingAccount(SavingAccount account) {
    List<TransactionBank> transactions = transactionBankRepository.findBySavingAccount(account);
    return transactions;
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
    return bank.map(b -> currentAccountRepository.findByBank(b)).orElse(Collections.emptyList());
  }

  @Override
  public List<SavingAccount> getAllSavingAccounts() {
    return bank.map(b -> savingAccountRepository.findByBank(b)).orElse(Collections.emptyList());
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
  public void depositAtCurrentAccount(Long id, Float balance, String observation, String typeOperation) {

    CurrentAccount account = currentAccountRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
    Float currentBalance = account.getSaldo();
    account.setSaldo(currentBalance + balance);
    currentAccountRepository.save(account);

    createExtractDepositCurrentAccount(id, balance, observation, LocalDateTime.now(), typeOperation);
  }

  @Override
  public void depositAtSavingAccount(Long id, Float balance, String observation, String typeOperation) {
    SavingAccount account = savingAccountRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
    Float savingBalance = account.getSaldo();
    account.setSaldo(savingBalance + balance);
    savingAccountRepository.save(account);

    createExtractDepositSavingAccount(id, balance, observation, LocalDateTime.now(), typeOperation);
  }

  @Override
  public void withdrawAtCurrentAccount(Long id, Float balance, String observation, String typeOperation) {
    CurrentAccount account = currentAccountRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
    Float currentBalance = account.getSaldo();
    if (!suficientBalance(currentBalance, balance)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente para realizar esta operação.");
    }

    account.setSaldo(currentBalance - balance);
    currentAccountRepository.save(account);
    createExtractWithdrawCurrentAccount(id, balance, observation, LocalDateTime.now(), typeOperation);
  }

  @Override
  public void withdrawAtSavingAccount(Long id, Float balance, String observation, String typeOperation) {
    SavingAccount account = savingAccountRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
    Float savingBalance = account.getSaldo();
    if (!suficientBalance(savingBalance, balance)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente para realizar esta operação.");
    }

    account.setSaldo(savingBalance - balance);
    savingAccountRepository.save(account);
    createExtractWithdrawSavingAccount(id, balance, observation, LocalDateTime.now(), typeOperation);
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
  public List<TransactionBank> statement() {
    return transactionBankRepository.findAll();
    
  }

  @Override
  public List<TransactionBank> statementByType(String type) {
    return transactionBankRepository.findByOperation(type);
    
  }

  private Boolean isWhatTypeAccount(Record type) {
    return (type instanceof CreateCurrentAccountDto);
  }

  private CurrentAccount createCurrentAccount(CreateCurrentAccountDto currentAccount) {
    Optional<CurrentAccount> existingCurrentAccount = currentAccountRepository.findByCpf(currentAccount.cpf());

    if (existingCurrentAccount.isPresent()) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Conta Corrente já existe com esse cpf.");
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
    Optional<SavingAccount> existingSavingAccount = savingAccountRepository.findByCpf(savingAccount.cpf());

    if (existingSavingAccount.isPresent()) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Conta Poupança já existe com esse cpf.");
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

  @Override
  public void createExtractDepositCurrentAccount(Long id, Float value, String observation,
      LocalDateTime dateTimeOperation, String typeOperation) {
    Optional<CurrentAccount> currentAccount = currentAccountRepository.findById(id);
    TransactionBank transactionBank = new TransactionBank();
    transactionBank.setCurrentAccount(currentAccount.get());
    transactionBank.setDateTimeOperation(dateTimeOperation);
    transactionBank.setObservation(observation);
    transactionBank.setOperation(typeOperation);
    transactionBank.setValue(value);
    transactionBank.setDescription("Conta Corrente ID: " + currentAccount.get().getId().toString()
        + " recebeu deposito no valor de R$: " + value.toString());

    transactionBankRepository.save(transactionBank);
  }

  @Override
  public void createExtractDepositSavingAccount(Long id, Float value, String observation,
      LocalDateTime dateTimeOperation, String typeOperation) {
    Optional<SavingAccount> savingAccount = savingAccountRepository.findById(id);
    TransactionBank transactionBank = new TransactionBank();
    transactionBank.setSavingAccount(savingAccount.get());
    transactionBank.setDateTimeOperation(dateTimeOperation);
    transactionBank.setObservation(observation);
    transactionBank.setOperation(typeOperation);
    transactionBank.setValue(value);
    transactionBank.setDescription("Conta Poupanca ID: " + savingAccount.get().getId().toString()
        + " recebeu deposito no valor de R$: " + value.toString());

    transactionBankRepository.save(transactionBank);

  }

  @Override
  public void createExtractWithdrawCurrentAccount(Long id, Float value, String observation,
      LocalDateTime dateTimeOperation, String typeOperation) {
    Optional<CurrentAccount> currentAccount = currentAccountRepository.findById(id);
    TransactionBank transactionBank = new TransactionBank();
    transactionBank.setCurrentAccount(currentAccount.get());
    transactionBank.setDateTimeOperation(dateTimeOperation);
    transactionBank.setObservation(observation);
    transactionBank.setOperation(typeOperation);
    transactionBank.setValue(value);
    transactionBank.setDescription("Conta Corrente ID: " + currentAccount.get().getId().toString()
        + " fez um saque de R$: " + value.toString());

    transactionBankRepository.save(transactionBank);
  }

  @Override
  public void createExtractWithdrawSavingAccount(Long id, Float value, String observation,
      LocalDateTime dateTimeOperation, String typeOperation) {
    Optional<SavingAccount> savingAccount = savingAccountRepository.findById(id);
    TransactionBank transactionBank = new TransactionBank();
    transactionBank.setSavingAccount(savingAccount.get());
    transactionBank.setDateTimeOperation(dateTimeOperation);
    transactionBank.setObservation(observation);
    transactionBank.setOperation(typeOperation);
    transactionBank.setValue(value);
    transactionBank.setDescription("Conta Poupanca ID: " + savingAccount.get().getId().toString()
        + " fez um saque de R$: " + value.toString());

    transactionBankRepository.save(transactionBank);
  }

}
