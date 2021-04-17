package aurora.timer.client.view.baseUI.login;

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
        super.paint(g);
    }

    @Override
    protected void frameInit() {
        super.frameInit();
        this.setUndecorated(true);
    }

    public LoginFrame(String title) throws HeadlessException {
        super(title);
    }

}
