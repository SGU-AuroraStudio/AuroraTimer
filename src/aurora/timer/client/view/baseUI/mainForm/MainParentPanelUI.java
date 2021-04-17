package aurora.timer.client.view.baseUI.mainForm;

import aurora.timer.client.vo.base.Constants;

import javax.swing.*;
import javax.swing.plaf.basic.BasicPanelUI;
import java.awt.*;
import java.io.File;

/**
 * Created by hao on 17-2-22.
 */
public class MainParentPanelUI extends BasicPanelUI {

    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
        // 优先从注册表里读取，没有就设置为默认。（从服务器读取到到会改注册表）
        File bg = new File(Constants.preferences.get("bg", ""));
        if (bg.exists() && bg.length() > 100) {
            g.drawImage(new ImageIcon(bg.getPath()).getImage(), 0, 0, c.getWidth(), c.getHeight(), null);
        } else {
            g.drawImage(new ImageIcon(getClass().getResource("aurora/timer/img/bg/bg1.png")).getImage(), 0, 0, c.getWidth(), c.getHeight(), null);
        }
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(255, 255, 255, 200));
        //头部
        g2.fillRect(0, 0, c.getWidth(), 40);
        g2.setColor(new Color(50, 50, 50, 200));
        g2.drawRect(0, 0, c.getWidth() - 1, c.getHeight() - 1);
    }
//    @Override
//    public void update(Graphics g, JComponent c) {
//        super.update(g, c);
//        File bg = new File(ServerURL.BG_PATH);
//        if (bg.exists()) {
//            g.drawImage(new ImageIcon(bg.getPath()).getImage(), 0, 0, c.getWidth(), c.getHeight(), null);
//        } else {
//            g.drawImage(new ImageIcon(getClass().getResource("bg1.png")).getImage(), 0, 0, c.getWidth(), c.getHeight(), null);
//        }
//        Graphics2D g2 = (Graphics2D) g;
//        g2.setColor(new Color(255, 255, 255, 200));
//        g2.fillRect(0, 0, c.getWidth(), 37);
//        g2.setColor(new Color(50, 50, 50, 200));
//        g2.drawRect(0, 0, c.getWidth()-1, c.getHeight()-1);
//    }
}
