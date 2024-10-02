package tec.challenge.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tec.challenge.bank.models.CurrentAccount;

public interface CurrentAccountRepository extends JpaRepository<CurrentAccount, Long> {

}
