package tec.challenge.bank.configurations;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import jakarta.transaction.Transactional;
import tec.challenge.bank.models.Bank;
import tec.challenge.bank.repository.BankRepository;
import tec.challenge.bank.services.BankService;

@Configuration
public class BankXyzRegistation implements CommandLineRunner {

  @Autowired
  private BankRepository bankRepository;

  @Autowired
  private BankService bankService;

  @Override
  @Transactional
  public void run(String... args) throws Exception {
    
    Optional<Bank> optionalBank = bankRepository.findById(1l);

    if (optionalBank.isPresent()) {
      printBankExists();
    } else {
      persistNewBank(1l);
    }
    bankService.initBank();
  }
  
  private void printBankExists() {
    System.out.println("Banco já existe");
  }

  private void persistNewBank(Long id) {
    Bank bank = new Bank();
    bank.setId(id);
    bankRepository.save(bank);
  }

}
