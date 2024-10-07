package tec.challenge.bank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tec.challenge.bank.models.CurrentAccount;
import tec.challenge.bank.models.SavingAccount;
import tec.challenge.bank.models.TransactionBank;

@Repository
public interface TransactionBankRepository extends JpaRepository<TransactionBank, Long> {
  List<TransactionBank> findByCurrentAccount(CurrentAccount account);

  List<TransactionBank> findBySavingAccount(SavingAccount account);

  List<TransactionBank> findByOperation(String operation);
}
