package tec.challenge.bank.controllers;

import java.util.ArrayList;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tec.challenge.bank.controllers.dtos.CreateCurrentAccountDto;
import tec.challenge.bank.controllers.dtos.CreateSavingAccountDto;
import tec.challenge.bank.models.CurrentAccount;
import tec.challenge.bank.models.SavingAccount;
import tec.challenge.bank.models.TransactionBank;
import tec.challenge.bank.services.IBankService;

@Controller
public class BankController {
  @Autowired
  private IBankService bankService;

  @GetMapping("/dashboard")
  public String dashboard(Model model) {
    List<CurrentAccount> currentAccounts = bankService.getAllCurrentAccounts();
    List<SavingAccount> savingAccounts = bankService.getAllSavingAccounts();
    List<TransactionBank> transactions = bankService.statement();
    List<TransactionBank> deposits = bankService.statementByType("deposit");
    List<TransactionBank> withdraws= bankService.statementByType("withdraw"); 
    List<TransactionBank> transfers = bankService.statementByType("transfer");
    model.addAttribute("deposits", deposits);
    model.addAttribute("withdraws", withdraws);
    model.addAttribute("transfers", transfers);
    model.addAttribute("transactions", transactions);
    model.addAttribute("currentAccounts", currentAccounts);
    model.addAttribute("savingAccounts", savingAccounts);

    return "dashboard";
  }

  // Criar conta corrente ou poupança
  @PostMapping("/dashboard/create-account")
  public String createAccount(@RequestParam("accountType") String accountType,
      @RequestParam("nameClient") String nameClient,
      @RequestParam("saldo") Float saldo,
      @RequestParam("cpf") Long cpf, Model model) {
    List<CurrentAccount> currentAccounts = bankService.getAllCurrentAccounts();
    List<SavingAccount> savingAccounts = bankService.getAllSavingAccounts();
    List<TransactionBank> transactions = bankService.statement();
    model.addAttribute("transactions", transactions);
    model.addAttribute("currentAccounts", currentAccounts);
    model.addAttribute("savingAccounts", savingAccounts);
    // Antigos métodos de criação de contas sem tratamento de exceção
    // if ("CURRENT".equalsIgnoreCase(accountType)) {
    //   CreateCurrentAccountDto currentAccountDto = new CreateCurrentAccountDto(nameClient, saldo, cpf);
    //   bankService.createAccount(currentAccountDto);
    // } else if ("SAVING".equalsIgnoreCase(accountType)) {
    //   CreateSavingAccountDto savingAccountDto = new CreateSavingAccountDto(nameClient, saldo, cpf);
    //   bankService.createAccount(savingAccountDto);
    // } else {
    //   model.addAttribute("message", "Tipo de conta inválido!");
    //   return "dashboard";
    // }
    try {
      if ("CURRENT".equalsIgnoreCase(accountType)) {
          CreateCurrentAccountDto currentAccountDto = new CreateCurrentAccountDto(nameClient, saldo, cpf);
          bankService.createAccount(currentAccountDto);
          model.addAttribute("createdAccount", currentAccountDto);
          model.addAttribute("typeAccount", "current");
      } else if ("SAVING".equalsIgnoreCase(accountType)) {
          CreateSavingAccountDto savingAccountDto = new CreateSavingAccountDto(nameClient, saldo, cpf);
          bankService.createAccount(savingAccountDto);
          model.addAttribute("createdAccount", savingAccountDto); 
          model.addAttribute("typeAccount", "saving");
      } else {
          model.addAttribute("message", "Tipo de conta inválido!");
          return "create-account";
      }

      model.addAttribute("message", "Conta criada com sucesso!");
    } catch (ResponseStatusException e) {
        model.addAttribute("message", e.getReason());
    } catch (Exception e) {
        model.addAttribute("message", "Ocorreu um erro ao criar a conta.");
    }

    
    return "create-account";
  }

  // Consultar conta corrente
  @PostMapping("/dashboard/consult-current-account")
  public String consultCurrentAccount(@RequestParam("accountId") Long accountId, Model model) {
    Optional<CurrentAccount> account = bankService.consultCurrentAccount(accountId);
    List<CurrentAccount> currentAccounts = bankService.getAllCurrentAccounts();
    List<SavingAccount> savingAccounts = bankService.getAllSavingAccounts();
    List<TransactionBank> transactions = bankService.statement();
    List<TransactionBank> deposits = bankService.statementByType("deposit");
    List<TransactionBank> withdraws= bankService.statementByType("withdraw"); 
    List<TransactionBank> transfers = bankService.statementByType("transfer");
    model.addAttribute("deposits", deposits);
    model.addAttribute("withdraws", withdraws);
    model.addAttribute("transfers", transfers);
    model.addAttribute("transactions", transactions);
    model.addAttribute("currentAccounts", currentAccounts);
    model.addAttribute("savingAccounts", savingAccounts);
    if (account.isPresent()) {
      model.addAttribute("currentAccount", account.get());
    } else {
      model.addAttribute("message", "Conta corrente não encontrada!");
    }
    return "dashboard";
  }

  // Consultar conta poupança
  @PostMapping("/dashboard/consult-saving-account")
  public String consultSavingAccount(@RequestParam("accountId") Long accountId, Model model) {
    Optional<SavingAccount> account = bankService.consultSavingAccount(accountId);
    List<CurrentAccount> currentAccounts = bankService.getAllCurrentAccounts();
    List<SavingAccount> savingAccounts = bankService.getAllSavingAccounts();
    List<TransactionBank> transactions = bankService.statement();
    List<TransactionBank> deposits = bankService.statementByType("deposit");
    List<TransactionBank> withdraws= bankService.statementByType("withdraw"); 
    List<TransactionBank> transfers = bankService.statementByType("transfer");
    model.addAttribute("deposits", deposits);
    model.addAttribute("withdraws", withdraws);
    model.addAttribute("transfers", transfers);
    model.addAttribute("transactions", transactions);
    model.addAttribute("currentAccounts", currentAccounts);
    model.addAttribute("savingAccounts", savingAccounts);
    if (account.isPresent()) {
      model.addAttribute("savingAccount", account.get());
    } else {
      model.addAttribute("message", "Conta poupança não encontrada!");
    }
    return "dashboard";
  }

  // Editar Conta Corrente
  @GetMapping("/dashboard/edit-current-account/{id}")
  public String showEditCurrentAccountForm(@PathVariable("id") Long id, Model model) {
    CurrentAccount account = bankService.consultCurrentAccount(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

    model.addAttribute("account", account);
    return "edit-current-account";
  }

  @PostMapping("/dashboard/edit-current-account/{id}")
  public String editCurrentAccount(@PathVariable("id") Long id, @ModelAttribute CreateCurrentAccountDto accountDto,  RedirectAttributes redirectAttributes) {
    // bankService.editCurrentAccount(id, accountDto);
    try {
      bankService.editCurrentAccount(id, accountDto);
      redirectAttributes.addFlashAttribute("message", "Conta editada com sucesso!");
  } catch (ResponseStatusException e) {
    redirectAttributes.addFlashAttribute("message", e.getReason());
  } catch (Exception e) {
    redirectAttributes.addFlashAttribute("message", "Ocorreu um erro ao editar a conta.");
  }
    return "redirect:/dashboard";
    
  }

  // Editar Conta Poupança
  @GetMapping("/dashboard/edit-saving-account/{id}")
  public String showEditSavingAccountForm(@PathVariable("id") Long id, Model model) {
    SavingAccount account = bankService.consultSavingAccount(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

    model.addAttribute("account", account);
    return "edit-saving-account";
  }

  @PostMapping("/dashboard/edit-saving-account/{id}")
  public String editSavingAccount(@PathVariable("id") Long id, @ModelAttribute CreateSavingAccountDto accountDto, RedirectAttributes redirectAttributes) {
    // bankService.editSavingAccount(id, accountDto);
    try {
      bankService.editSavingAccount(id, accountDto);
      redirectAttributes.addFlashAttribute("message", "Conta editada com sucesso!");
  } catch (ResponseStatusException e) {
    redirectAttributes.addFlashAttribute("message", e.getReason());
  } catch (Exception e) {
    redirectAttributes.addFlashAttribute("message", "Ocorreu um erro ao editar a conta.");
  }
    return "redirect:/dashboard";
  }

  // Excluir conta corrente
  @PostMapping("/dashboard/delete-current-account/{id}")
  public String deleteCurrentAccount(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
    bankService.deleteCurrentAccount(id);
    redirectAttributes.addFlashAttribute("message", "Conta excluída com sucesso.");
    return "redirect:/dashboard";
  }

  // Excluir conta poupança
  @PostMapping("/dashboard/delete-saving-account/{id}")
  public String deleteSavingAccount(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
    bankService.deleteSavingAccount(id);
    redirectAttributes.addFlashAttribute("message", "Conta excluída com sucesso.");
    return "redirect:/dashboard";
  }

  // Depositar em conta corrente
  @GetMapping("/dashboard/current-deposits")
  public String showDepositAtCurrentAccount(Model model) {
    List<CurrentAccount> currentAccounts = bankService.getAllCurrentAccounts();
    model.addAttribute("currentAccounts", currentAccounts);
    return "deposit-current-account";
  }

  @PostMapping("/dashboard/deposit-current-account")
  public String depositAtCurrentAccount(@RequestParam("accountId") Long accountId,
      @RequestParam("balance") Float balance,
      @RequestParam("observation") String observation,
      @RequestParam("typeOperation") String typeOperation) {
    bankService.depositAtCurrentAccount(accountId, balance, observation, typeOperation);

    return "redirect:/dashboard/current-deposits";
  }

  // Depositar em conta poupança
  @GetMapping("/dashboard/saving-deposits")
  public String showDepositAtSavingAccount(Model model) {
    List<SavingAccount> savingAccounts = bankService.getAllSavingAccounts();
    model.addAttribute("savingAccounts", savingAccounts);
    return "deposit-saving-account";
  }

  @PostMapping("/dashboard/deposit-saving-account")
  public String depositAtSavingAccount(@RequestParam("accountId") Long accountId,
      @RequestParam("balance") Float balance,
      @RequestParam("observation") String observation,
      @RequestParam("typeOperation") String typeOperation) {
    bankService.depositAtSavingAccount(accountId, balance, observation, typeOperation);
    return "redirect:/dashboard/saving-deposits";
  }

  // Sacar de conta corrente
  @GetMapping("/dashboard/current-withdraws")
  public String showWithdrawAtCurrentAccount(Model model) {
    List<CurrentAccount> currentAccounts = bankService.getAllCurrentAccounts();
    model.addAttribute("currentAccounts", currentAccounts);
    return "withdraw-current-account";
  }

  @PostMapping("/dashboard/withdraw-current-account")
  public String withdrawAtCurrentAccount(@RequestParam("accountId") Long accountId,
      @RequestParam("balance") Float balance,
      @RequestParam("observation") String observation,
      @RequestParam("typeOperation") String typeOperation,
      RedirectAttributes redirectAttributes) {

    try {
      bankService.withdrawAtCurrentAccount(accountId, balance, observation, typeOperation);
      redirectAttributes.addFlashAttribute("message", "Saque realizado com sucesso.");
    } catch (ResponseStatusException e) {
      redirectAttributes.addFlashAttribute("errorMessage", e.getReason());
    }
    return "redirect:/dashboard/current-withdraws";
  }

  // Sacar de conta poupança
  @GetMapping("/dashboard/saving-withdraws")
  public String showWithdrawAtSavingAccount(Model model) {
    List<SavingAccount> savingAccounts = bankService.getAllSavingAccounts();
    model.addAttribute("savingAccounts", savingAccounts);
    return "withdraw-saving-account";
  }

  @PostMapping("/dashboard/withdraw-saving-account")
  public String withdrawAtSavingAccount(@RequestParam("accountId") Long accountId,
      @RequestParam("balance") Float balance,
      @RequestParam("observation") String observation,
      @RequestParam("typeOperation") String typeOperation,
      RedirectAttributes redirectAttributes) {

    try {
      bankService.withdrawAtSavingAccount(accountId, balance, observation, typeOperation);
      redirectAttributes.addFlashAttribute("message", "Saque realizado com sucesso.");
    } catch (ResponseStatusException e) {
      redirectAttributes.addFlashAttribute("errorMessage", e.getReason());
    }
    return "redirect:/dashboard/saving-withdraws";
  }

  @GetMapping("/dashboard/transfers")
  public String transfer(Model model) {
    List<CurrentAccount> currentAccounts = bankService.getAllCurrentAccounts();
    List<SavingAccount> savingAccounts = bankService.getAllSavingAccounts();

    model.addAttribute("currentAccounts", currentAccounts);
    model.addAttribute("savingAccounts", savingAccounts);
    return "transfers";
  }

  @PostMapping("/dashboard/transfer")
  public String transfer(
      @RequestParam("senderAccountId") Long senderId,
      @RequestParam("recipientAccountId") Long recipientId,
      @RequestParam("senderAccountType") String senderType,
      @RequestParam("recipientAccountType") String recipientType,
      @RequestParam("observation") String observation,
      @RequestParam("transferValue") Float transferValue,
      RedirectAttributes redirectAttributes,
      Model model) {
    try {
      bankService.transfer(senderId, recipientId, senderType, recipientType, transferValue, observation);
      redirectAttributes.addFlashAttribute("message", "Transferência realizada com sucesso.");
    } catch (ResponseStatusException e) {
      redirectAttributes.addFlashAttribute("errorMessage", e.getReason());
    }

    return "redirect:/dashboard/transfers";
  }

  @GetMapping("/dashboard/extracts")
  public String getAccountExtract(@RequestParam("accountId") Long accountId,
      @RequestParam("accountType") String accountType,
      Model model) {
    List<TransactionBank> transactions;

    if (accountType.equals("current")) {
      Optional<CurrentAccount> currentAccount = bankService.consultCurrentAccount(accountId);
      if (currentAccount.isPresent()) {
        transactions = bankService.transactionByCurrentAccount(currentAccount.get());
      } else {

        model.addAttribute("message", "Conta Corrente não encontrada.");
        transactions = new ArrayList<>();
      }
    } else if (accountType.equals("saving")) {
      Optional<SavingAccount> savingAccount = bankService.consultSavingAccount(accountId);
      if (savingAccount.isPresent()) {
        transactions = bankService.transactionBySavingAccount(savingAccount.get());
      } else {

        model.addAttribute("message", "Conta Poupança não encontrada.");
        transactions = new ArrayList<>();
      }
    } else {

      model.addAttribute("message", "Tipo de conta inválido.");
      transactions = new ArrayList<>();
    }

    model.addAttribute("transactions", transactions);
    return "extracts";
  }
}
