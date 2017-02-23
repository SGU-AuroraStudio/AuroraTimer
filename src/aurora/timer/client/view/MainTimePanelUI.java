package aurora.timer.client.view;

import javax.swing.*;
import javax.swing.plaf.basic.BasicPanelUI;
import java.awt.*;

/**
 * Created by hao on 17-2-22.
 */
public class MainTimePanelUI extends BasicPanelUI {
    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
        c.setSize(565, 780);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(30, 40, 50, 153));
        g2.fillOval(c.getWidth()/2 - 242, 38, 484, 484);

        g2.setColor(new Color(12, 96, 108, 153));
        g2.setStroke(new BasicStroke(26));
        g2.drawArc(c.getWidth()/2 - 187, 86, 375, 375, 0, 360);

        g2.setColor(new Color(83, 235, 228, 178));
        g2.fillOval(c.getWidth()/2 - 59, 577, 112,112);

//        g2.drawImage()
//        g2.setColor(new Color(88, 222, 234, 200));
//        g2.drawArc(95, 86, 375, 375, 90, -60);
    }
}
