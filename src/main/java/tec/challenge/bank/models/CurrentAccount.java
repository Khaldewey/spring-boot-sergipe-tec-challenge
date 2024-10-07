package tec.challenge.bank.models;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_current_accounts", uniqueConstraints = { @UniqueConstraint(columnNames = "cpf") })
public class CurrentAccount {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "nameClient", nullable = false)
  private String nameClient;
  @Column(name = "saldo", nullable = false)
  private Float saldo;
  @Column(name = "cpf", nullable = false)
  private Long cpf;

  @OneToMany(mappedBy = "currentAccount", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TransactionBank> transactions;

  @ManyToOne
  @JoinColumn(name = "bank_id")
  private Bank bank;

}
