package tec.challenge.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tec.challenge.bank.models.Bank;

public interface BankRepository extends JpaRepository<Bank, Long> {

}
