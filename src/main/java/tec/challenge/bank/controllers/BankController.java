package tec.challenge.bank.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import tec.challenge.bank.controllers.dtos.CreateCurrentAccountDto;
import tec.challenge.bank.controllers.dtos.CreateSavingAccountDto;
import tec.challenge.bank.models.CurrentAccount;
import tec.challenge.bank.models.SavingAccount;
import tec.challenge.bank.services.IBankService;

@Controller
public class BankController {
  @Autowired
  private IBankService bankService;

  @GetMapping("/dashboard")
  public String dashboard() {
    return "dashboard";
  }

  @PostMapping("/dashboard/create-account")
  public String createAccount(@RequestParam("accountType") String accountType,
      @RequestParam("nameClient") String nameClient,
      @RequestParam("saldo") Float saldo,
      @RequestParam("cpf") Long cpf, Model model) {

    // Verifica o tipo de conta e cria o DTO correspondente
    if ("CURRENT".equalsIgnoreCase(accountType)) {
      CreateCurrentAccountDto currentAccountDto = new CreateCurrentAccountDto(nameClient, saldo, cpf);
      bankService.createAccount(currentAccountDto); // Cria conta corrente
    } else if ("SAVING".equalsIgnoreCase(accountType)) {
      CreateSavingAccountDto savingAccountDto = new CreateSavingAccountDto(nameClient, saldo, cpf);
      bankService.createAccount(savingAccountDto); // Cria conta poupança
    } else {
      model.addAttribute("message", "Tipo de conta inválido!");
      return "dashboard";
    }

    model.addAttribute("message", "Conta criada com sucesso!");
    return "dashboard";
  }

  @PostMapping("/dashboard/consult-current-account")
  public String consultCurrentAccount(@RequestParam("accountId") Long accountId, Model model) {
    Optional<CurrentAccount> account = bankService.consultCurrentAccount(accountId);
    if (account.isPresent()) {
      model.addAttribute("currentAccount", account.get());
    } else {
      model.addAttribute("message", "Conta corrente não encontrada!");
    }
    return "dashboard";
  }

  // Consulta uma conta poupança
  @PostMapping("/dashboard/consult-saving-account")
  public String consultSavingAccount(@RequestParam("accountId") Long accountId, Model model) {
    Optional<SavingAccount> account = bankService.consultSavingAccount(accountId);
    if (account.isPresent()) {
      model.addAttribute("savingAccount", account.get());
    } else {
      model.addAttribute("message", "Conta poupança não encontrada!");
    }
    return "dashboard";
  }

}
