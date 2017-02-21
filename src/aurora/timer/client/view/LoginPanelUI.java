package aurora.timer.client.view;

import javax.swing.*;
import javax.swing.plaf.basic.BasicPanelUI;
import java.awt.*;

/**
 * Created by hao on 17-2-20.
 */
public class LoginPanelUI extends BasicPanelUI {
    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
        g.drawImage(new ImageIcon(getClass().getResource("login.png")).getImage(), 0, 0, 270, 190, null);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(152, 152, 152));
        g2.drawRect(0, 0, 269, 189);
    }
}
