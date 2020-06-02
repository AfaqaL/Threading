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
		initAccounts(numAccounts);
		bufferBank = bank;
	}

	private void initAccounts(int numAccounts) {
		for (int i = 0; i < numAccounts; i++) {
			accounts[i] = new Account(bufferBank, i, Account.INIT_ACCOUNT);
		}
	}

	public void putTransaction(Transaction currTrans){
		blockq.add(currTrans);
	}

	public Transaction takeTransaction(){
		try {
			return blockq.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return takeTransaction();
		}
	}


	// YOUR CODE HERE
}
