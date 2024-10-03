package tec.challenge.bank.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import tec.challenge.bank.models.CurrentAccount;
import tec.challenge.bank.models.SavingAccount;

@Service
public interface IBankService {
  public void createAccount(Record type);

  public Optional<CurrentAccount> consultCurrentAccount(Long id);

  public Optional<SavingAccount> consultSavingAccount(Long id);

  public void editAccount(Long id);

  public void deleteAccount(Long id);

  public void depositAtCurrentAccount(Long id);

  public void depositAtSavingAccount(Long id);

  public void withdrawAtCurrentAccount(Long id);

  public void withdrawAtSavingAccount(Long id);

  public void transferAtCurrentAccount(Long sender_id, Long recipient_id);

  public void transferAtSavingAccount(Long sender_id, Long recipient_id);

  public void statement();
}
