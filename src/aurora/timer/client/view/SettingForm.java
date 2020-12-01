package aurora.timer.client.view;

import aurora.timer.client.ServerURL;
import aurora.timer.client.service.UserDataService;
import aurora.timer.client.view.until.CustomFileChooser;
import aurora.timer.client.vo.UserData;
//import org.omg.CORBA.FREE_MEM;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicPanelUI;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.prefs.Preferences;

/**
 * Created by hao on 17-4-27.
 */
public class SettingForm {
    private static JFrame FRAME;
    private JPanel parent;
    private JPanel Main2FormParent;
    private JButton Main2FormSettingButton;
    private JButton OkButton;
    private JButton CancelButton;
    private CustomFileChooser fileChooser;
    private JButton selectBgImgBtn;
    private JComboBox<String> imgComboBox;
    private final Preferences preferences = Preferences.userRoot().node(ServerURL.PRE_PATH);
    private String filePath;
    private UserData userData;

    public SettingForm(JPanel Main2FormParent, JButton Main2FormSettingButton, UserData userData) {
        this.Main2FormParent = Main2FormParent;
        this.userData = userData;
        this.Main2FormSettingButton = Main2FormSettingButton;
        initComboBox();
        this.filePath = preferences.get("bg", "res" + File.separator + "bg.png");
        setBgForThisParent(ServerURL.BG_PATH);
        CancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main2FormSettingButton.setEnabled(true);
                FRAME.dispose();
            }
        });
        OkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setBgForMain2FormParent(filePath);
                //TODO:上传图片到服务器
                try {
                    uploadBg();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                Main2FormSettingButton.setEnabled(true);
                FRAME.dispose();
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
                    if (imgName.equals("经典1")) {
                        filePath = "res" + File.separator + "bg.png";
                        setBgForThisParent(filePath);
                    } else if (imgName.equals("经典2")) {
                        filePath = "res" + File.separator + "bg4.png";
                        setBgForThisParent(filePath);
                    } else if (imgName.equals("经典3")) {
                        filePath = "res" + File.separator + "bg5.png";
                        setBgForThisParent(filePath);
                    }
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
                    g.drawImage(new ImageIcon(getClass().getResource("bg.png")).getImage(), 0, 0, c.getWidth(), c.getHeight(), null);
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
                ServerURL.BG_PATH = filePath;
                if (bg.exists()) {
                    g.drawImage(new ImageIcon(bg.getPath()).getImage(), 0, 0, c.getWidth(), c.getHeight(), null);
                    preferences.put("bg", filePath);
                } else {
                    g.drawImage(new ImageIcon(getClass().getResource("bg.png")).getImage(), 0, 0, c.getWidth(), c.getHeight(), null);
                    preferences.put("bg", "res" + File.separator + "bg.png");
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
        File file = new File(filePath);
        FileInputStream inputStream = new FileInputStream(file);
        UserDataService uds = new UserDataService();
        boolean flag = uds.uploadBg(userData.getID(), userData.getPassWord(), inputStream);
        if(!flag){
            JOptionPane.showMessageDialog(null, "上传背景图片到服务器失败，请检查网络或者服务器\n", "提示", JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void main(JPanel Main2FormParent, JButton Main2FormSettingButton, UserData userData) {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        try {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    FRAME = new MainFrame("设置");
                    SettingForm settingForm = new SettingForm(Main2FormParent, Main2FormSettingButton, userData);
                    FRAME.setContentPane(settingForm.parent);
                    FRAME.setBounds((d.width - FRAME.getWidth()) / 2, (d.height - FRAME.getHeight()) / 2, FRAME.getWidth(), FRAME.getHeight());
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
