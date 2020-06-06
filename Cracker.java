// Cracker.java
/*
 Generates SHA hashes of short strings in parallel.
*/

import java.security.*;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class Cracker {
	// Array of chars used to produce strings
	public static final char[] CHARS = "abcdefghijklmnopqrstuvwxyz0123456789.,-!".toCharArray();
	private byte[] hash;
	private boolean DONE_FLAG;
	private CountDownLatch latch;
	//private MessageDigest md;

	public Cracker(){
		this("SHA");
	}
	public Cracker(String algorithm) {

	}

	public static String generateMode(String target){
		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			md.update(target.getBytes());
			byte[] bytes = md.digest();
			return hexToString(bytes);
		} catch (NoSuchAlgorithmException e) {
			System.out.println("No such algorithmic expression...");
			return "";
		}
	}

	public void crackMode(String target, int maxLength, int numWorkers){
		hash = hexToArray(target);
		latch = new CountDownLatch(numWorkers);
		int len = CHARS.length / numWorkers;
		int rem = CHARS.length % numWorkers;

		int start = 0;
		while(start < CHARS.length){
			int end = Math.min(start + len, CHARS.length);
			new Thread(new Worker(start, end, maxLength)).start();
			start = end;
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("All Done!");
	}

	private class Worker implements Runnable{
		private int from, to, len;

		public Worker(int from,int to,int len){
			this.from = from;
			this.to = to;
			this.len = len;
			System.out.println(from + " " + to);
		}

		@Override
		public void run() {
			for (int i = from; i < to; i++) {
				String str = CHARS[i] + "";
				recRun(str, 1);
			}
			latch.countDown();
		}

		private void recRun(String str, int currLen) {
			if(currLen > len)
				return;

			if(isPassword(str))
				System.out.println(str);

			for (int i = 0; i < CHARS.length; i++) {
				recRun(str + CHARS[i], currLen + 1);
			}
		}

		private boolean isPassword(String pass) {
		//	byte[] res;
		//	try{
//				md.update(pass.getBytes());
//				res = md.digest();
				return generateMode(pass).equals(hexToString(hash));
		//	}catch (ArrayIndexOutOfBoundsException e){
		//		System.out.println("Demerxa am stringze: " + pass);
		//		return false;
		//	}
			//return Arrays.equals(res, hash);
		}
	}
	
	/*
	 Given a byte[] array, produces a hex String,
	 such as "234a6f". with 2 chars for each byte in the array.
	 (provided code)
	*/
	public static String hexToString(byte[] bytes) {
		StringBuffer buff = new StringBuffer();
		for (int i=0; i<bytes.length; i++) {
			int val = bytes[i];
			val = val & 0xff;  // remove higher bits, sign
			if (val<16) buff.append('0'); // leading 0
			buff.append(Integer.toString(val, 16));
		}
		return buff.toString();
	}
	
	/*
	 Given a string of hex byte values such as "24a26f", creates
	 a byte[] array of those values, one byte value -128..127
	 for each 2 chars.
	 (provided code)
	*/
	public static byte[] hexToArray(String hex) {
		byte[] result = new byte[hex.length()/2];
		for (int i=0; i<hex.length(); i+=2) {
			result[i/2] = (byte) Integer.parseInt(hex.substring(i, i+2), 16);
		}
		return result;
	}
	
	
	
	public static void main(String[] args) {
		if(args.length == 0){
			throw new RuntimeException("No given arguments");
		}
		Cracker cracker = new Cracker();
		String target = args[0];
		if(args.length == 1){
			System.out.println(cracker.generateMode(target));
		}else{
			int lengths = Integer.parseInt(args[1]);
			int numWorkers = Integer.parseInt(args[2]);
			cracker.crackMode(target, lengths, numWorkers);
		}
		// a! 34800e15707fae815d7c90d49de44aca97e2d759
		// xyz 66b27417d37e024c46526c2f6d358a754fc552f3
		
		// YOUR CODE HERE
	}
}
