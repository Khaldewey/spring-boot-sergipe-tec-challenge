package tec.challenge.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tec.challenge.bank.models.SavingAccount;

@Repository
public interface SavingAccountRepository extends JpaRepository<SavingAccount, Long> {

}
