import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class WebFrame extends JFrame {
    private String document;
    private JButton single;
    private JButton concurrent;
    private JTextField field;
    private JLabel running;
    private JLabel completed;
    private JLabel elapsed;
    private JProgressBar progressBar;
    private JButton stop;
    private DefaultTableModel model;
    private int numRunning;
    private int numCompleted;
    private long elapsedTime;
    private WorkerLauncher launcher;

    public WebFrame(String document){
        super("Web Worker");
        this.document = document;
        resetValues();
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

        add(buildTablePanel());
        add(buildButtonPanel());
        fillTable();
        addListeners();
    }

    private void addListeners() {
        single.addActionListener(l ->{
            resetValues();
            clearTable();
            setRunningState();
            launcher = new WorkerLauncher("1", this, model.getRowCount());
            launcher.start();
        });
        concurrent.addActionListener(l ->{
            resetValues();
            clearTable();
            setRunningState();
            launcher = new WorkerLauncher(field.getText(), this, model.getRowCount());
            launcher.start();
        });
        stop.addActionListener(l ->{
            setDisabledState();
            launcher.interrupt();
            launcher.interruptAll();
        });
    }

    private void clearTable() {
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt("", i, 1);
        }
    }

    private void resetValues() {
        numRunning = 0;
        numCompleted = 0;
        elapsedTime = 0;
        if(progressBar != null)
            progressBar.setValue(0);
    }

    private void setDisabledState() {
        single.setEnabled(true);
        concurrent.setEnabled(true);
        stop.setEnabled(false);
    }

    private void setRunningState() {
        elapsedTime = System.currentTimeMillis();
        single.setEnabled(false);
        concurrent.setEnabled(false);
        stop.setEnabled(true);
    }

    private void fillTable() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(document));
            String line = reader.readLine();
            while(line != null){
                model.addRow(new String[]{line, ""});
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        progressBar.setMaximum(model.getRowCount());
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        single = new JButton("Single Thread Fetch");
        concurrent = new JButton("Concurrent Fetch");
        field = new JTextField();
        field.setMaximumSize(new Dimension(80, 30));
        running = new JLabel("Running: ");
        completed = new JLabel("Completed: ");
        elapsed = new JLabel("Elapsed: ");
        progressBar = new JProgressBar(0);
        stop = new JButton("Stop");

        addButtons(panel);
        return panel;
    }

    private void addButtons(JPanel panel) {
        panel.add(single);
        panel.add(Box.createRigidArea(new Dimension(20, 10)));

        panel.add(concurrent);
        panel.add(Box.createRigidArea(new Dimension(20, 5)));

        panel.add(field);

        panel.add(running);
        panel.add(completed);
        panel.add(elapsed);
        panel.add(Box.createRigidArea(new Dimension(20, 5)));

        panel.add(progressBar);
        panel.add(Box.createRigidArea(new Dimension(20, 5)));

        stop.setEnabled(false);
        panel.add(stop);
        panel.add(Box.createRigidArea(new Dimension(20, 5)));

    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel();
        model = new DefaultTableModel(new String[] {"URL", "Status"}, 0);
        JTable table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(600,300));
        panel.add(scrollPane);
        return panel;
    }

    public String getUrl(int row){
        return (String)model.getValueAt(row, 0);
    }
    public static void main(String[] args) {
        WebFrame frame = new WebFrame("links.txt");

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public synchronized void changeRunning(int val) {
        numRunning += val;
        SwingUtilities.invokeLater(() -> running.setText("Running: " + numRunning));
        if(numRunning == 0){
            setDisabledState();
        }
    }

    public void updateTable(String value, int row) {
        model.setValueAt(value, row, 1);
    }

    public synchronized void incrementCompleted() {
        numCompleted++;
        SwingUtilities.invokeLater(() -> completed.setText("Completed: " + numCompleted));
        SwingUtilities.invokeLater(() -> progressBar.setValue(numCompleted));
        if(numCompleted == model.getRowCount()){
            elapsed.setText("Elapsed: " + (System.currentTimeMillis() - elapsedTime) + "ms");
        }
    }
}
