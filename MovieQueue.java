import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MovieQueue {
    private static final int TICKET_PROCESSING_TIME = 2;

    private List<Queue<String>> queues;
    private Timer timer;

    private JFrame frame;
    private List<JTextArea> queueTextAreas;

    public MovieQueue(int numQueues, int numPersons) {
        queues = new ArrayList<>(numQueues);
        queueTextAreas = new ArrayList<>(numQueues);

        // Create N queues
        for (int i = 0; i < numQueues; i++) {
            queues.add(new LinkedList<>());
        }

        // Enqueue persons into each queue
        for (int i = 0; i < numPersons; i++) {
            for (int j = numQueues - 1; j >= 0; j--) {
                queues.get(j).add("Person " + i);
            }
        }

        timer = new Timer(TICKET_PROCESSING_TIME * 1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processNextPerson();

                // Check if all queues are empty or all persons are processed
                if (isSimulationComplete()) {
                    timer.stop();
                }
            }
        });
    }

    private void processNextPerson() {
        // Process the person in the first queue
        Queue<String> firstQueue = queues.get(0);
        if (!firstQueue.isEmpty()) {
            firstQueue.remove();
        }

        // Move the persons from the last sub-queue to the first sub-queue (queue n to queue 0)
        for (int i = queues.size() - 1; i >= 1; i--) {
            Queue<String> currentQueue = queues.get(i);
            Queue<String> previousQueue = queues.get(i - 1);
            if (!currentQueue.isEmpty()) {
                previousQueue.add(currentQueue.remove());
            }
        }

        updateGUI();
    }

    private boolean isSimulationComplete() {
        for (Queue<String> queue : queues) {
            if (!queue.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void updateGUI() {
        for (int i = 0; i < queueTextAreas.size(); i++) {
            JTextArea textArea = queueTextAreas.get(i);
            StringBuilder sb = new StringBuilder("Queue " + i + ":\n");
            Queue<String> queue = queues.get(i);
            for (String person : queue) {
                sb.append(person).append("\n");
            }
            textArea.setText(sb.toString());
        }
    }

    private void createAndShowGUI() {
        frame = new JFrame("Movie Queue Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(1, queues.size()));
    
        for (int i = 0; i < queues.size(); i++) {
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
    
            JTextArea textArea = new JTextArea();
            textArea.setEditable(false);
            textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            queueTextAreas.add(textArea);
    
            if (i == 0) {
                JLabel titleLabel = new JLabel("Ticket Collector");
                titleLabel.setHorizontalAlignment(JLabel.CENTER);
                titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
                panel.add(titleLabel, BorderLayout.NORTH);
            }
    
            JScrollPane scrollPane = new JScrollPane(textArea);
            panel.add(scrollPane, BorderLayout.CENTER);
            frame.add(panel);
        }
    
        JButton startButton = new JButton("Start Simulation");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startSimulation();
            }
        });
    
        frame.add(startButton);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    

    private void startSimulation() {
        timer.start();
    }

    public static void main(String[] args) {
        int numQueues = Integer.parseInt(JOptionPane.showInputDialog("Enter the number of queues:"));
        int numPersons = Integer.parseInt(JOptionPane.showInputDialog("Enter the number of persons:"));

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MovieQueue simulation = new MovieQueue(numQueues, numPersons);
                simulation.createAndShowGUI();
            }
        });
    }
}