package tec.challenge.bank.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import tec.challenge.bank.controllers.dtos.CreateCurrentAccountDto;
import tec.challenge.bank.controllers.dtos.CreateSavingAccountDto;
import tec.challenge.bank.models.CurrentAccount;
import tec.challenge.bank.models.SavingAccount;
import tec.challenge.bank.repository.CurrentAccountRepository;
import tec.challenge.bank.repository.SavingAccountRepository;

public class BankService implements IBankService {
  @Autowired
  CurrentAccountRepository currentAccountRepository;
  @Autowired
  SavingAccountRepository savingAccountRepository;

  @Override
  public void createAccount(Record dto) {
    if (isWhatTypeAccount(dto)) {
      createCurrentAccount((CreateCurrentAccountDto) dto);
    } else {
      createSavingAccount((CreateSavingAccountDto) dto);
    }
  }

  @Override
  public CurrentAccount consultCurrentAccount(Long id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'consultCurrentAccount'");
  }

  @Override
  public SavingAccount consultSavingAccount(Long id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'consultSavingAccount'");
  }

  @Override
  public void editAccount(Long id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'editAccount'");
  }

  @Override
  public void deleteAccount(Long id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'deleteAccount'");
  }

  @Override
  public void depositAtCurrentAccount(Long id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'depositAtCurrentAccount'");
  }

  @Override
  public void depositAtSavingAccount(Long id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'depositAtSavingAccount'");
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
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Founder already exists");
    }

    var newCurrentAccount = new CurrentAccount();
    newCurrentAccount.setCpf(currentAccount.cpf());

    currentAccountRepository.save(newCurrentAccount);

    return newCurrentAccount;
  }

  private SavingAccount createSavingAccount(CreateSavingAccountDto savingAccount) {
    var existingSavingAccount = savingAccountRepository.findByCpf(savingAccount.cpf());

    if (existingSavingAccount.isPresent()) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Founder already exists");
    }

    var newSavingAccount = new SavingAccount();
    newSavingAccount.setCpf(savingAccount.cpf());

    savingAccountRepository.save(newSavingAccount);

    return newSavingAccount;
  }

}
