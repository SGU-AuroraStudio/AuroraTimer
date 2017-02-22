package aurora.timer.client.view;

import aurora.timer.client.service.UserDataService;
import aurora.timer.client.service.UserOnlineTimeService;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by hao on 17-2-22.
 */
public class Main2Form {
    private static MainFrame FRAME;
    private JPanel parent;
    private JButton minButton;
    private JButton outButton;
    private JPanel timePanel;
    private JPanel headPanel;
    private TrayIcon trayIcon;
    private SystemTray systemTray;
    int mx, my, jfx, jfy;

    public void init() {
        parent.setUI(new MainParentPanelUI());
        minButton.setUI(new LoginButtonUI());
        outButton.setUI(new LoginButtonUI());
        timePanel.setUI(new MainTimePanelUI());
    }

    public Main2Form() {
        init();
        minButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                loadSystemTray();
                FRAME.setVisible(false);
            }
        });
        outButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(1);
            }
        });
        timePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mx = e.getX();
                my = e.getY();
                jfx = parent.getX();
                jfy = parent.getY();
                System.out.println("22");
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                //TODO:This is a question
            }
        });
    }

    public void loadSystemTray() {
        if (!SystemTray.isSupported()) {
            return;
        }
        systemTray = SystemTray.getSystemTray();
        PopupMenu popupMenu = new PopupMenu();
        MenuItem restoreItem = new MenuItem("还原");
        MenuItem exitItem  = new MenuItem("退出");

        restoreItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!FRAME.isVisible()) {
                    FRAME.setVisible(true);
                    systemTray.remove(trayIcon);
                }
            }
        });
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        popupMenu.add(restoreItem);
        popupMenu.addSeparator();
        popupMenu.add(exitItem);

        try {
            trayIcon = new TrayIcon(ImageIO.read(getClass().getResource("trayIcon.png")));
            trayIcon.setPopupMenu(popupMenu);
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getButton()==MouseEvent.BUTTON1 && !FRAME.isVisible()) {
                        FRAME.setVisible(true);
                        systemTray.remove(trayIcon);
                    }
                }
            });
            systemTray.add(trayIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        try {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    FRAME = new MainFrame("Hello, ");
                    Main2Form main2Form = new Main2Form();
                    FRAME.setContentPane(main2Form.parent);
                    FRAME.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                    FRAME.setLocation((d.width - FRAME.getWidth()) / 2, (d.height - FRAME.getHeight()) / 2);
//                    FRAME.setBounds((d.width - FRAME.getWidth()) / 2, (d.height - FRAME.getHeight()) / 2, FRAME.getWidth(), FRAME.getHeight());
                    FRAME.setResizable(false);
                    FRAME.setVisible(true);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
