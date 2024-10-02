package tec.challenge.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tec.challenge.bank.models.Bank;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {
}
