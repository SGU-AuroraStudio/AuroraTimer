package aurora.timer.client.view;

import javax.swing.*;
import java.awt.*;

/**
 * Created by hao on 17-1-26.
 */
public class MainForm {
    private JPanel parent;
    private JButton button1;

    public static void main(String[] args) {
        JFrame frame = new JFrame("MainForm");
        frame.setContentPane(new MainForm().parent);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
