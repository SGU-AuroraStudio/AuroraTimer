package aurora.timer.client.view;

import javax.swing.*;
import javax.swing.plaf.basic.BasicPanelUI;
import java.awt.*;

/**
 * Created by hao on 17-2-22.
 */
public class MainParentPanelUI extends BasicPanelUI {
    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
        g.drawImage(new ImageIcon(getClass().getResource("bg.png")).getImage(), 0, 0, c.getWidth(), c.getHeight(), null);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(255, 255, 255, 128));
        g2.fillRect(0, 0, c.getWidth(), 44);
    }
}
