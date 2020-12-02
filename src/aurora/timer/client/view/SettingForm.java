package aurora.timer.client.view;

import aurora.timer.client.ServerURL;
import aurora.timer.client.service.UserDataService;
import aurora.timer.client.view.until.CustomFileChooser;
import aurora.timer.client.view.until.SaveBg;
import aurora.timer.client.vo.UserData;
//import org.omg.CORBA.FREE_MEM;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicPanelUI;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 * Created by hao on 17-4-27.
 */
public class SettingForm {
    public static JFrame FRAME;
    public JPanel parent;
    public JButton OkButton;
    public JButton CancelButton;
    public CustomFileChooser fileChooser;
    public JButton selectBgImgBtn;
    public JComboBox<String> imgComboBox;
    public final Preferences preferences = Preferences.userRoot().node(ServerURL.PRE_PATH);
    public String filePath;
    public UserData userData;
    private JPanel Main2BeforeInComponent;
    private JPanel Main2FormParent;
    private JButton Main2FormSettingButton;
    Logger logger = Logger.getLogger("SETTING");

    public SettingForm(JPanel Main2FormParent, JButton Main2FormSettingButton, UserData userData) {
        this.Main2FormParent = Main2FormParent;
        this.userData = userData;
        this.Main2FormSettingButton = Main2FormSettingButton;
        initComboBox();
        this.filePath = preferences.get("bg", "");
        setBgForThisParent(filePath);
        CancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main2FormSettingButton.setEnabled(true);
                Main2BeforeInComponent.setVisible(true);
                Main2FormParent.remove(parent);
                Main2FormParent.repaint();
            }
        });
        OkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setBgForMain2FormParent(filePath);
                preferences.put("bg",filePath);
                //TODO:上传图片到服务器
                try {
                    uploadBg();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                Main2FormSettingButton.setEnabled(true);
                Main2BeforeInComponent.setVisible(true);
                Main2FormParent.remove(parent);
                Main2FormParent.repaint();
            }
        });
        selectBgImgBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser = new CustomFileChooser();
                fileChooser.setMultiSelectionEnabled(false);
                FileFilter filter = new FileNameExtensionFilter("图片(PNG,JPG,JPEG)", "png", "jpg", "jpeg");
                fileChooser.setFileFilter(filter);
                if (fileChooser.showOpenDialog(selectBgImgBtn) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    // 文件不合格
                    if (file.length() >= (1024L * 1024L * 15)) {
                        JOptionPane.showMessageDialog(null, "请选择15M以下的文件", "错误", JOptionPane.ERROR_MESSAGE);
                    } else {
                        filePath = file.getAbsolutePath();
                        // 预览背景图
                        setBgForThisParent(filePath);
                    }
                }
            }
        });
    }

    private void initComboBox() {
        imgComboBox.addItem("");
        imgComboBox.addItem("经典1");
        imgComboBox.addItem("经典2");
        imgComboBox.addItem("经典3");
        imgComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String imgName = (String) imgComboBox.getSelectedItem();
                    InputStream bg = null;
                    String bgPath = null;
                    if (imgName.equals("经典1")) {
                        bg = getClass().getResourceAsStream("bg1.png");
                        bgPath = System.getProperty("java.io.tmpdir") + File.separator + "AuroraTimer_bg1.png";
                    } else if (imgName.equals("经典2")) {
                        bg = getClass().getResourceAsStream("bg2.png");
                        bgPath = System.getProperty("java.io.tmpdir") + File.separator + "AuroraTimer_bg2.png";
                    } else if (imgName.equals("经典3")) {
                        bg = getClass().getResourceAsStream("bg3.png");
                        bgPath = System.getProperty("java.io.tmpdir") + File.separator + "AuroraTimer_bg3.png";
                    } else if (imgName.equals("")){
                        bgPath = System.getProperty("java.io.tmpdir") + File.separator + userData.getID() + "_bg.png";
                    }
                    if (!imgName.equals("")) {
                        try {
                            if (SaveBg.saveBg(bgPath, bg, false))
                                filePath = bgPath;
                            else
                                logger.warning("保存背景图失败");
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                            logger.warning("保存背景图错误！");
                        }
                    }
                    setBgForThisParent(filePath);
                }
            }
        });
    }

    private void setBgForThisParent(String filePath) {
        parent.setUI(new BasicPanelUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                super.paint(g, c);
                File bg = new File(filePath);
                if (bg.exists()) {
                    g.drawImage(new ImageIcon(bg.getPath()).getImage(), 0, 0, c.getWidth(), c.getHeight(), null);
                } else {
                    g.drawImage(new ImageIcon(getClass().getResource("bg1.png")).getImage(), 0, 0, c.getWidth(), c.getHeight(), null);
                }
            }
        });
    }

    private void setBgForMain2FormParent(String filePath) {
        Main2FormParent.setUI(new BasicPanelUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                super.paint(g, c);
                File bg = new File(filePath);
                if (bg.exists()) {
                    g.drawImage(new ImageIcon(bg.getPath()).getImage(), 0, 0, c.getWidth(), c.getHeight(), null);
                    preferences.put("bg", filePath);
//                    ServerURL.BG_PATH = filePath;
                } else {
                    g.drawImage(new ImageIcon(getClass().getResource("bg1.png")).getImage(), 0, 0, c.getWidth(), c.getHeight(), null);
                }
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(255, 255, 255, 200));
                g2.fillRect(0, 0, c.getWidth(), 37);
                g2.setColor(new Color(50, 50, 50, 200));
                g2.drawRect(0, 0, c.getWidth() - 1, c.getHeight() - 1);
            }
        });
    }

    public void uploadBg() throws IOException {
        String bgPath = System.getProperty("java.io.tmpdir") + File.separator + userData.getID() + "_bg.png";
        File file = new File(filePath);
        FileInputStream bg = new FileInputStream(file);
        FileInputStream bg1 = new FileInputStream(file);
        UserDataService uds = new UserDataService();
        // 上传的同时保存到临时文件夹,注意：inputstream用一次就没了！
        if (SaveBg.saveBg(bgPath, bg1, true)) ;
        preferences.put("bg", bgPath);
        boolean flag = uds.uploadBg(userData.getID(), userData.getPassWord(), bg);
        if (!flag) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(null, "上传背景图片到服务器失败，请检查网络或者服务器\n", "提示", JOptionPane.ERROR_MESSAGE);
                }
            });
        }
    }

    public void setMain2BeforeInComponent(JPanel Main2BeforeInComponent) {
        this.Main2BeforeInComponent = Main2BeforeInComponent;
    }

    public static void main(JFrame Main2FRAME, JPanel Main2FormParent, JButton Main2FormSettingButton, UserData userData) {
//        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        Point d = Main2FRAME.getLocation();
        try {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    FRAME = new MainFrame("设置");
                    SettingForm settingForm = new SettingForm(Main2FormParent, Main2FormSettingButton, userData);
                    FRAME.setContentPane(settingForm.parent);
//                    FRAME.setBounds((d.width - FRAME.getWidth()) / 2, (d.height - FRAME.getHeight()) / 2, FRAME.getWidth(), FRAME.getHeight());
                    FRAME.setLocation((int) d.getX(), (int) d.getY());
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
