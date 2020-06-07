import javax.swing.*;
import java.util.concurrent.Semaphore;

public class WorkerLauncher extends Thread{
    private int maxNWorkers;
    private final int numUrls;
    private WebFrame frame;
    private Semaphore threadLock;
    private WebWorker[] workers;

    public WorkerLauncher(String numWorkers, WebFrame frame, int numUrls){
        this.frame = frame;
        try{
            maxNWorkers = Integer.parseInt(numWorkers);
        }catch(NumberFormatException exc){
            exc.printStackTrace();
        }
        this.numUrls = numUrls;
        threadLock = new Semaphore(maxNWorkers);
        workers = new WebWorker[numUrls];
    }

    @Override
    public void run() {
        frame.changeRunning(1);
        try {
            for (int i = 0; i < numUrls; i++) {
                if(isInterrupted()){
                    break;
                }
                threadLock.acquire();
                workers[i] = new WebWorker(frame.getUrl(i), i, frame, threadLock);
                workers[i].start();
            }
        } catch (InterruptedException ignored) {}
        frame.changeRunning(-1);
    }

    public void interruptAll(){
        for (int i = 0; i < workers.length; i++) {
            if(workers[i] != null){
                workers[i].interrupt();
            }
        }
    }
}
