package aurora.timer.client.view.until;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Locale;
import java.util.Vector;

import javax.accessibility.AccessibleContext;
import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.FileChooserUI;
import javax.swing.plaf.basic.BasicDirectoryModel;
import javax.swing.plaf.basic.BasicFileChooserUI;

import sun.awt.shell.ShellFolder;
import sun.swing.FilePane;
import sun.swing.SwingUtilities2;

/**
 * 自定义文件选择器
 *
 * @author tang https://blog.csdn.net/u012643122/article/details/39081595
 * @since 2014/8/19
 */
@SuppressWarnings({"serial", "restriction"})
public class CustomFileChooser extends JFileChooser {

    public static void main(String[] args) {

        UIManager.put("ScrollBarUI", "com.sun.java.swing.plaf.windows.WindowsScrollBarUI");// 设置滚动条样式为window风格的滚动条样式

        // 设置文件夹在swing中所显示的图标
        UIManager.put("FileView.directoryIcon", FileSystemView.getFileSystemView().getSystemIcon(new File(System.getProperty("user.dir"))));

        // 如果觉得默认的图标（文件选择器窗口的右上角那一排）太丑可以自己设置文件选择对话框的一系列图标
        //	UIManager.put("FileChooser.newFolderIcon", newFolderIcon);
        //	UIManager.put("FileChooser.upFolderIcon", upFolderIcon);
        //	UIManager.put("FileChooser.homeFolderIcon", homeFolderIcon);
        //	UIManager.put("FileChooser.detailsViewIcon", detailsViewIcon);
        //	UIManager.put("FileChooser.listViewIcon", listViewIcon);

        // 设置工具提示的默认样式
        Color toolTipColor = new Color(80, 80, 80);
        UIManager.put("ToolTip.border",
                BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(toolTipColor), BorderFactory.createEmptyBorder(2, 3, 2, 3)));
        UIManager.put("ToolTip.background", Color.WHITE);
        UIManager.put("ToolTip.foreground", toolTipColor);

        CustomFileChooser chooser = new CustomFileChooser();
        chooser.showOpenDialog(null);
    }

    public CustomFileChooser() {
        super();
    }

    public CustomFileChooser(File currentDirectory, FileSystemView fsv) {
        super(currentDirectory, fsv);
    }

    public CustomFileChooser(File currentDirectory) {
        super(currentDirectory);
    }

    public CustomFileChooser(FileSystemView fsv) {
        super(fsv);
    }

    public CustomFileChooser(String currentDirectoryPath, FileSystemView fsv) {
        super(currentDirectoryPath, fsv);
    }

    public CustomFileChooser(String currentDirectoryPath) {
        super(currentDirectoryPath);
    }

    {
        setUI(new CustomFileChooserUI(this));
    }

    @Override
    protected JDialog createDialog(Component parent) throws HeadlessException {
        FileChooserUI ui = getUI();
        String title = ui.getDialogTitle(this);
        putClientProperty(AccessibleContext.ACCESSIBLE_DESCRIPTION_PROPERTY, title);

        Window window = getWindowForComponent(parent);
        JDialog dialog = new JDialog(window, title);
        dialog.setModal(true);
        dialog.setComponentOrientation(this.getComponentOrientation());

        if (JDialog.isDefaultLookAndFeelDecorated()) {
            boolean supportsWindowDecorations = UIManager.getLookAndFeel().getSupportsWindowDecorations();
            if (supportsWindowDecorations) {
                dialog.getRootPane().setWindowDecorationStyle(JRootPane.FILE_CHOOSER_DIALOG);
            }
        }
        dialog.getRootPane().setDefaultButton(ui.getDefaultButton(this));
        dialog.getContentPane().add(this);
        dialog.pack();
        setLocationRelativeTo(parent, dialog);

        return dialog;
    }

    /**
     * 自定义文件选择器UI,代码主要来源于MetalFileChooserUI
     *
     * @author tang
     * @since 2014/8/19
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static class CustomFileChooserUI extends BasicFileChooserUI {

        private static Font labelFont14 = new Font(Font.DIALOG, Font.PLAIN, 14);
        private static Font labelFont12 = new Font(Font.DIALOG, Font.PLAIN, 12);
        private static Color boxBorderColor = Color.GRAY;
        private static Color boxForegroundColor = new Color(51, 51, 51);
        // private static int boxHeight = 25;
        // private static int buttonWidth=30;
        // private static int buttonHeight=30;

        // Much of the Metal UI for JFilechooser is just a copy of
        // the windows implementation, but using Metal themed buttons, lists,
        // icons, etc. We are planning a complete rewrite, and hence we've
        // made most things in this class private.

        private BasicFileView fileView = new CustomFileView();

        private JLabel lookInLabel;
        private CustomComboBox directoryComboBox;
        private DirectoryComboBoxModel directoryComboBoxModel;
        private Action directoryComboBoxAction = new DirectoryComboBoxAction();

        private FilterComboBoxModel filterComboBoxModel;

        private JTextField fileNameTextField;

        private FilePane filePane;
        private JToggleButton listViewButton;
        private JToggleButton detailsViewButton;

        private JButton approveButton;
        private JButton cancelButton;

        private JPanel buttonPanel;
        private JPanel bottomPanel;

        private CustomComboBox filterComboBox;

        private static final Dimension hstrut5 = new Dimension(5, 1);
        // private static final Dimension hstrut11 = new Dimension(11, 1);

        private static final Dimension vstrut5 = new Dimension(1, 5);

        private static final Insets shrinkwrap = new Insets(0, 0, 0, 0);

        // Preferred and Minimum sizes for the dialog box
        private static int PREF_WIDTH = 500;
        private static int PREF_HEIGHT = 326;
        private static Dimension PREF_SIZE = new Dimension(PREF_WIDTH, PREF_HEIGHT);

        private static int MIN_WIDTH = 500;
        private static int MIN_HEIGHT = 326;
        private static Dimension MIN_SIZE = new Dimension(MIN_WIDTH, MIN_HEIGHT);

        private static int LIST_PREF_WIDTH = 405;
        private static int LIST_PREF_HEIGHT = 135;
        private static Dimension LIST_PREF_SIZE = new Dimension(LIST_PREF_WIDTH, LIST_PREF_HEIGHT);

        // Labels, mnemonics, and tooltips (oh my!)
        private int lookInLabelMnemonic = 0;
        private String lookInLabelText = null;
        private String saveInLabelText = null;

        private int fileNameLabelMnemonic = 0;
        private String fileNameLabelText = null;
        private int folderNameLabelMnemonic = 0;
        private String folderNameLabelText = null;

        private int filesOfTypeLabelMnemonic = 0;
        private String filesOfTypeLabelText = null;

        private String upFolderToolTipText = null;
        private String upFolderAccessibleName = null;

        private String homeFolderToolTipText = null;
        private String homeFolderAccessibleName = null;

        private String newFolderToolTipText = null;
        private String newFolderAccessibleName = null;

        private String listViewButtonToolTipText = null;
        private String listViewButtonAccessibleName = null;

        private String detailsViewButtonToolTipText = null;
        private String detailsViewButtonAccessibleName = null;

        private AlignedLabel fileNameLabel;

        private void populateFileNameLabel() {
            if (getFileChooser().getFileSelectionMode() == JFileChooser.DIRECTORIES_ONLY) {
                fileNameLabel.setText(folderNameLabelText);
                fileNameLabel.setDisplayedMnemonic(folderNameLabelMnemonic);
            } else {
                fileNameLabel.setText(fileNameLabelText);
                fileNameLabel.setDisplayedMnemonic(fileNameLabelMnemonic);
            }
        }

        //
        // ComponentUI Interface Implementation methods
        //
        public static ComponentUI createUI(JComponent c) {
            return new CustomFileChooserUI((JFileChooser) c);
        }

        public CustomFileChooserUI(JFileChooser filechooser) {
            super(filechooser);
        }

        public void installUI(JComponent c) {
            super.installUI(c);
        }

        public void uninstallComponents(JFileChooser fc) {
            fc.removeAll();
            bottomPanel = null;
            buttonPanel = null;
        }

        private class CustomFileChooserUIAccessor implements FilePane.FileChooserUIAccessor {
            public JFileChooser getFileChooser() {
                return CustomFileChooserUI.this.getFileChooser();
            }

            public BasicDirectoryModel getModel() {
                return CustomFileChooserUI.this.getModel();
            }

            public JPanel createList() {
                return CustomFileChooserUI.this.createList(getFileChooser());
            }

            public JPanel createDetailsView() {
                return CustomFileChooserUI.this.createDetailsView(getFileChooser());
            }

            public boolean isDirectorySelected() {
                return CustomFileChooserUI.this.isDirectorySelected();
            }

            public File getDirectory() {
                return CustomFileChooserUI.this.getDirectory();
            }

            public Action getChangeToParentDirectoryAction() {
                return CustomFileChooserUI.this.getChangeToParentDirectoryAction();
            }

            public Action getApproveSelectionAction() {
                return CustomFileChooserUI.this.getApproveSelectionAction();
            }

            public Action getNewFolderAction() {
                return CustomFileChooserUI.this.getNewFolderAction();
            }

            public MouseListener createDoubleClickListener(JList list) {
                return CustomFileChooserUI.this.createDoubleClickListener(getFileChooser(), list);
            }

            public ListSelectionListener createListSelectionListener() {
                return CustomFileChooserUI.this.createListSelectionListener(getFileChooser());
            }
        }

        public void installComponents(JFileChooser fc) {
            FileSystemView fsv = fc.getFileSystemView();

            fc.setBorder(new EmptyBorder(12, 12, 11, 11));
            fc.setLayout(new BorderLayout(0, 11));

            filePane = new FilePane(new CustomFileChooserUIAccessor());
            fc.addPropertyChangeListener(filePane);

            // ********************************* //
            // **** Construct the top panel **** //
            // ********************************* //

            // Directory manipulation buttons
            JPanel topPanel = new JPanel(new BorderLayout(11, 0));
            JPanel topButtonPanel = new JPanel();
            topButtonPanel.setLayout(new BoxLayout(topButtonPanel, BoxLayout.LINE_AXIS));
            topPanel.add(topButtonPanel, BorderLayout.AFTER_LINE_ENDS);

            // Add the top panel to the fileChooser
            fc.add(topPanel, BorderLayout.NORTH);

            // ComboBox Label
            lookInLabel = new JLabel(lookInLabelText);
            lookInLabel.setFont(labelFont14);
            lookInLabel.setDisplayedMnemonic(lookInLabelMnemonic);
            topPanel.add(lookInLabel, BorderLayout.BEFORE_LINE_BEGINS);

            // CurrentDir ComboBox
            directoryComboBox = new CustomComboBox() {
                public Dimension getPreferredSize() {
                    Dimension d = super.getPreferredSize();
                    // Must be small enough to not affect total width.
                    d.width = 150;
                    return d;
                }
            };
            setCustomComboBox(directoryComboBox);
            directoryComboBox.putClientProperty(AccessibleContext.ACCESSIBLE_DESCRIPTION_PROPERTY, lookInLabelText);
            directoryComboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
            lookInLabel.setLabelFor(directoryComboBox);
            directoryComboBoxModel = createDirectoryComboBoxModel(fc);
            directoryComboBox.setModel(directoryComboBoxModel);
            directoryComboBox.addActionListener(directoryComboBoxAction);
            directoryComboBox.setRenderer(createDirectoryComboBoxRenderer(fc));
            directoryComboBox.setAlignmentX(JComponent.LEFT_ALIGNMENT);
            directoryComboBox.setAlignmentY(JComponent.TOP_ALIGNMENT);
            directoryComboBox.setMaximumRowCount(8);

            topPanel.add(directoryComboBox, BorderLayout.CENTER);

            // Up Button
            JButton upFolderButton = getButton(upFolderIcon, getChangeToParentDirectoryAction());
            upFolderButton.setToolTipText(upFolderToolTipText);
            upFolderButton.putClientProperty(AccessibleContext.ACCESSIBLE_NAME_PROPERTY, upFolderAccessibleName);

            topButtonPanel.add(upFolderButton);
            topButtonPanel.add(Box.createRigidArea(hstrut5));

            // Home Button
            File homeDir = fsv.getHomeDirectory();
            String toolTipText = homeFolderToolTipText;
            if (fsv.isRoot(homeDir)) {
                toolTipText = getFileView(fc).getName(homeDir); // Probably "Desktop".
            }

            JButton goHomeButton = getButton(homeFolderIcon, getGoHomeAction());
            goHomeButton.setToolTipText(toolTipText);
            goHomeButton.putClientProperty(AccessibleContext.ACCESSIBLE_NAME_PROPERTY, homeFolderAccessibleName);

            topButtonPanel.add(goHomeButton);
            topButtonPanel.add(Box.createRigidArea(hstrut5));

            // New Directory Button
            if (!UIManager.getBoolean("FileChooser.readOnly")) {
                JButton newFolderButton = getButton(newFolderIcon, filePane.getNewFolderAction());
                newFolderButton.setToolTipText(newFolderToolTipText);
                newFolderButton.putClientProperty(AccessibleContext.ACCESSIBLE_NAME_PROPERTY, newFolderAccessibleName);
                topButtonPanel.add(newFolderButton);
                topButtonPanel.add(Box.createRigidArea(hstrut5));
            }

            // View button group
            ButtonGroup viewButtonGroup = new ButtonGroup();

            // List Button
            listViewButton = getToggleButton(listViewIcon, filePane.getViewTypeAction(FilePane.VIEWTYPE_LIST));
            listViewButton.setToolTipText(listViewButtonToolTipText);
            listViewButton.putClientProperty(AccessibleContext.ACCESSIBLE_NAME_PROPERTY, listViewButtonAccessibleName);
            listViewButton.setSelected(true);
            topButtonPanel.add(listViewButton);
            viewButtonGroup.add(listViewButton);
            topButtonPanel.add(Box.createRigidArea(hstrut5));

            // Details Button
            detailsViewButton = getToggleButton(detailsViewIcon, filePane.getViewTypeAction(FilePane.VIEWTYPE_DETAILS));
            detailsViewButton.setToolTipText(detailsViewButtonToolTipText);
            detailsViewButton.putClientProperty(AccessibleContext.ACCESSIBLE_NAME_PROPERTY, detailsViewButtonAccessibleName);
            topButtonPanel.add(detailsViewButton);
            viewButtonGroup.add(detailsViewButton);

            filePane.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent e) {
                    if ("viewType".equals(e.getPropertyName())) {
                        int viewType = filePane.getViewType();
                        switch (viewType) {
                            case FilePane.VIEWTYPE_LIST:
                                listViewButton.setSelected(true);
                                break;

                            case FilePane.VIEWTYPE_DETAILS:
                                detailsViewButton.setSelected(true);
                                break;
                        }
                    }
                }
            });

            // ************************************** //
            // ******* Add the directory pane ******* //
            // ************************************** //
            fc.add(getAccessoryPanel(), BorderLayout.AFTER_LINE_ENDS);
            JComponent accessory = fc.getAccessory();
            if (accessory != null) {
                getAccessoryPanel().add(accessory);
            }
            filePane.setPreferredSize(LIST_PREF_SIZE);
            fc.add(filePane, BorderLayout.CENTER);

            // ********************************** //
            // **** Construct the bottom panel ** //
            // ********************************** //
            JPanel bottomPanel = getBottomPanel();
            bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
            fc.add(bottomPanel, BorderLayout.SOUTH);

            // FileName label and textfield
            JPanel fileNamePanel = new JPanel();
            fileNamePanel.setLayout(new BoxLayout(fileNamePanel, BoxLayout.LINE_AXIS));
            bottomPanel.add(fileNamePanel);
            bottomPanel.add(Box.createRigidArea(vstrut5));

            fileNameLabel = new AlignedLabel();
            populateFileNameLabel();
            fileNamePanel.add(fileNameLabel);

            fileNameTextField = new JTextField(35) {
                public Dimension getMaximumSize() {
                    return new Dimension(Short.MAX_VALUE, super.getPreferredSize().height);
                }
            };
            setTextField(fileNameTextField);
            fileNamePanel.add(fileNameTextField);
            fileNameLabel.setLabelFor(fileNameTextField);
            fileNameTextField.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    if (!getFileChooser().isMultiSelectionEnabled()) {
                        filePane.clearSelection();
                    }
                }
            });
            if (fc.isMultiSelectionEnabled()) {
                setFileName(fileNameString(fc.getSelectedFiles()));
            } else {
                setFileName(fileNameString(fc.getSelectedFile()));
            }

            // Filetype label and combobox
            JPanel filesOfTypePanel = new JPanel();
            filesOfTypePanel.setLayout(new BoxLayout(filesOfTypePanel, BoxLayout.LINE_AXIS));
            bottomPanel.add(filesOfTypePanel);

            AlignedLabel filesOfTypeLabel = new AlignedLabel(filesOfTypeLabelText);
            filesOfTypeLabel.setDisplayedMnemonic(filesOfTypeLabelMnemonic);
            filesOfTypePanel.add(filesOfTypeLabel);

            filterComboBoxModel = createFilterComboBoxModel();
            fc.addPropertyChangeListener(filterComboBoxModel);
            filterComboBox = new CustomComboBox(filterComboBoxModel);
            setCustomComboBox(filterComboBox);
            filterComboBox.putClientProperty(AccessibleContext.ACCESSIBLE_DESCRIPTION_PROPERTY, filesOfTypeLabelText);
            filesOfTypeLabel.setLabelFor(filterComboBox);
            filterComboBox.setRenderer(createFilterComboBoxRenderer());
            filesOfTypePanel.add(filterComboBox);

            // buttons
            getButtonPanel().setLayout(new ButtonAreaLayout());

            approveButton = createRolloverBackgroundButton(getApproveButtonText(fc));

            // Note: Metal does not use mnemonics for approve and cancel
            approveButton.addActionListener(getApproveSelectionAction());
            approveButton.setToolTipText(getApproveButtonToolTipText(fc));
            getButtonPanel().add(approveButton);

            cancelButton = createRolloverBackgroundButton(cancelButtonText);
            cancelButton.setToolTipText(cancelButtonToolTipText);
            cancelButton.addActionListener(getCancelSelectionAction());
            getButtonPanel().add(cancelButton);

            if (fc.getControlButtonsAreShown()) {
                addControlButtons();
            }

            groupLabels(new AlignedLabel[]{fileNameLabel, filesOfTypeLabel});
        }

        /**
         * 创建一个有默认样式的按钮
         *
         * @param text
         * @return
         */
        public static RolloverBackgroundButton createRolloverBackgroundButton(String text) {
            RolloverBackgroundButton button = new RolloverBackgroundButton(text);
            button.setFont(new Font(Font.DIALOG, Font.PLAIN, 16));
            return button;
        }

        protected void setTextField(JTextField textField) {
            textField.setFont(labelFont12);
            textField.setPreferredSize(new Dimension(0, 25));
            textField.setSelectedTextColor(Color.WHITE);
            textField.setSelectionColor(new Color(51, 153, 255));
            textField.setBorder(BorderFactory.createLineBorder(boxBorderColor));
            textField.setForeground(boxForegroundColor);
        }

        protected JButton getButton(Icon icon, Action action) {
            JButton button = new JButton(action);
            button.setIcon(icon);
            if (icon != null) {
                button.setPressedIcon(new MoveIcon(icon, 1, 1));
            }
            button.setText(null);
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            button.setBorderPainted(true);
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(boxBorderColor), new EmptyBorder(2, 2, 2, 2)));
            button.setAlignmentX(JComponent.LEFT_ALIGNMENT);
            button.setAlignmentY(JComponent.CENTER_ALIGNMENT);
            button.setMargin(shrinkwrap);
            return button;
        }

        protected JToggleButton getToggleButton(Icon icon, Action action) {
            JToggleButton button = new JToggleButton(action);
            button.setIcon(icon);
            if (icon != null) {
                button.setPressedIcon(new MoveIcon(icon, 1, 1));
            }
            button.setText(null);
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            button.setHorizontalTextPosition(SwingConstants.CENTER);
            button.setVerticalTextPosition(SwingConstants.CENTER);
            button.setRolloverEnabled(true);
            button.setFocusPainted(false);
            button.setOpaque(false);
            button.setContentAreaFilled(false);
            button.setBorderPainted(true);
            button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(boxBorderColor), new EmptyBorder(2, 2, 2, 2)));
            button.setAlignmentX(JComponent.LEFT_ALIGNMENT);
            button.setAlignmentY(JComponent.CENTER_ALIGNMENT);
            button.setMargin(shrinkwrap);
            return button;
        }

        protected CustomComboBox setCustomComboBox(CustomComboBox CustomComboBox) {
            CustomComboBox.setFont(labelFont12);
            CustomComboBox.setPreferredSize(new Dimension(0, 25));
            CustomComboBox.setSelectionBackground(Color.decode("#d7d7d7"));
            CustomComboBox.setBorder(BorderFactory.createLineBorder(boxBorderColor));
            CustomComboBox.setForeground(boxForegroundColor);
            CustomComboBox.synchAllToPopup();
            return CustomComboBox;
        }

        protected JPanel getButtonPanel() {
            if (buttonPanel == null) {
                buttonPanel = new JPanel();
            }
            return buttonPanel;
        }

        protected JPanel getBottomPanel() {
            if (bottomPanel == null) {
                bottomPanel = new JPanel();
            }
            return bottomPanel;
        }

        protected void installStrings(JFileChooser fc) {
            super.installStrings(fc);

            Locale l = fc.getLocale();

            lookInLabelMnemonic = getMnemonic("FileChooser.lookInLabelMnemonic", l);
            lookInLabelText = UIManager.getString("FileChooser.lookInLabelText", l);
            saveInLabelText = UIManager.getString("FileChooser.saveInLabelText", l);

            fileNameLabelMnemonic = getMnemonic("FileChooser.fileNameLabelMnemonic", l);
            fileNameLabelText = UIManager.getString("FileChooser.fileNameLabelText", l);
            folderNameLabelMnemonic = getMnemonic("FileChooser.folderNameLabelMnemonic", l);
            folderNameLabelText = UIManager.getString("FileChooser.folderNameLabelText", l);

            filesOfTypeLabelMnemonic = getMnemonic("FileChooser.filesOfTypeLabelMnemonic", l);
            filesOfTypeLabelText = UIManager.getString("FileChooser.filesOfTypeLabelText", l);

            upFolderToolTipText = UIManager.getString("FileChooser.upFolderToolTipText", l);
            upFolderAccessibleName = UIManager.getString("FileChooser.upFolderAccessibleName", l);

            homeFolderToolTipText = UIManager.getString("FileChooser.homeFolderToolTipText", l);
            homeFolderAccessibleName = UIManager.getString("FileChooser.homeFolderAccessibleName", l);

            newFolderToolTipText = UIManager.getString("FileChooser.newFolderToolTipText", l);
            newFolderAccessibleName = UIManager.getString("FileChooser.newFolderAccessibleName", l);

            listViewButtonToolTipText = UIManager.getString("FileChooser.listViewButtonToolTipText", l);
            listViewButtonAccessibleName = UIManager.getString("FileChooser.listViewButtonAccessibleName", l);

            detailsViewButtonToolTipText = UIManager.getString("FileChooser.detailsViewButtonToolTipText", l);
            detailsViewButtonAccessibleName = UIManager.getString("FileChooser.detailsViewButtonAccessibleName", l);
        }

        private Integer getMnemonic(String key, Locale l) {
            return SwingUtilities2.getUIDefaultsInt(key, l);
        }

        protected void installListeners(JFileChooser fc) {
            super.installListeners(fc);
            ActionMap actionMap = getActionMaping();
            SwingUtilities.replaceUIActionMap(fc, actionMap);
        }

        private ActionMap getActionMaping() {
            return createActionMaping();
        }

        private ActionMap createActionMaping() {
            ActionMap map = new ActionMapUIResource();
            FilePane.addActionsToMap(map, filePane.getActions());
            return map;
        }

        protected JPanel createList(JFileChooser fc) {
            return filePane.createList();
        }

        protected JPanel createDetailsView(JFileChooser fc) {
            return filePane.createDetailsView();
        }

        /**
         * Creates a selection listener for the list of files and directories.
         *
         * @param fc a <code>JFileChooser</code>
         * @return a <code>ListSelectionListener</code>
         */
        public ListSelectionListener createListSelectionListener(JFileChooser fc) {
            return super.createListSelectionListener(fc);
        }

        // Obsolete class, not used in this version.
        protected class SingleClickListener extends MouseAdapter {
            public SingleClickListener(JList list) {
            }
        }

        // Obsolete class, not used in this version.
        protected class FileRenderer extends DefaultListCellRenderer {
        }

        public void uninstallUI(JComponent c) {
            // Remove listeners
            c.removePropertyChangeListener(filterComboBoxModel);
            c.removePropertyChangeListener(filePane);
            cancelButton.removeActionListener(getCancelSelectionAction());
            approveButton.removeActionListener(getApproveSelectionAction());
            fileNameTextField.removeActionListener(getApproveSelectionAction());

            if (filePane != null) {
                filePane.uninstallUI();
                filePane = null;
            }

            super.uninstallUI(c);
        }

        /**
         * Returns the preferred size of the specified <code>JFileChooser</code>
         * . The preferred size is at least as large, in both height and width,
         * as the preferred size recommended by the file chooser's layout
         * manager.
         *
         * @param c a <code>JFileChooser</code>
         * @return a <code>Dimension</code> specifying the preferred width and
         * height of the file chooser
         */
        public Dimension getPreferredSize(JComponent c) {
            int prefWidth = PREF_SIZE.width;
            Dimension d = c.getLayout().preferredLayoutSize(c);
            if (d != null) {
                return new Dimension(d.width < prefWidth ? prefWidth : d.width, d.height < PREF_SIZE.height ? PREF_SIZE.height : d.height);
            } else {
                return new Dimension(prefWidth, PREF_SIZE.height);
            }
        }

        /**
         * Returns the minimum size of the <code>JFileChooser</code>.
         *
         * @param c a <code>JFileChooser</code>
         * @return a <code>Dimension</code> specifying the minimum width and
         * height of the file chooser
         */
        public Dimension getMinimumSize(JComponent c) {
            return MIN_SIZE;
        }

        /**
         * Returns the maximum size of the <code>JFileChooser</code>.
         *
         * @param c a <code>JFileChooser</code>
         * @return a <code>Dimension</code> specifying the maximum width and
         * height of the file chooser
         */
        public Dimension getMaximumSize(JComponent c) {
            return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }

        private String fileNameString(File file) {
            if (file == null) {
                return null;
            } else {
                JFileChooser fc = getFileChooser();
                if ((fc.isDirectorySelectionEnabled() && !fc.isFileSelectionEnabled())
                        || (fc.isDirectorySelectionEnabled() && fc.isFileSelectionEnabled() && fc.getFileSystemView().isFileSystemRoot(file))) {
                    return file.getPath();
                } else {
                    return file.getName();
                }
            }
        }

        private String fileNameString(File[] files) {
            StringBuffer buf = new StringBuffer();
            for (int i = 0; files != null && i < files.length; i++) {
                if (i > 0) {
                    buf.append(" ");
                }
                if (files.length > 1) {
                    buf.append("\"");
                }
                buf.append(fileNameString(files[i]));
                if (files.length > 1) {
                    buf.append("\"");
                }
            }
            return buf.toString();
        }

        /* The following methods are used by the PropertyChange Listener */

        private void doSelectedFileChanged(PropertyChangeEvent e) {
            File f = (File) e.getNewValue();
            JFileChooser fc = getFileChooser();
            if (f != null && ((fc.isFileSelectionEnabled() && !f.isDirectory()) || (f.isDirectory() && fc.isDirectorySelectionEnabled()))) {

                setFileName(fileNameString(f));
            }
        }

        private void doSelectedFilesChanged(PropertyChangeEvent e) {
            File[] files = (File[]) e.getNewValue();
            JFileChooser fc = getFileChooser();
            if (files != null && files.length > 0 && (files.length > 1 || fc.isDirectorySelectionEnabled() || !files[0].isDirectory())) {
                setFileName(fileNameString(files));
            }
        }

        private void doDirectoryChanged(PropertyChangeEvent e) {
            JFileChooser fc = getFileChooser();
            FileSystemView fsv = fc.getFileSystemView();

            clearIconCache();
            File currentDirectory = fc.getCurrentDirectory();
            if (currentDirectory != null) {
                directoryComboBoxModel.addItem(currentDirectory);

                if (fc.isDirectorySelectionEnabled() && !fc.isFileSelectionEnabled()) {
                    if (fsv.isFileSystem(currentDirectory)) {
                        setFileName(currentDirectory.getPath());
                    } else {
                        setFileName(null);
                    }
                }
            }
        }

        private void doFilterChanged(PropertyChangeEvent e) {
            clearIconCache();
        }

        private void doFileSelectionModeChanged(PropertyChangeEvent e) {
            if (fileNameLabel != null) {
                populateFileNameLabel();
            }
            clearIconCache();

            JFileChooser fc = getFileChooser();
            File currentDirectory = fc.getCurrentDirectory();
            if (currentDirectory != null && fc.isDirectorySelectionEnabled() && !fc.isFileSelectionEnabled()
                    && fc.getFileSystemView().isFileSystem(currentDirectory)) {

                setFileName(currentDirectory.getPath());
            } else {
                setFileName(null);
            }
        }

        private void doAccessoryChanged(PropertyChangeEvent e) {
            if (getAccessoryPanel() != null) {
                if (e.getOldValue() != null) {
                    getAccessoryPanel().remove((JComponent) e.getOldValue());
                }
                JComponent accessory = (JComponent) e.getNewValue();
                if (accessory != null) {
                    getAccessoryPanel().add(accessory, BorderLayout.CENTER);
                }
            }
        }

        private void doApproveButtonTextChanged(PropertyChangeEvent e) {
            JFileChooser chooser = getFileChooser();
            approveButton.setText(getApproveButtonText(chooser));
            approveButton.setToolTipText(getApproveButtonToolTipText(chooser));
        }

        private void doDialogTypeChanged(PropertyChangeEvent e) {
            JFileChooser chooser = getFileChooser();
            approveButton.setText(getApproveButtonText(chooser));
            approveButton.setToolTipText(getApproveButtonToolTipText(chooser));
            if (chooser.getDialogType() == JFileChooser.SAVE_DIALOG) {
                lookInLabel.setText(saveInLabelText);
            } else {
                lookInLabel.setText(lookInLabelText);
            }
        }

        private void doApproveButtonMnemonicChanged(PropertyChangeEvent e) {
            // Note: Metal does not use mnemonics for approve and cancel
        }

        private void doControlButtonsChanged(PropertyChangeEvent e) {
            if (getFileChooser().getControlButtonsAreShown()) {
                addControlButtons();
            } else {
                removeControlButtons();
            }
        }

        /*
         * Listen for filechooser property changes, such as the selected file
         * changing, or the type of the dialog changing.
         */
        public PropertyChangeListener createPropertyChangeListener(JFileChooser fc) {
            return new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent e) {
                    String s = e.getPropertyName();
                    if (s.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
                        doSelectedFileChanged(e);
                    } else if (s.equals(JFileChooser.SELECTED_FILES_CHANGED_PROPERTY)) {
                        doSelectedFilesChanged(e);
                    } else if (s.equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)) {
                        doDirectoryChanged(e);
                    } else if (s.equals(JFileChooser.FILE_FILTER_CHANGED_PROPERTY)) {
                        doFilterChanged(e);
                    } else if (s.equals(JFileChooser.FILE_SELECTION_MODE_CHANGED_PROPERTY)) {
                        doFileSelectionModeChanged(e);
                    } else if (s.equals(JFileChooser.ACCESSORY_CHANGED_PROPERTY)) {
                        doAccessoryChanged(e);
                    } else if (s.equals(JFileChooser.APPROVE_BUTTON_TEXT_CHANGED_PROPERTY)
                            || s.equals(JFileChooser.APPROVE_BUTTON_TOOL_TIP_TEXT_CHANGED_PROPERTY)) {
                        doApproveButtonTextChanged(e);
                    } else if (s.equals(JFileChooser.DIALOG_TYPE_CHANGED_PROPERTY)) {
                        doDialogTypeChanged(e);
                    } else if (s.equals(JFileChooser.APPROVE_BUTTON_MNEMONIC_CHANGED_PROPERTY)) {
                        doApproveButtonMnemonicChanged(e);
                    } else if (s.equals(JFileChooser.CONTROL_BUTTONS_ARE_SHOWN_CHANGED_PROPERTY)) {
                        doControlButtonsChanged(e);
                    } else if (s.equals("componentOrientation")) {
                        ComponentOrientation o = (ComponentOrientation) e.getNewValue();
                        JFileChooser cc = (JFileChooser) e.getSource();
                        if (o != e.getOldValue()) {
                            cc.applyComponentOrientation(o);
                        }
                    } else if (s == "FileChooser.useShellFolder") {
                        doDirectoryChanged(e);
                    } else if (s.equals("ancestor")) {
                        if (e.getOldValue() == null && e.getNewValue() != null) {
                            // Ancestor was added, set initial focus
                            fileNameTextField.selectAll();
                            fileNameTextField.requestFocus();
                        }
                    }
                }
            };
        }

        protected void removeControlButtons() {
            getBottomPanel().remove(getButtonPanel());
        }

        protected void addControlButtons() {
            getBottomPanel().add(getButtonPanel());
        }

        public void ensureFileIsVisible(JFileChooser fc, File f) {
            filePane.ensureFileIsVisible(fc, f);
        }

        public void rescanCurrentDirectory(JFileChooser fc) {
            filePane.rescanCurrentDirectory();
        }

        public String getFileName() {
            if (fileNameTextField != null) {
                return fileNameTextField.getText();
            } else {
                return null;
            }
        }

        public void setFileName(String filename) {
            if (fileNameTextField != null) {
                fileNameTextField.setText(filename);
            }
        }

        /**
         * Property to remember whether a directory is currently selected in the
         * UI. This is normally called by the UI on a selection event.
         *
         * @param directorySelected if a directory is currently selected.
         * @since 1.4
         */
        protected void setDirectorySelected(boolean directorySelected) {
            super.setDirectorySelected(directorySelected);
            JFileChooser chooser = getFileChooser();
            if (directorySelected) {
                if (approveButton != null) {
                    approveButton.setText(directoryOpenButtonText);
                    approveButton.setToolTipText(directoryOpenButtonToolTipText);
                }
            } else {
                if (approveButton != null) {
                    approveButton.setText(getApproveButtonText(chooser));
                    approveButton.setToolTipText(getApproveButtonToolTipText(chooser));
                }
            }
        }

        public String getDirectoryName() {
            // PENDING(jeff) - get the name from the directory combobox
            return null;
        }

        public void setDirectoryName(String dirname) {
            // PENDING(jeff) - set the name in the directory combobox
        }

        protected DirectoryComboBoxRenderer createDirectoryComboBoxRenderer(JFileChooser fc) {
            return new DirectoryComboBoxRenderer();
        }

        //
        // Renderer for DirectoryComboBox
        //
        class DirectoryComboBoxRenderer extends DefaultListCellRenderer {
            IndentIcon ii = new IndentIcon();

            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value == null) {
                    setText("");
                    return this;
                }
                File directory = (File) value;
                setText(getFileChooser().getName(directory));
                Icon icon = getFileChooser().getIcon(directory);
                ii.icon = icon;
                ii.depth = directoryComboBoxModel.getDepth(index);
                setIcon(ii);

                return this;
            }
        }

        final static int space = 10;

        class IndentIcon implements Icon {

            Icon icon = null;
            int depth = 0;

            public void paintIcon(Component c, Graphics g, int x, int y) {
                if (c.getComponentOrientation().isLeftToRight()) {
                    icon.paintIcon(c, g, x + depth * space, y);
                } else {
                    icon.paintIcon(c, g, x, y);
                }
            }

            public int getIconWidth() {
                return icon.getIconWidth() + depth * space;
            }

            public int getIconHeight() {
                return icon.getIconHeight();
            }

        }

        //
        // DataModel for DirectoryComboxbox
        //
        protected DirectoryComboBoxModel createDirectoryComboBoxModel(JFileChooser fc) {
            return new DirectoryComboBoxModel();
        }

        /**
         * Data model for a type-face selection combo-box.
         */
        protected class DirectoryComboBoxModel extends AbstractListModel<Object> implements ComboBoxModel<Object> {
            Vector<File> directories = new Vector<File>();
            int[] depths = null;
            File selectedDirectory = null;
            JFileChooser chooser = getFileChooser();
            FileSystemView fsv = chooser.getFileSystemView();

            public DirectoryComboBoxModel() {
                // Add the current directory to the model, and make it the
                // selectedDirectory
                File dir = getFileChooser().getCurrentDirectory();
                if (dir != null) {
                    addItem(dir);
                }
            }

            /**
             * Adds the directory to the model and sets it to be selected,
             * additionally clears out the previous selected directory and the
             * paths leading up to it, if any.
             */
            private void addItem(File directory) {

                if (directory == null) {
                    return;
                }

                boolean useShellFolder = FilePane.usesShellFolder(chooser);

                directories.clear();

                File[] baseFolders;
                if (useShellFolder) {
                    baseFolders = AccessController.doPrivileged(new PrivilegedAction<File[]>() {
                        public File[] run() {
                            return (File[]) ShellFolder.get("fileChooserComboBoxFolders");
                        }
                    });
                } else {
                    baseFolders = fsv.getRoots();
                }
                directories.addAll(Arrays.asList(baseFolders));

                // Get the canonical (full) path. This has the side
                // benefit of removing extraneous chars from the path,
                // for example /foo/bar/ becomes /foo/bar
                File canonical;
                try {
                    canonical = ShellFolder.getNormalizedFile(directory);
                } catch (IOException e) {
                    // Maybe drive is not ready. Can't abort here.
                    canonical = directory;
                }

                // create File instances of each directory leading up to the top
                try {
                    File sf = useShellFolder ? ShellFolder.getShellFolder(canonical) : canonical;
                    File f = sf;
                    Vector<File> path = new Vector<File>(10);
                    do {
                        path.addElement(f);
                    } while ((f = f.getParentFile()) != null);

                    int pathCount = path.size();
                    // Insert chain at appropriate place in vector
                    for (int i = 0; i < pathCount; i++) {
                        f = path.get(i);
                        if (directories.contains(f)) {
                            int topIndex = directories.indexOf(f);
                            for (int j = i - 1; j >= 0; j--) {
                                directories.insertElementAt(path.get(j), topIndex + i - j);
                            }
                            break;
                        }
                    }
                    calculateDepths();
                    setSelectedItem(sf);
                } catch (FileNotFoundException ex) {
                    calculateDepths();
                }
            }

            private void calculateDepths() {
                depths = new int[directories.size()];
                for (int i = 0; i < depths.length; i++) {
                    File dir = directories.get(i);
                    File parent = dir.getParentFile();
                    depths[i] = 0;
                    if (parent != null) {
                        for (int j = i - 1; j >= 0; j--) {
                            if (parent.equals(directories.get(j))) {
                                depths[i] = depths[j] + 1;
                                break;
                            }
                        }
                    }
                }
            }

            public int getDepth(int i) {
                return (depths != null && i >= 0 && i < depths.length) ? depths[i] : 0;
            }

            public void setSelectedItem(Object selectedDirectory) {
                this.selectedDirectory = (File) selectedDirectory;
                fireContentsChanged(this, -1, -1);
            }

            public Object getSelectedItem() {
                return selectedDirectory;
            }

            public int getSize() {
                return directories.size();
            }

            public Object getElementAt(int index) {
                return directories.elementAt(index);
            }
        }

        //
        // Renderer for Types ComboBox
        //
        protected FilterComboBoxRenderer createFilterComboBoxRenderer() {
            return new FilterComboBoxRenderer();
        }

        /**
         * Render different type sizes and styles.
         */
        public class FilterComboBoxRenderer extends DefaultListCellRenderer {
            {
                setFont(labelFont12);
            }

            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value != null && value instanceof FileFilter) {
                    setText(((FileFilter) value).getDescription());
                }

                return this;
            }
        }

        //
        // DataModel for Types Comboxbox
        //
        protected FilterComboBoxModel createFilterComboBoxModel() {
            return new FilterComboBoxModel();
        }

        /**
         * Data model for a type-face selection combo-box.
         */
        protected class FilterComboBoxModel extends AbstractListModel<Object> implements ComboBoxModel<Object>, PropertyChangeListener {
            protected FileFilter[] filters;

            protected FilterComboBoxModel() {
                super();
                filters = getFileChooser().getChoosableFileFilters();
            }

            public void propertyChange(PropertyChangeEvent e) {
                String prop = e.getPropertyName();
                if (prop == JFileChooser.CHOOSABLE_FILE_FILTER_CHANGED_PROPERTY) {
                    filters = (FileFilter[]) e.getNewValue();
                    fireContentsChanged(this, -1, -1);
                } else if (prop == JFileChooser.FILE_FILTER_CHANGED_PROPERTY) {
                    fireContentsChanged(this, -1, -1);
                }
            }

            public void setSelectedItem(Object filter) {
                if (filter != null) {
                    getFileChooser().setFileFilter((FileFilter) filter);
                    fireContentsChanged(this, -1, -1);
                }
            }

            public Object getSelectedItem() {
                // Ensure that the current filter is in the list.
                // NOTE: we shouldnt' have to do this, since JFileChooser adds
                // the filter to the choosable filters list when the filter
                // is set. Lets be paranoid just in case someone overrides
                // setFileFilter in JFileChooser.
                FileFilter currentFilter = getFileChooser().getFileFilter();
                boolean found = false;
                if (currentFilter != null) {
                    for (FileFilter filter : filters) {
                        if (filter == currentFilter) {
                            found = true;
                        }
                    }
                    if (found == false) {
                        getFileChooser().addChoosableFileFilter(currentFilter);
                    }
                }
                return getFileChooser().getFileFilter();
            }

            public int getSize() {
                if (filters != null) {
                    return filters.length;
                } else {
                    return 0;
                }
            }

            public Object getElementAt(int index) {
                if (index > getSize() - 1) {
                    // This shouldn't happen. Try to recover gracefully.
                    return getFileChooser().getFileFilter();
                }
                if (filters != null) {
                    return filters[index];
                } else {
                    return null;
                }
            }
        }

        public void valueChanged(ListSelectionEvent e) {
            JFileChooser fc = getFileChooser();
            File f = fc.getSelectedFile();
            if (!e.getValueIsAdjusting() && f != null && !getFileChooser().isTraversable(f)) {
                setFileName(fileNameString(f));
            }
        }

        /**
         * Acts when DirectoryComboBox has changed the selected item.
         */
        protected class DirectoryComboBoxAction extends AbstractAction {
            protected DirectoryComboBoxAction() {
                super("DirectoryComboBoxAction");
            }

            public void actionPerformed(ActionEvent e) {
                directoryComboBox.hidePopup();
                File f = (File) directoryComboBox.getSelectedItem();
                if (!getFileChooser().getCurrentDirectory().equals(f)) {
                    getFileChooser().setCurrentDirectory(f);
                }
            }
        }

        protected JButton getApproveButton(JFileChooser fc) {
            return approveButton;
        }

        /**
         * <code>ButtonAreaLayout</code> behaves in a similar manner to
         * <code>FlowLayout</code>. It lays out all components from left to
         * right, flushed right. The widths of all components will be set to the
         * largest preferred size width.
         */
        private static class ButtonAreaLayout implements LayoutManager {
            private int hGap = 5;
            private int topMargin = 17;

            public void addLayoutComponent(String string, Component comp) {
            }

            public void layoutContainer(Container container) {
                Component[] children = container.getComponents();

                if (children != null && children.length > 0) {
                    int numChildren = children.length;
                    Dimension[] sizes = new Dimension[numChildren];
                    Insets insets = container.getInsets();
                    int yLocation = insets.top + topMargin;
                    int maxWidth = 0;

                    for (int counter = 0; counter < numChildren; counter++) {
                        sizes[counter] = children[counter].getPreferredSize();
                        maxWidth = Math.max(maxWidth, sizes[counter].width);
                    }
                    int xLocation, xOffset;
                    if (container.getComponentOrientation().isLeftToRight()) {
                        xLocation = container.getSize().width - insets.left - maxWidth;
                        xOffset = hGap + maxWidth;
                    } else {
                        xLocation = insets.left;
                        xOffset = -(hGap + maxWidth);
                    }
                    for (int counter = numChildren - 1; counter >= 0; counter--) {
                        children[counter].setBounds(xLocation, yLocation, maxWidth, sizes[counter].height);
                        xLocation -= xOffset;
                    }
                }
            }

            public Dimension minimumLayoutSize(Container c) {
                if (c != null) {
                    Component[] children = c.getComponents();

                    if (children != null && children.length > 0) {
                        int numChildren = children.length;
                        int height = 0;
                        Insets cInsets = c.getInsets();
                        int extraHeight = topMargin + cInsets.top + cInsets.bottom;
                        int extraWidth = cInsets.left + cInsets.right;
                        int maxWidth = 0;

                        for (int counter = 0; counter < numChildren; counter++) {
                            Dimension aSize = children[counter].getPreferredSize();
                            height = Math.max(height, aSize.height);
                            maxWidth = Math.max(maxWidth, aSize.width);
                        }
                        return new Dimension(extraWidth + numChildren * maxWidth + (numChildren - 1) * hGap, extraHeight + height);
                    }
                }
                return new Dimension(0, 0);
            }

            public Dimension preferredLayoutSize(Container c) {
                return minimumLayoutSize(c);
            }

            public void removeLayoutComponent(Component c) {
            }
        }

        private static void groupLabels(AlignedLabel[] group) {
            for (int i = 0; i < group.length; i++) {
                group[i].group = group;
            }
        }

        private class AlignedLabel extends JLabel {
            private AlignedLabel[] group;
            private int maxWidth = 0;

            AlignedLabel() {
                super();
            }

            AlignedLabel(String text) {
                super(text);
            }

            {
                setAlignmentX(JComponent.LEFT_ALIGNMENT);
                setFont(labelFont14);
            }

            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                // Align the width with all other labels in group.
                return new Dimension(getMaxWidth() + 11, d.height);
            }

            private int getMaxWidth() {
                if (maxWidth == 0 && group != null) {
                    int max = 0;
                    for (int i = 0; i < group.length; i++) {
                        max = Math.max(group[i].getSuperPreferredWidth(), max);
                    }
                    for (int i = 0; i < group.length; i++) {
                        group[i].maxWidth = max;
                    }
                }
                return maxWidth;
            }

            private int getSuperPreferredWidth() {
                return super.getPreferredSize().width;
            }
        }

        @Override
        public void clearIconCache() {
            fileView.clearIconCache();
        }

        @Override
        public FileView getFileView(JFileChooser fc) {
            return fileView;
        }

        // ***********************
        // * FileView operations *
        // ***********************
        protected class CustomFileView extends BasicFileView {

            public Icon getIcon(File f) {
                Icon icon = getCachedIcon(f);
                if (icon != null) {
                    return icon;
                }
                icon = FileSystemView.getFileSystemView().getSystemIcon(f);
                cacheIcon(f, icon);
                return icon;
            }
        }
    }

    ///

    public static Window getWindowForComponent(Component parentComponent) throws HeadlessException {
        if (parentComponent == null)
            return JOptionPane.getRootFrame();
        if (parentComponent instanceof Window)
            return (Window) parentComponent;
        return getWindowForComponent(parentComponent.getParent());
    }

    public static void setLocationRelativeTo(Component parent, Window child) {
        if (parent == null) {
            child.setLocationRelativeTo(null);
        } else {
            Window window = getWindowForComponent(parent);
            if (window != null && window.isShowing()) {
                child.setLocationRelativeTo(parent);
            } else {
                child.setLocationRelativeTo(null);
            }
        }
    }

    /**
     * 将原Icon位置移动的Icon
     *
     * @author PC
     */
    public static class MoveIcon implements Icon {
        Icon icon;
        int moveX;
        int moveY;

        public MoveIcon(Icon icon, int moveX, int moveY) {
            this.icon = icon;
            this.moveX = moveX;
            this.moveY = moveY;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            icon.paintIcon(c, g, x + moveX, y + moveY);
        }

        @Override
        public int getIconWidth() {
            return icon.getIconWidth();
        }

        @Override
        public int getIconHeight() {
            return icon.getIconHeight();
        }
    }

    /**
     * 箭头Icon
     *
     * @author tang
     */
    public static class ArrowIcon implements Icon {

        protected int iconWidth;
        protected int iconHeight;
        protected int triangleWidth;
        protected int triangleHeight;
        protected Color triangleColor;
        protected int direction;
        protected Polygon triangle = new Polygon();

        public ArrowIcon(int width, int height, Color arrowColor, int direction) {
            this(width, height, width, height, arrowColor, direction);
        }

        public ArrowIcon(int iconWidth, int iconHeight, int triangleWidth, int triangleHeight, Color triangleColor, int direction) {
            this.iconWidth = iconWidth;
            this.iconHeight = iconHeight;
            this.triangleWidth = triangleWidth;
            this.triangleHeight = triangleHeight;
            this.triangleColor = triangleColor;
            this.direction = direction;

            createTriangle();
        }

        protected void createTriangle() {
            int x = (iconWidth - triangleWidth) / 2;
            int y = (iconHeight - triangleHeight) / 2;

            if (direction == SwingConstants.TOP) {// 箭头向上
                triangle.addPoint(triangleWidth / 2 + x, y);
                triangle.addPoint(triangleWidth + x, triangleHeight + y);
                triangle.addPoint(x, triangleHeight + y);
            } else if (direction == SwingConstants.BOTTOM) {// 箭头向下
                triangle.addPoint(x, y);
                triangle.addPoint(triangleWidth + x, y);
                triangle.addPoint(triangleWidth / 2 + x, triangleHeight + y);
            } else if (direction == SwingConstants.LEFT) {
                triangle.addPoint(x, triangleHeight / 2 + y);
                triangle.addPoint(triangleWidth + x, y);
                triangle.addPoint(triangleWidth + x, triangleHeight + y);
            } else if (direction == SwingConstants.RIGHT) {
                triangle.addPoint(x, y);
                triangle.addPoint(triangleWidth + x, triangleHeight / 2 + y);
                triangle.addPoint(x, triangleHeight + y);
            }
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(triangleColor);
            AffineTransform af = new AffineTransform();
            af.translate(x, y);
            Shape shape = af.createTransformedShape(triangle);
            g2.fill(shape);
        }

        @Override
        public int getIconWidth() {
            return iconWidth;
        }

        @Override
        public int getIconHeight() {
            return iconHeight;
        }
    }
}