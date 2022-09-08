import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Lab1b {
    private final static int FRAME_WIDTH = 600;
    private final static int FRAME_HEIGHT = 800;
    private final static int SLIDER_MIN = 0;
    private final static int SLIDER_MAX = 100;
    private final static int SLIDER_INIT = 50;

    private final static JSlider slider;
    private static final AtomicInteger semaphore;

    static {
        slider = new JSlider(JSlider.HORIZONTAL, SLIDER_MIN, SLIDER_MAX, SLIDER_INIT);
        semaphore = new AtomicInteger(0);
    }
    private static void graphic(){
        JFrame frame = new JFrame();
        GridBagConstraints constraints = new GridBagConstraints();
        JPanel panel = new JPanel(new GridBagLayout());

        slider.setMajorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);

        JLabel label = new JLabel("Not blocked by any threads");

        JButton buttonStart1 = new JButton("Start 1");
        buttonStart1.addActionListener(e -> {
            if(semaphore.get() == 0) {
                semaphore.set(1);
                label.setText("Blocked by thread 1");
            }
        });
        JButton buttonStart2 = new JButton("Start 2");
        buttonStart2.addActionListener(e -> {
            if(semaphore.get() == 0) {
                semaphore.set(2);
                label.setText("Blocked by thread 2");
            }
        });
        JButton buttonStop1 = new JButton("Stop 1");
        buttonStop1.addActionListener(e -> {
            if(semaphore.get() == 1) {
                semaphore.set(0);
                label.setText("Not blocked by any threads");
            }
        });
        JButton buttonStop2 = new JButton("Stop 2");
        buttonStop2.addActionListener(e -> {
            if(semaphore.get() == 2) {
                semaphore.set(0);
                label.setText("Not blocked by any threads");
            }
        });

        position(constraints, 50, 25, 0, 3);
        constraints.insets = new Insets(25,0,0,50);
        panel.add(buttonStart1, constraints);
        position(constraints, 50, 25, 1, 3);
        constraints.insets = new Insets(25,0,0,0);
        panel.add(buttonStart2, constraints);
        position(constraints, 50, 25, 0, 4);
        constraints.insets = new Insets(10,0,0,50);
        panel.add(buttonStop1, constraints);
        position(constraints, 50, 25, 1, 4);
        constraints.insets = new Insets(10,0,0,0);
        panel.add(buttonStop2, constraints);

        position(constraints, 0, 0, 0, 0);
        constraints.gridwidth = 2;
        constraints.insets = new Insets(0,0,0,0);
        panel.add(slider, constraints);

        position(constraints, 0, 0, 0, 5);
        constraints.gridwidth = 2;
        constraints.insets = new Insets(20,0,FRAME_HEIGHT - 500,0);
        panel.add(label, constraints);

        frame.setContentPane(panel);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    private static void position(GridBagConstraints constraints, int ipadx, int ipady, int gridx, int gridy){
        constraints.ipadx = ipadx;
        constraints.ipady = ipady;
        constraints.gridx = gridx;
        constraints.gridy = gridy;
    }

    public static void main(String[] args) {
        graphic();

        Thread th1 = new Thread(() -> {
            while (true) {
                synchronized (slider) {
                    if (semaphore.get() == 1) {
                        slider.setValue(slider.getValue() - (slider.getValue() > 10 ? 1 : 0));
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }
            }
        });

        Thread th2 = new Thread(() -> {
            while (true) {
                synchronized (slider) {
                    if (semaphore.get() == 2) {
                        slider.setValue(slider.getValue() + (slider.getValue() < 90 ? 1 : 0));
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }
            }
        });

        th1.setPriority(1);
        th2.setPriority(10);
        th1.start();
        th2.start();
    }
}