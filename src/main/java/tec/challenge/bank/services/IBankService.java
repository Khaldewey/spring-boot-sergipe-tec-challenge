package tec.challenge.bank.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import tec.challenge.bank.controllers.dtos.CreateCurrentAccountDto;
import tec.challenge.bank.controllers.dtos.CreateSavingAccountDto;
import tec.challenge.bank.models.CurrentAccount;
import tec.challenge.bank.models.SavingAccount;

@Service
public interface IBankService {
  public void createAccount(Record type);

  public Optional<CurrentAccount> consultCurrentAccount(Long id);

  public Optional<SavingAccount> consultSavingAccount(Long id);

  public List<CurrentAccount> getAllCurrentAccounts();

  public List<SavingAccount> getAllSavingAccounts();

  public void editCurrentAccount(Long id, CreateCurrentAccountDto dto);

  public void editSavingAccount(Long id, CreateSavingAccountDto dto);

  public void deleteAccount(Long id);

  public void depositAtCurrentAccount(Long id);

  public void depositAtSavingAccount(Long id);

  public void withdrawAtCurrentAccount(Long id);

  public void withdrawAtSavingAccount(Long id);

  public void transferAtCurrentAccount(Long sender_id, Long recipient_id);

  public void transferAtSavingAccount(Long sender_id, Long recipient_id);

  public void statement();
}
