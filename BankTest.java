import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.StringTokenizer;

import static org.junit.jupiter.api.Assertions.*;

class BankTest {

    @Test
    public void testMain() throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(outStream);

        PrintStream old = System.out;
        System.setOut(ps);
        Bank.main(new String[]{"small.txt"});

        String res = outStream.toString();

        System.out.flush();
        System.setOut(old);

        StringTokenizer tokenizer = new StringTokenizer(res, "\n\r");
        int rem = 0;
        while(tokenizer.hasMoreTokens()){
            String line = tokenizer.nextToken();
            assertTrue(line.contains( (rem % 2 == 0 ? "999" : "1001") ));
            rem++;
        }
    }
    @Test
    public void testMainThreaded() throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(outStream);

        PrintStream old = System.out;
        System.setOut(ps);
        Bank.main(new String[]{"5k.txt", "8"});

        String res = outStream.toString();

        System.out.flush();
        System.setOut(old);

        StringTokenizer tokenizer = new StringTokenizer(res, "\n\r");
        while(tokenizer.hasMoreTokens()){
            String line = tokenizer.nextToken();
            assertTrue(line.contains("1000"));
        }
    }

    @Test
    public void emptyMain() throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(outStream);

        PrintStream old = System.out;
        System.setOut(ps);
        Bank.main(new String[0]);

        String res = outStream.toString();

        System.out.flush();
        System.setOut(old);

        assertTrue(res.contains("Args: transaction-"));
    }
}