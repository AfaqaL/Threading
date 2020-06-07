import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

class CrackerTest {
    @Test
    public void testHashGeneration(){
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(outStream);

        PrintStream old = System.out;
        System.setOut(ps);

        Cracker.main(new String[]{"a!"});

        String res = outStream.toString();
        assertTrue(res.contains("34800e15707fae815d7c90d49de44aca97e2d759"));

        System.out.flush();
        System.setOut(old);
    }

    @Test
    public void testHashCracking(){
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(outStream);

        PrintStream old = System.out;
        System.setOut(ps);

        Cracker.main(new String[]{"34800e15707fae815d7c90d49de44aca97e2d759", "4", "6"});

        String res = outStream.toString();
        assertTrue(res.contains("a!"));

        System.out.flush();
        System.setOut(old);
    }

    @Test
    public void emptyMain() {
        Exception exc = assertThrows(RuntimeException.class, () -> Cracker.main(new String[0]));
        assertTrue(exc.getMessage().contains("No given arguments"));
    }
}