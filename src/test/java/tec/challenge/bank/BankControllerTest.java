package tec.challenge.bank;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import tec.challenge.bank.controllers.BankController;
import tec.challenge.bank.controllers.dtos.CreateCurrentAccountDto;

import tec.challenge.bank.services.IBankService;

import java.util.Collections;
import java.util.Optional;

public class BankControllerTest {

    @InjectMocks
    private BankController bankController;

    @Mock
    private IBankService bankService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(bankController).build();
    }

    @Test
    void testDashboard() throws Exception {
        when(bankService.getAllCurrentAccounts()).thenReturn(Collections.emptyList());
        when(bankService.getAllSavingAccounts()).thenReturn(Collections.emptyList());
        when(bankService.statement()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("currentAccounts"))
                .andExpect(model().attributeExists("savingAccounts"));
    }

    @Test
    void testCreateAccountSuccess() throws Exception {
        String accountType = "CURRENT";
        String nameClient = "Client Name";
        Float saldo = 1000f;
        Long cpf = 12345678901L;

        mockMvc.perform(post("/dashboard/create-account")
                .param("accountType", accountType)
                .param("nameClient", nameClient)
                .param("saldo", String.valueOf(saldo))
                .param("cpf", String.valueOf(cpf)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("message", "Conta criada com sucesso!"))
                .andExpect(view().name("create-account"));

        verify(bankService, times(1)).createAccount(any());
    }

    @Test
    void testConsultCurrentAccountNotFound() throws Exception {
        Long accountId = 1L;
        when(bankService.consultCurrentAccount(accountId)).thenReturn(Optional.empty());

        mockMvc.perform(post("/dashboard/consult-current-account")
                .param("accountId", String.valueOf(accountId)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("message", "Conta corrente não encontrada!"))
                .andExpect(view().name("dashboard"));
    }

    @Test
    void testEditCurrentAccountSuccess() throws Exception {
        Long accountId = 1L;
        CreateCurrentAccountDto dto = new CreateCurrentAccountDto("Client", 1500f, 12345678901L);

        doNothing().when(bankService).editCurrentAccount(eq(accountId), any());

        mockMvc.perform(post("/dashboard/edit-current-account/{id}", accountId)
                .flashAttr("accountDto", dto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"))
                .andExpect(flash().attribute("message", "Conta editada com sucesso!"));

        verify(bankService, times(1)).editCurrentAccount(eq(accountId), any());
    }

    @Test
    void testDeleteCurrentAccount() throws Exception {
        Long accountId = 1L;

        mockMvc.perform(post("/dashboard/delete-current-account/{id}", accountId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"))
                .andExpect(flash().attribute("message", "Conta excluída com sucesso."));

        verify(bankService, times(1)).deleteCurrentAccount(accountId);
    }
}
