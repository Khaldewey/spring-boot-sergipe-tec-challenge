package tec.challenge.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tec.challenge.bank.models.TransactionBank;

@Repository
public interface TransactionBankRepository extends JpaRepository<TransactionBank, Long> {

}
