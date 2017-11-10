package aurora.timer.client.view;

import javax.swing.*;
import java.awt.*;

/**
 * Created by hao on 17-2-22.
 */
public class MainFrame extends JFrame{
    @Override
    protected void frameInit() {
        super.frameInit();
        this.setSize(565, 780);
        this.setUndecorated(true);
        this.setBackground(new Color(255,255,255));
    }

    public MainFrame() throws HeadlessException {
        super();
    }

    public MainFrame(String title) throws HeadlessException {
        super(title);
    }
}
