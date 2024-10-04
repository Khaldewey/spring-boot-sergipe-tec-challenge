package tec.challenge.bank.services;

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
import tec.challenge.bank.repository.BankRepository;
import tec.challenge.bank.repository.CurrentAccountRepository;
import tec.challenge.bank.repository.SavingAccountRepository;

@Service
public class BankService implements IBankService {

  @Autowired
  CurrentAccountRepository currentAccountRepository;
  @Autowired
  SavingAccountRepository savingAccountRepository;
  @Autowired
  BankRepository bankRepository;

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
  public void withdrawAtCurrentAccount(Long id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'withdrawAtCurrentAccount'");
  }

  @Override
  public void withdrawAtSavingAccount(Long id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'withdrawAtSavingAccount'");
  }

  @Override
  public void transferAtCurrentAccount(Long sender_id, Long recipient_id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'transferAtCurrentAccount'");
  }

  @Override
  public void transferAtSavingAccount(Long sender_id, Long recipient_id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'transferAtSavingAccount'");
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

}
