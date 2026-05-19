import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BankingTransaction {

    public record Transaction(String type, int amount, LocalDateTime timestamp, String status) {
    }

    static class Account {
        private final int accountId;
        private int balance;
        private final List<Transaction> ledger = new ArrayList<>();

        Account(int accountId, int initialBalance) {
            this.accountId = accountId;
            this.balance = initialBalance;
            logTransaction("INITIAL_DEPOSIT", initialBalance, "SUCCESS");
        }

        public synchronized int getBalance() {
            return balance;
        }

        public synchronized boolean withdraw(int amount) {
            if (amount <= 0) {
                logTransaction("WITHDRAW", amount, "FAILED - INVALID AMOUNT");
                return false;
            }
            if (this.balance < amount) {
                logTransaction("WITHDRAW", amount, "FAILED - OVERDRAFT");
                return false;
            }
            this.balance -= amount;
            logTransaction("WITHDRAW", amount, "SUCCESS");
            return true;
        }

        public synchronized boolean deposit(int amount) {
            if (amount <= 0) {
                logTransaction("DEPOSIT", amount, "FAILED - AMOUNT INVALID");
                return false;
            }
            this.balance += amount;
            logTransaction("DEPOSIT", amount, "SUCCESS");
            return true;
        }

        private void logTransaction(String type, int amount, String status) {
            ledger.add(new Transaction(type, amount, LocalDateTime.now(), status));
        }

        public List<Transaction> getLedger() {
            
            return new ArrayList<>(ledger);
        }
    }


    private final Map<Integer, Account> accounts = new HashMap<>();

    public void createAccount(int id, int initialBalance) {
        if (initialBalance < 0) {
            System.out.println("Error: Initial balance cannot be negative.");
            return;
        }
        accounts.putIfAbsent(id, new Account(id, initialBalance));
    }

    public void deposit(int id, int amount) {
        Account account = accounts.get(id);
        if (account == null) {
            System.out.println("Deposit failed: Account " + id + " does not exist.");
            return;
        }
        if (account.deposit(amount)) {
            System.out.println("Deposited " + amount + " to account " + id + ". New Balance: " + account.getBalance());
        } else {
            System.out.println("Deposit failed for account " + id + " (Invalid Amount).");
        }
    }

    public void withdraw(int id, int amount) {
        Account account = accounts.get(id);
        if (account == null) {
            System.out.println("Withdraw failed: Account " + id + " does not exist.");
            return;
        }
        if (account.withdraw(amount)) {
            System.out.println("Withdrew "+amount+" from account "+id+" New Balance: "+account.getBalance());
        } else {
            System.out.println("Withdrawal failed for account " + id + " (Insufficient funds or invalid amount).");
        }
    }
    

    public synchronised void accountTransfer(int fromId, int toId, int amount) {
          if (fromAccount.getBalance() >= amount) {
                    fromAccount.withdraw(amount);
                    toAccount.deposit(amount);
                    System.out.println("Successfully transferred " + amount + " from Account " + fromId + " to Account " + toId);
                } else {
                    System.out.println("Transfer failed: Account " + fromId + " has insufficient funds.");
                }
            }

    public Integer getAccountBalance(int id) {
        Account account = accounts.get(id);
        if (account != null) {
            return account.getBalance();
        }
        return null;
    }

    public List<Transaction> getAccountHistory(int id) {
        Account account = accounts.get(id);
        if (account != null) {
            return account.getLedger();
        }
        return Collections.emptyList();
    }

    public static void main(String[] args) throws InterruptedException {
        BankingTransaction banktTransaction = new BankingTransaction();
        banktTransaction.createAccount(1, 10000);
        banktTransaction.createAccount(2, 15000);


        Thread t1 = new Thread(() -> banktTransaction.accountTransfer(1, 2, 1000));
        Thread t2 = new Thread(() -> banktTransaction.accountTransfer(2, 1, 500));
        Thread t3 = new Thread(() -> banktTransaction.withdraw(1, 12000));

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        banktTransaction.getAccountHistory(1).forEach(System.out::println);
        banktTransaction.getAccountHistory(2).forEach(System.out::println);
    }
}
