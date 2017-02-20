package aurora.timer.client.view;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

/**
 * Created by hao on 17-2-20.
 */
public class LoginButtonUI extends BasicButtonUI {
    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
    }

    @Override
    protected void installDefaults(AbstractButton b) {
        super.installDefaults(b);
//        LookAndFeel.installProperty(b, "opaque", Boolean.TRUE);
    }

    @Override
    protected void paintButtonPressed(Graphics g, AbstractButton b) {
        super.paintButtonPressed(g, b);
    }
}
