package tec.challenge.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tec.challenge.bank.models.SavingAccount;

public interface SavingAccountRepository extends JpaRepository<SavingAccount, Long> {

}
