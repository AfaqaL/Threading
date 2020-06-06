// Account.java

/*
 Simple, thread-safe Account class encapsulates
 a balance and a transaction count.
*/
public class Account {
	private int id;
	private int balance;
	private int transactions;
	public static int INIT_ACCOUNT = 1000;
	
	// It may work out to be handy for the account to
	// have a pointer to its Bank.
	// (a suggestion, not a requirement)
	private Bank bank;  
	
	public Account(Bank bank, int id, int balance) {
		this.bank = bank;
		this.id = id;
		this.balance = balance;
		transactions = 0;
	}

	public synchronized void adjustBalance(int amount){
		balance += amount;
		transactions++;
	}

	@Override
	public String toString(){
		return "Account N" + id + " Balance: " + balance + " Transactions: " + transactions;
	}
	
}
