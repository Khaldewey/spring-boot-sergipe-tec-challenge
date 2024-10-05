package tec.challenge.bank.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_transactions")
public class TransactionBank {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "operation", nullable = false)
  private String operation;
  @Column(name = "dateTimeOperation", nullable = false)
  private LocalDateTime dateTimeOperation;

  @Column(name = "observation", nullable = false)
  private String observation;

  @Column(name = "value", nullable = false)
  private Float value;

  @Column(name = "description", nullable = false)
  private String description;

  @ManyToOne
  @JoinColumn(name = "saving_account_id")
  private SavingAccount savingAccount;

  @ManyToOne
  @JoinColumn(name = "current_account_id")
  private CurrentAccount currentAccount;

}
