package aurora.timer.client.view;

import aurora.timer.client.vo.base.Constants;
import aurora.timer.client.vo.base.ServerURL;
import aurora.timer.client.service.UserDataService;
import aurora.timer.client.view.util.CustomFileChooser;
import aurora.timer.client.view.util.SaveBg;
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
    public JPanel settingPanel;
    public JButton okButton;
    public JButton cancelButton;
    public CustomFileChooser fileChooser;
    public JButton selectBgImgBtn;
    public JComboBox<String> imgComboBox;
    public final Preferences preferences = Preferences.userRoot().node(ServerURL.PRE_PATH);
    public String localBgPath;
    public String tempFilePath;
    public UserData userData;
    private final JPanel Main2FormParent;
    Logger logger = Logger.getLogger("SETTING");

    //TODO:修改用户信息
    public SettingForm(JPanel Main2FormParent,JPanel Main2FormCardPanel, UserData userData) {
        this.Main2FormParent = Main2FormParent;
        this.userData = userData;
        initComboBox();
        this.localBgPath = preferences.get("bg", "");
        this.tempFilePath = localBgPath;
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(tempFilePath.equals(localBgPath))
                    return;
                setBgForMain2FormParent(tempFilePath);
                preferences.put("bg",tempFilePath);
                localBgPath = tempFilePath;
                //如果是默认图片，或者是服务器下载的背景图片，就不上传了。
                //但是如果是默认图片，依然会和请求服务器，作用是修改服务器里保存的url
                if(localBgPath.contains(userData.getID()+"_bg")) {
                    Main2Form.cardLayout.show(Main2FormCardPanel, "weekInfoPanel");
                    return;
                }
                //上传filePath到服务器，多线程防止上传慢按钮卡死不动。
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(uploadBg(localBgPath)){
                                //如果不是默认图片，下拉框加载“我的自定义”
                                if(!localBgPath.contains("AuroraTimer_bg")) {
                                    imgComboBox.addItem("我的自定义");
                                    imgComboBox.setSelectedItem("我的自定义");
                                }
                                else {
                                    imgComboBox.removeItem("我的自定义");
                                }
                                logger.info("上传背景图片成功");
                                JOptionPane.showMessageDialog(FRAME, "上传成功\n", "提示", JOptionPane.INFORMATION_MESSAGE);
                            }else {
                                logger.warning("上传背景图片失败");
                                JOptionPane.showMessageDialog(FRAME, "上传失败\n", "提示", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                }).start();
                Main2Form.cardLayout.show(Main2FormCardPanel, "weekInfoPanel");
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imgComboBox.setSelectedIndex(0);
                setBgForMain2FormParent(localBgPath);
                Main2Form.cardLayout.show(Main2FormCardPanel, "weekInfoPanel");
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
                        tempFilePath = file.getAbsolutePath();
                        // 预览背景图
//                        setBgForThisParent(filePath);
                        setBgForMain2FormParent(tempFilePath);
                    }
                }
            }
        });
    }

    //TODO:加上用户自己设置的背景图选项
    //下拉框
    private void initComboBox() {
        imgComboBox.addItem("");
        imgComboBox.addItem("经典1");
        imgComboBox.addItem("经典2");
        imgComboBox.addItem("经典3");
        //如果注册表里保存的用户的背景图不是默认，那就看看有没有该背景图文件，有就加上该选项
        String localBgPath = Constants.preferences.get("bg", "");
        if(!localBgPath.contains("AuroraTimer_bg"))
            imgComboBox.addItem("我的自定义");
        imgComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String imgName = (String) imgComboBox.getSelectedItem();
                    InputStream bg = null;
                    String bgPathToSave = null; //保存到本地的路径
                    switch (imgName) {
                        case "经典1":
                            bgPathToSave = System.getProperty("java.io.tmpdir") + File.separator + "AuroraTimer_bg1.png";
                            bg = getClass().getClassLoader().getResourceAsStream("aurora/timer/img/bg/bg1.png");
                            break;
                        case "经典2":
                            bgPathToSave = System.getProperty("java.io.tmpdir") + File.separator + "AuroraTimer_bg2.png";
                            bg = getClass().getClassLoader().getResourceAsStream("aurora/timer/img/bg/bg2.png");
                            break;
                        case "经典3":
                            bgPathToSave = System.getProperty("java.io.tmpdir") + File.separator + "AuroraTimer_bg3.png";
                            bg = getClass().getClassLoader().getResourceAsStream("aurora/timer/img/bg/bg3.png");
                            break;
                        case "":
                            bgPathToSave = "";
                            break;
                        case "我的自定义":
                            bgPathToSave = Constants.preferences.get("bg", "");
                            try {
                                File bgFile = new File(bgPathToSave);
                                //判断自定义图片有没有在本地，（虽然启动时获取了，但还是有可能清理文件被清掉的）。没有的话从服务器从新获取一张
                                if(bgFile.exists())
                                    bg = new FileInputStream(bgFile);
                                else
                                    bg = new UserDataService().getBg(userData.getBgUrl());
                            } catch (FileNotFoundException fileNotFoundException) {
                                fileNotFoundException.printStackTrace();
                            }
                            break;
                    }
                    //如果要保存的路径为""，就不保存了
                    assert bgPathToSave != null;
                    if (!bgPathToSave.equals("")) {
                        try {
                            if (SaveBg.saveBg(bgPathToSave, bg, false))
                                tempFilePath = bgPathToSave;
                            else
                                logger.warning("保存背景图失败");
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                            logger.warning("保存背景图错误！");
                        }
                    }
//                    setBgForThisParent(filePath);
                    setBgForMain2FormParent(tempFilePath);
                }
            }
        });
    }
//
//    private void setBgForThisParent(String filePath) {
//        settingPanel.setUI(new BasicPanelUI() {
//            @Override
//            public void paint(Graphics g, JComponent c) {
//                super.paint(g, c);
//                File bg = new File(filePath);
//                if (bg.exists()) {
//                    g.drawImage(new ImageIcon(bg.getPath()).getImage(), 0, 0, c.getWidth(), c.getHeight(), null);
//                } else {
//                    g.drawImage(new ImageIcon(getClass().getClassLoader().getResource("bg1.png")).getImage(), 0, 0, c.getWidth(), c.getHeight(), null);
//                }
//            }
//        });
//    }

    private void setBgForMain2FormParent(String filePath) {
        Main2FormParent.setUI(new BasicPanelUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                super.paint(g, c);
                File bg = new File(filePath);
                if (bg.exists()) {
                    g.drawImage(new ImageIcon(bg.getPath()).getImage(), 0, 0, c.getWidth(), c.getHeight(), null);
                } else {
                    g.drawImage(new ImageIcon(getClass().getClassLoader().getResource("aurora/timer/img/bg/bg1.png")).getImage(), 0, 0, c.getWidth(), c.getHeight(), null);
                }
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(255, 255, 255, 200));
                g2.fillRect(0, 0, c.getWidth(), 40);
                g2.setColor(new Color(50, 50, 50, 200));
                g2.drawRect(0, 0, c.getWidth() - 1, c.getHeight() - 1);
            }
        });
    }

    public boolean uploadBg(String filePath) throws IOException {
        String bgPath = System.getProperty("java.io.tmpdir") + File.separator + userData.getID() + "_bg.png";
        File file = new File(filePath);
        if(file.length()<100)
            return false;
        UserDataService uds = new UserDataService();
        // 上传的同时保存到临时文件夹,注意：inputstream用一次就没了！
        SaveBg.saveBg(bgPath, new FileInputStream(file), true);
        uds.uploadBg(file);
        return true;
    }

}
