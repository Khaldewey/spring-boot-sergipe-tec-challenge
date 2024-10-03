package tec.challenge.bank.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

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
  public String dashboard(Model model) {
    List<CurrentAccount> currentAccounts = bankService.getAllCurrentAccounts();
    List<SavingAccount> savingAccounts = bankService.getAllSavingAccounts();

    model.addAttribute("currentAccounts", currentAccounts);
    model.addAttribute("savingAccounts", savingAccounts);

    return "dashboard";
  }

  @PostMapping("/dashboard/create-account")
  public String createAccount(@RequestParam("accountType") String accountType,
      @RequestParam("nameClient") String nameClient,
      @RequestParam("saldo") Float saldo,
      @RequestParam("cpf") Long cpf, Model model) {
    List<CurrentAccount> currentAccounts = bankService.getAllCurrentAccounts();
    List<SavingAccount> savingAccounts = bankService.getAllSavingAccounts();

    model.addAttribute("currentAccounts", currentAccounts);
    model.addAttribute("savingAccounts", savingAccounts);

    if ("CURRENT".equalsIgnoreCase(accountType)) {
      CreateCurrentAccountDto currentAccountDto = new CreateCurrentAccountDto(nameClient, saldo, cpf);
      bankService.createAccount(currentAccountDto);
    } else if ("SAVING".equalsIgnoreCase(accountType)) {
      CreateSavingAccountDto savingAccountDto = new CreateSavingAccountDto(nameClient, saldo, cpf);
      bankService.createAccount(savingAccountDto);
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
    List<CurrentAccount> currentAccounts = bankService.getAllCurrentAccounts();
    List<SavingAccount> savingAccounts = bankService.getAllSavingAccounts();

    model.addAttribute("currentAccounts", currentAccounts);
    model.addAttribute("savingAccounts", savingAccounts);
    if (account.isPresent()) {
      model.addAttribute("currentAccount", account.get());
    } else {
      model.addAttribute("message", "Conta corrente não encontrada!");
    }
    return "dashboard";
  }

  @PostMapping("/dashboard/consult-saving-account")
  public String consultSavingAccount(@RequestParam("accountId") Long accountId, Model model) {
    Optional<SavingAccount> account = bankService.consultSavingAccount(accountId);
    List<CurrentAccount> currentAccounts = bankService.getAllCurrentAccounts();
    List<SavingAccount> savingAccounts = bankService.getAllSavingAccounts();

    model.addAttribute("currentAccounts", currentAccounts);
    model.addAttribute("savingAccounts", savingAccounts);
    if (account.isPresent()) {
      model.addAttribute("savingAccount", account.get());
    } else {
      model.addAttribute("message", "Conta poupança não encontrada!");
    }
    return "dashboard";
  }

  // editar Conta Corrente
  @GetMapping("/dashboard/edit-current-account/{id}")
  public String showEditCurrentAccountForm(@PathVariable("id") Long id, Model model) {
    CurrentAccount account = bankService.consultCurrentAccount(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

    model.addAttribute("account", account);
    return "edit-current-account";
  }

  @PostMapping("/dashboard/edit-current-account/{id}")
  public String editCurrentAccount(@PathVariable("id") Long id, @ModelAttribute CreateCurrentAccountDto accountDto) {
    bankService.editCurrentAccount(id, accountDto);
    return "redirect:/dashboard";
  }

  // editar Conta Poupança
  @GetMapping("/dashboard/edit-saving-account/{id}")
  public String showEditSavingAccountForm(@PathVariable("id") Long id, Model model) {
    SavingAccount account = bankService.consultSavingAccount(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

    model.addAttribute("account", account);
    return "edit-saving-account";
  }

  @PostMapping("/dashboard/edit-saving-account/{id}")
  public String editSavingAccount(@PathVariable("id") Long id, @ModelAttribute CreateSavingAccountDto accountDto) {
    bankService.editSavingAccount(id, accountDto);
    return "redirect:/dashboard";
  }

}
