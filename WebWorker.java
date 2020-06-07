import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Semaphore;
import javax.swing.*;
import java.time.*;

public class WebWorker extends Thread {
    private String url;
    private int row;
    private WebFrame frame;
    private Semaphore myLock;

    public WebWorker(String url, int row, WebFrame frame, Semaphore myLock){
        this.url = url;
        this.row = row;
        this.frame = frame;
        this.myLock = myLock;
    }

    @Override
    public void run() {
        frame.changeRunning(1);

        download();

        frame.changeRunning(-1);
        frame.incrementCompleted();
        myLock.release();
    }

    public void download() {
        InputStream input = null;
        StringBuilder contents = null;
        try {
            long start = System.currentTimeMillis();
            URL url = new URL(this.url);
            URLConnection connection = url.openConnection();

            // Set connect() to throw an IOException
            // if connection does not succeed in this many msecs.
            connection.setConnectTimeout(5000);

            connection.connect();
            input = connection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            char[] array = new char[1000];
            int len;
            contents = new StringBuilder(1000);
            while ((len = reader.read(array, 0, array.length)) > 0) {
                if(isInterrupted()){
                    frame.updateTable("Interrupted", row);
                    break;
                }
                contents.append(array, 0, len);
                Thread.sleep(100);
            }

            // Successful download if we get here
            long end = System.currentTimeMillis();
            String timeElapsed = " " + (end - start) + "ms ";
            frame.updateTable(LocalDate.now().toString() + timeElapsed + contents.length() + "bytes", row);
        }
        // Otherwise control jumps to a catch...
        catch (MalformedURLException ignored) {
            frame.updateTable("Malformed Url", row);
        } catch (InterruptedException exception) {
            // YOUR CODE HERE
            frame.updateTable("Interrupted", row);
            // deal with interruption
        } catch (IOException ignored) {
            if(isInterrupted()){
                frame.updateTable("Interrupted", row);
            }else{
                frame.updateTable("ERR", row);
            }
        }
        // "finally" clause, to close the input stream
        // in any case
        finally {
            try {
                if (input != null) input.close();
            } catch (IOException ignored) {
            }
        }

    }
}
