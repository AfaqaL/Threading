// Bank.java

/*
 Creates a bunch of accounts and uses threads
 to post transactions to the accounts concurrently.
*/

import java.io.*;
import java.util.concurrent.CountDownLatch;

public class Bank {
	public static final int ACCOUNTS = 20;	 // number of accounts
	private static final int INITIAL_WORKERS = 1;

	private Buffer buffer;
	private	CountDownLatch latch;
	private int numWorkers;

	public Bank(int numWorkers){
		this.numWorkers = numWorkers;
		buffer = new Buffer(ACCOUNTS, this);
		latch = new CountDownLatch(numWorkers);
	}
	/*
	 Reads transaction data (from/to/amt) from a file for processing.
	 (provided code)
	 */
	public void readFile(String file) throws Exception{
		BufferedReader reader = new BufferedReader(new FileReader(file));

		// Use stream tokenizer to get successive words from file
		StreamTokenizer tokenizer = new StreamTokenizer(reader);

		while (true) {
			int read = tokenizer.nextToken();
			if (read == StreamTokenizer.TT_EOF) break;  // detect EOF
			int from = (int)tokenizer.nval;

			tokenizer.nextToken();
			int to = (int)tokenizer.nval;

			tokenizer.nextToken();
			int amount = (int)tokenizer.nval;

			// Use the from/to/amount
			buffer.putTransaction(new Transaction(from, to, amount));
			// put in queue
		}

		for (int i = 0; i < numWorkers; i++) {
			buffer.putTransaction(new TerminateTransaction());
		}
	}

	private class Worker implements Runnable{
		@Override
		public void run() {
			while(true){
				Transaction curr = buffer.takeTransaction();
				if(curr instanceof TerminateTransaction) {
					break;
				}

				buffer.processTransaction(curr);
			}

			latch.countDown();
		}
	}

	/*
	 Processes one file of transaction data
	 -fork off workers
	 -read file into the buffer
	 -wait for the workers to finish
	*/
	public void processFile(String file, int numWorkers) throws Exception {
		for (int i = 0; i < numWorkers; i++) {
			new Thread(new Worker()).start();
		}
		readFile(file);
		try {
			latch.await();
		} catch (InterruptedException ignored) {}
		System.out.println(buffer.toString());
	}



	/*
	 Looks at commandline args and calls Bank processing.
	*/
	public static void main(String[] args) throws Exception{
		// deal with command-lines args
		if (args.length == 0) {
			System.out.println("Args: transaction-file [num-workers [limit]]");
			return;
		}

		String file = args[0];
		int numWorkers = 1;
		if (args.length >= 2) {
			numWorkers = Integer.parseInt(args[1]);
		}

		Bank bank = new Bank(numWorkers);
		bank.processFile(file, numWorkers);

	}
}

