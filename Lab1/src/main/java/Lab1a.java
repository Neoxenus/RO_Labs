import javax.swing.*;
import java.awt.*;

public class Lab1a {
    private final static int FRAME_WIDTH = 600;
    private final static int FRAME_HEIGHT = 800;
    private final static int SLIDER_MIN = 0;
    private final static int SLIDER_MAX = 100;
    private final static int SLIDER_INIT = 50;

    private final static JSlider slider;
    private static JSpinner spinner1;
    private static JSpinner spinner2;
    private static Thread th1;
    private static Thread th2;

    static {
        slider = new JSlider(JSlider.HORIZONTAL, SLIDER_MIN, SLIDER_MAX, SLIDER_INIT);
    }
    private static void graphic(){
        JFrame frame = new JFrame();
        GridBagConstraints constraints = new GridBagConstraints();
        JPanel panel = new JPanel(new GridBagLayout());

        slider.setMajorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);

        SpinnerModel sm1 = new SpinnerNumberModel(1, 1, 10, 1);
        SpinnerModel sm2 = new SpinnerNumberModel(1, 1, 10, 1);
        spinner1 = new JSpinner(sm1);
        spinner2 = new JSpinner(sm2);

        JButton button = new JButton("Start!");
        button.addActionListener(e -> {
            if(!th1.isAlive() && !th2.isAlive()){
                th1.start();
                th2.start();
            }
        });

        position(constraints, 50, 50, 0, 1);
        constraints.insets = new Insets(0,20,0,20);
        panel.add(spinner1, constraints);
        position(constraints, 50, 50, 1, 1);
        constraints.insets = new Insets(0,20,0,20);
        panel.add(spinner2, constraints);
        position(constraints, 0, 0, 0, 0);
        constraints.gridwidth = 2;
        panel.add(slider, constraints);
        position(constraints, 100, 50, 0, 3);
        constraints.gridwidth = 2;
        constraints.insets = new Insets(50,0,FRAME_HEIGHT - 350,0);
        panel.add(button, constraints);

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

        th1 = new Thread(() -> {
            while (true) {
                synchronized (slider) {
                    slider.setValue(slider.getValue() - (slider.getValue()>10?1:0));
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    th1.setPriority((int)spinner1.getValue());
                }
            }
        });

        th2 = new Thread(() -> {
            while (true) {
                synchronized (slider) {
                    slider.setValue(slider.getValue() + (slider.getValue()<90?1:0));
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    th2.setPriority((int)spinner2.getValue());
                }
            }
        });
    }
}