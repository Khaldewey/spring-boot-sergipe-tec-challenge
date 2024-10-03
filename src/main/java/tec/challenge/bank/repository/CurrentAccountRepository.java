package tec.challenge.bank.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tec.challenge.bank.models.Bank;
import tec.challenge.bank.models.CurrentAccount;

@Repository
public interface CurrentAccountRepository extends JpaRepository<CurrentAccount, Long> {
  Optional<CurrentAccount> findByCpf(Long cpf);

  List<CurrentAccount> findByBank(Bank bank);
}
