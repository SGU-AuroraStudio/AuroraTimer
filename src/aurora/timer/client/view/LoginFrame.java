package aurora.timer.client.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by hao on 17-2-19.
 */
public class LoginFrame extends JFrame {
    public LoginFrame() throws HeadlessException {
        super();
    }

    @Override
    public void paint(Graphics g) {
//        g.drawImage(new ImageIcon(getClass().getResource("login.png")).getImage(), 0, 0, 270, 190, this);
        super.paint(g);
    }

    @Override
    protected void frameInit() {
        super.frameInit();
        this.setUndecorated(true);
        this.setBackground(new Color(0,0,0,0));
    }

    public LoginFrame(String title) throws HeadlessException {
        super(title);
    }

}
