package aurora.timer.client.view;

import aurora.timer.client.ServerURL;
import aurora.timer.client.view.until.AutoOpen;
//import org.omg.CORBA.FREE_MEM;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.prefs.Preferences;

/**
 * Created by hao on 17-4-27.
 */
public class SettingForm {
    private static JFrame FRAME;
    private JRadioButton autoOpenRadio;
    private JPanel parent;
    private JButton OkButton;
    private JButton CancelButton;
    private Preferences preferences;

    public SettingForm() {
        preferences = Preferences.userRoot().node(ServerURL.PRE_PATH);
        OkButton.setEnabled(false);
        autoOpenRadio.setSelected(preferences.getBoolean("autoOpen", false));
        CancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                FRAME.dispose();
            }
        });
        OkButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                preferences.putBoolean("autoOpen", autoOpenRadio.isSelected());
                if (autoOpenRadio.isSelected()) {
                    AutoOpen.autoOpen();
                } else {
                    AutoOpen.cancelAuto();
                }

                FRAME.dispose();
            }
        });
        autoOpenRadio.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (autoOpenRadio.isSelected() == preferences.getBoolean("autoOpen", false)) {
                    OkButton.setEnabled(false);
                } else {
                    OkButton.setEnabled(true);
                }
            }
        });
    }

    public static void main(String args[]) {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        try {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    FRAME = new JFrame("设置");
                    SettingForm settingForm = new SettingForm();

                    FRAME.setContentPane(settingForm.parent);
                    int width = 210;
                    int height = 150;
                    FRAME.setBounds((d.width - width) / 2, (d.height - height) / 2, width, height);
                    FRAME.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    FRAME.setResizable(false);
                    FRAME.setVisible(true);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
