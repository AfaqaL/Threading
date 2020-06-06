// Buffer.java

import java.util.concurrent.*;

/*
 Holds the transactions for the worker
 threads.
*/
public class Buffer {
	public static final int SIZE = 64;
	private Account[] accounts;
	private BlockingQueue<Transaction> blockq;
	private Bank bufferBank;

	public Buffer(int numAccounts, Bank bank){
		accounts = new Account[numAccounts];
		blockq = new ArrayBlockingQueue<>(SIZE);
		initAccounts();
		bufferBank = bank;
	}

	private void initAccounts() {
		for (int i = 0; i < accounts.length; i++) {
			accounts[i] = new Account(bufferBank, i, Account.INIT_ACCOUNT);
		}
	}

	public void putTransaction(Transaction currTrans){
		try {
			blockq.put(currTrans);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public Transaction takeTransaction(){
		try {
			return blockq.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return takeTransaction();
		}
	}

	public void processTransaction(Transaction curr){
		accounts[curr.from].adjustBalance(-curr.amount);
		accounts[curr.to].adjustBalance(curr.amount);
	}

	@Override
	public String toString() {
		String res = "";
		for (int i = 0; i < accounts.length; i++) {
			res += accounts[i].toString() + '\n';
		}
		return res;
	}

	// YOUR CODE HERE
}
