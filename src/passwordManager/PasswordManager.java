package passwordManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.fasterxml.jackson.databind.ObjectMapper;

import accounts.Account;
import accounts.Site;
import accounts.SiteDirectory;
import tools.FileEncoder;
import tools.PasswordUtils;

/**
 * Simple Password Manager that uses AES to encrypt password file
 * 
 * @author Thomas Lee
 * 
 *  Copyright 2018 Thomas Lee

 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
    
 *      http://www.apache.org/licenses/LICENSE-2.0
    
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

public class PasswordManager extends JFrame{
    private static final long serialVersionUID = 2637498999135782809L;
    
    private String title;
    private long lastActionTime;
    private Integer currAcc = -1;
    private Integer currSite = -1;
    
    private SiteDirectory currDir = new SiteDirectory();
    private ObjectMapper mapper;
    
    private Font readableFont;
    
    private JFileChooser fileChooser;
    
    private JPanel saveFileArea;

    private JPanel mainField;
    private JPanel fileSelectPanel;
    private JPanel formPanel;
    
    private JPanel listPanelMain;
    private JList<String> siteList;
    private JList<String> accountList;
    private JButton shiftSiteUp;
    private JButton shiftSiteDown;
    
    private JButton deleteSiteButton;
    private JButton deleteAccountButton;
    private JTextField siteNameField;
    private JTextField urlField;
    private JTextField accountNameField;
    private JTextField usernameField;
    private JTextField passwordField;
    private JButton newAccountButton;
    
    private JButton shiftAccountUp;
    private JButton shiftAccountDown;
    
    private JPanel accountPanel;
    
    private JTextField saveFileField;
    private JPasswordField saveFilePasswordField;
    
    private boolean changesMade;
        
    public PasswordManager() {
        super();
        title = "Password Manager";
        mapper = new ObjectMapper();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(500, 100, 1000, 805);
        this.setTitle(title);
        lastActionTime = System.currentTimeMillis();
        
        readableFont = new Font(Font.DIALOG, Font.PLAIN, 18);
        fileChooser = new JFileChooser(System.getProperty("user.dir"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setPreferredSize(new Dimension(750,500));
     
        mainField = new JPanel(new FlowLayout());
        
        setupInitialDialogue();
        setupSiteSelect();
        setupSiteForm();
        setupSaveFileArea();
        
        this.getContentPane().add(BorderLayout.CENTER, mainField);
        this.getContentPane().add(BorderLayout.WEST, listPanelMain);
        this.getContentPane().add(BorderLayout.SOUTH, saveFileArea);
        this.setVisible(true);
        
        changesMade = false;
        
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {
                if (changesMade) {
                    int retVal = JOptionPane.showConfirmDialog(null, "Do you want to save before exiting?");
                    
                    if (retVal == JOptionPane.YES_OPTION) {
                        if (saveFile()) {
                            dispose();
                        }
                    } else if (retVal == JOptionPane.NO_OPTION) {
                        dispose();
                    }
                    
                } else {
                    dispose();
                }
            }
        });
        
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }
    
    private void setupInitialDialogue() {
        fileSelectPanel = new JPanel(new FlowLayout());
        JButton openButton = new JButton("Open existing File");
        JButton newFile = new JButton("Create new File?");
        openButton.setFont(readableFont);
        newFile.setFont(readableFont);
        
        fileSelectPanel.add(openButton);
        fileSelectPanel.add(newFile);
        mainField.add(fileSelectPanel);
        
        openButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                openFileDialogue();
            }
        });
        
        newFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveFileArea.setVisible(true);
                fileSelectPanel.setVisible(false);
                listPanelMain.setVisible(true);
                formPanel.setVisible(false);
            }
        });
    }

    private void setupSiteSelect() {
        siteList = new JList<String>();
        
        siteList.setModel(new DefaultListModel<String>());
        siteList.setFont(readableFont);

        listPanelMain = new JPanel(new FlowLayout());
        JPanel listPanelShift = new JPanel(new FlowLayout());
        JScrollPane siteListScroll = new JScrollPane(siteList);
        JLabel siteListLabel = new JLabel("Sites:");
        siteListLabel.setFont(readableFont);
        siteListLabel.setPreferredSize(new Dimension(200,50));
        
        siteListScroll.setPreferredSize(new Dimension(200, 550));
        listPanelMain.setPreferredSize(new Dimension(200, 600));
        
        JButton newSiteButton = new JButton("Add new site");
        newSiteButton.setFont(readableFont);

        siteList.setFixedCellHeight(40);
        
        listPanelMain.setVisible(false);
        
        shiftSiteUp = new JButton("^");
        shiftSiteDown = new JButton("v");
        
        shiftSiteUp.setToolTipText("Move the selected site up in the list");
        shiftSiteDown.setToolTipText("Move the selected site down in the list");

        shiftSiteUp.setPreferredSize(new Dimension(41,15));
        shiftSiteDown.setPreferredSize(new Dimension(41,15));
        shiftSiteUp.setEnabled(false);
        shiftSiteDown.setEnabled(false);
        
        listPanelShift.setPreferredSize(new Dimension(45,40));
        listPanelShift.add(shiftSiteUp);
        listPanelShift.add(shiftSiteDown);

        listPanelMain.add(siteListLabel);
        listPanelMain.add(siteListScroll);
        listPanelMain.add(newSiteButton);
        listPanelMain.add(listPanelShift);
        
        siteList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!siteList.isSelectionEmpty()) {
                    formPanel.setVisible(true);
                    accountPanel.setEnabled(false);
                    resetForm(accountNameField, usernameField, passwordField);
                    enableAccountFields(accountNameField, usernameField, passwordField, false);
                    currSite = siteList.getSelectedIndex();
                    
                    Site selectedSite = currDir.getSites().get(currSite);
                    
                    siteNameField.setText(selectedSite.getSiteName());
                    urlField.setText(selectedSite.getUrl());
                    accountList.setModel(populateModelSite(selectedSite));

                    shiftSiteUp.setEnabled(currSite != 0);
                    shiftSiteDown.setEnabled(currSite < currDir.getSites().size()-1);
                    deleteSiteButton.setEnabled(true);
                    deleteAccountButton.setEnabled(false);
                    formPanel.setVisible(true);
                }
            }
        });
        
        newSiteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                siteList.clearSelection();
                accountList.setModel(new DefaultListModel<String>());
                currAcc = -1;
                currSite = -1;
                resetForm(siteNameField, urlField, null);
                resetForm(accountNameField, usernameField, passwordField);
                enableAccountFields(accountNameField, usernameField, passwordField, true);
                deleteSiteButton.setEnabled(false);
                deleteAccountButton.setEnabled(false);
                formPanel.setVisible(true);
            }
        });
        
        shiftSiteUp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (System.currentTimeMillis() - lastActionTime > 500) {
                    int index = siteList.getSelectedIndex();
                    
                    if (currDir.shiftUp(index)) {
                        siteList.setModel(populateModelDirectory(currDir));
                        siteList.setSelectedIndex(index - 1);
                    }
                    lastActionTime = System.currentTimeMillis();
                    setTitle("*"+title);
                    changesMade = true;
                }
            }
        });
        
        shiftSiteDown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (System.currentTimeMillis() - lastActionTime > 500) {
                    int index = siteList.getSelectedIndex();
                    
                    if (currDir.shiftDown(index)) {
                        siteList.setModel(populateModelDirectory(currDir));
                        siteList.setSelectedIndex(index + 1);
                    }
                    lastActionTime = System.currentTimeMillis();
                    setTitle("*"+title);
                    changesMade = true;
                }
            }
        });
    }
    
    private void setupSiteForm() {
        formPanel = new JPanel(new FlowLayout());

        deleteSiteButton = new JButton("Delete Site");
        deleteAccountButton = new JButton("Delete Account");
        siteNameField = new JTextField();
        urlField = new JTextField();
        accountNameField = new JTextField();
        usernameField = new JTextField();
        passwordField = new JTextField();
        newAccountButton = new JButton("Add account");
        
        shiftAccountUp = new JButton("^");
        shiftAccountDown = new JButton("v");
        
        JLabel siteNameLabel = new JLabel("Site Name:");
        JLabel urlLabel = new JLabel("URL:");
        

        accountPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        accountPanel.setPreferredSize(new Dimension(500, 500));
        
        JLabel accountNameLabel = new JLabel("Account Name:");
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        JButton saveButton = new JButton("Save");
        
        JPanel accountListPanel = new JPanel(new FlowLayout());
        JPanel accountPanelShift = new JPanel(new FlowLayout());
        JLabel accountListLabel = new JLabel("Accounts:");
        newAccountButton.setFont(readableFont);
        accountListLabel.setFont(readableFont);
        PasswordUtils.setSizeAndFont(accountListLabel, readableFont, 200,50);
        
        shiftAccountUp.setToolTipText("Move the selected account up in the list");
        shiftAccountDown.setToolTipText("Move the selected account down in the list");

        shiftAccountUp.setPreferredSize(new Dimension(41,15));
        shiftAccountDown.setPreferredSize(new Dimension(41,15));

        shiftAccountUp.setEnabled(false);
        shiftAccountDown.setEnabled(false);
        
        accountPanelShift.setPreferredSize(new Dimension(45,40));
        accountPanelShift.add(shiftAccountUp);
        accountPanelShift.add(shiftAccountDown);
        
        accountList = new JList<String>();
        
        accountList.setModel(new DefaultListModel<String>());
        accountList.setFont(readableFont);
        
        JScrollPane accountListScroll = new JScrollPane(accountList);
        
        accountListScroll.setPreferredSize(new Dimension(200, 300));
        
        accountListPanel.setPreferredSize(new Dimension(200, 500));
        
        accountListPanel.add(accountListLabel);
        accountListPanel.add(accountListScroll);
        accountListPanel.add(newAccountButton);
        accountListPanel.add(accountPanelShift);

        PasswordUtils.setSizeAndFont(siteNameLabel, readableFont, 475, 45);
        PasswordUtils.setSizeAndFont(deleteSiteButton, readableFont, 200, 45);
        PasswordUtils.setSizeAndFont(siteNameField, readableFont, 675, 45);
        PasswordUtils.setSizeAndFont(urlLabel, readableFont, 675, 45);
        PasswordUtils.setSizeAndFont(urlField, readableFont, 675, 45);
        PasswordUtils.setSizeAndFont(accountNameLabel, readableFont, 250, 45);
        PasswordUtils.setSizeAndFont(deleteAccountButton, readableFont, 200, 45);
        PasswordUtils.setSizeAndFont(accountNameField, readableFont, 450, 45);
        PasswordUtils.setSizeAndFont(usernameLabel, readableFont, 450, 45);
        PasswordUtils.setSizeAndFont(usernameField, readableFont, 450, 45);
        PasswordUtils.setSizeAndFont(passwordLabel, readableFont, 450, 45);
        PasswordUtils.setSizeAndFont(passwordField, readableFont, 450, 45);
        deleteSiteButton.setEnabled(false);
        deleteAccountButton.setEnabled(false);

        PasswordUtils.setSizeAndFont(saveButton, readableFont, 200, 45);
        
        saveButton.setFont(readableFont);
        saveButton.setPreferredSize(new Dimension(200, 45));
        
        formPanel.setPreferredSize(new Dimension(850, 800));

        formPanel.add(siteNameLabel);
        formPanel.add(deleteSiteButton);
        formPanel.add(siteNameField);
        formPanel.add(urlLabel);
        formPanel.add(urlField);
        accountPanel.add(accountNameLabel);
        accountPanel.add(deleteAccountButton);
        accountPanel.add(accountNameField);
        accountPanel.add(usernameLabel);
        accountPanel.add(usernameField);
        accountPanel.add(passwordLabel);
        accountPanel.add(passwordField);
        accountPanel.add(saveButton);
        
        formPanel.add(accountListPanel);
        formPanel.add(accountPanel);
        
        formPanel.setVisible(false);
        formPanel.setAlignmentY(3);
        
        accountList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!accountList.isSelectionEmpty()) {
                    accountPanel.setEnabled(true);
                    enableAccountFields(accountNameField, usernameField, passwordField, true);
                    currAcc = accountList.getSelectedIndex();
                    
                    Site selectedSite = currDir.getSites().get(currSite);
                    Account selected = selectedSite.getAccounts().get(currAcc);
                    
                    accountNameField.setText(selected.getAccountName());
                    usernameField.setText(selected.getUsername());
                    passwordField.setText(selected.getPassword());
                    
                    shiftAccountUp.setEnabled(currAcc != 0);
                    shiftAccountDown.setEnabled(currAcc < selectedSite.getAccounts().size()-1);
                    deleteAccountButton.setEnabled(true);
                }
            }
        });
        
        newAccountButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                accountList.clearSelection();
                currAcc = -1;
                resetForm(accountNameField, usernameField, passwordField);
                enableAccountFields(accountNameField, usernameField, passwordField, true);
                deleteAccountButton.setEnabled(false);
            }
        });
        
        
        shiftAccountUp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (System.currentTimeMillis() - lastActionTime > 500) {
                    Site hold = currDir.getSites().get(siteList.getSelectedIndex());
                    
                    int index = accountList.getSelectedIndex();
                    
                    if (hold.shiftUp(index)) {
                        accountList.setModel(populateModelSite(hold));
                        accountList.setSelectedIndex(index - 1);
                    }
                    
                    lastActionTime = System.currentTimeMillis();
                    setTitle("*"+title);
                    changesMade = true;
                }
            }
        });
        
        shiftAccountDown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (System.currentTimeMillis() - lastActionTime > 500) {
                    Site hold = currDir.getSites().get(siteList.getSelectedIndex());
                    
                    int index = accountList.getSelectedIndex();
                    
                    if (hold.shiftDown(index)) {
                        accountList.setModel(populateModelSite(hold));
                        accountList.setSelectedIndex(index + 1);
                    }

                    lastActionTime = System.currentTimeMillis();
                    setTitle("*"+title);
                    changesMade = true;
                }
            }
        });
        
        deleteSiteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this site? This is irreversible if the file is saved.");
                
                if (confirm == JOptionPane.YES_OPTION) {
                    
                    currDir.removeSite(siteList.getSelectedIndex());
                    
                    siteList.setModel(populateModelDirectory(currDir));
                    currSite = -1;
                    currAcc = -1;
                    
                    shiftSiteUp.setEnabled(false);
                    shiftSiteDown.setEnabled(false);
                    resetForm(siteNameField, urlField, null);
                    resetForm(accountNameField, usernameField, passwordField);
                    formPanel.setVisible(false);
                    enableAccountFields(accountNameField, usernameField, passwordField, false);
                    setTitle("*"+title);
                    changesMade = true;
                }
                
            }
        });

        deleteAccountButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this account? This is irreversible if the file is saved.");
                
                if (confirm == JOptionPane.YES_OPTION) {
                    Site selectedSite = currDir.getSites().get(currSite);
                    
                    selectedSite.removeAccount(accountList.getSelectedIndex());
                    
                    accountList.setModel(populateModelSite(selectedSite));
                    currAcc = -1;
                    
                    shiftAccountUp.setEnabled(false);
                    shiftAccountDown.setEnabled(false);
                    resetForm(accountNameField, usernameField, passwordField);
                    accountPanel.setEnabled(false);
                    enableAccountFields(accountNameField, usernameField, passwordField, false);
                    setTitle("*"+title);
                    changesMade = true;
                }
                
            }
        });
        
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (currSite == -1) {
                    if (siteNameField.getText().length() == 0) {
                        JOptionPane.showMessageDialog(null, "Please enter a site name");
                    } else if (accountNameField.getText().length() == 0){
                        JOptionPane.showMessageDialog(null, "Please enter an account name");
                    } else {
                        String siteName = siteNameField.getText();
                        String url = urlField.getText();
                        String accountName = accountNameField.getText();
                        String username = usernameField.getText();
                        String password = passwordField.getText();
                        
                        if (currDir.getSites().size() == 0) {
                            currSite = 0;
                        } else {
                            currSite = currDir.getSites().lastKey() + 1;
                        }
                        
                        Account newacc = new Account(accountName, username, password);
                        Site newSite = new Site(siteName, url);
                        currAcc = 0;
                        
                        newSite.addAccount(newacc);
                        
                        currDir.addSite(newSite);
                        
                        siteList.setModel(populateModelDirectory(currDir));
                        accountList.setModel(populateModelSite(newSite));

                        siteList.setSelectedIndex(currSite);
                        accountList.setSelectedIndex(currAcc);
                        setTitle("*"+title);
                        changesMade = true;
                    }
                } else {
                    String siteName = siteNameField.getText();
                    String url = urlField.getText();
                    String accountName = accountNameField.getText();
                    String username = usernameField.getText();
                    String password = passwordField.getText();

                    Site changedSite = currDir.getSites().get(currSite);

                    changedSite.setSiteName(siteName);
                    changedSite.setUrl(url);
                    
                    boolean accCheck = false;
                    
                    if (accountPanel.isEnabled()) {
                        Account changedAccount;
                        
                        if (currAcc == -1) {
                            changedAccount = new Account();
                            if (changedSite.getAccounts().size() == 0) {
                                currAcc = 0;
                            } else {
                                currAcc = changedSite.getAccounts().lastKey() + 1;
                            }
                            changedSite.addAccount(changedAccount);
                        } else {
                            changedAccount = changedSite.getAccounts().get(currAcc);
                        }
                        
                        changedAccount.setAccountName(accountName);
                        changedAccount.setUsername(username);
                        changedAccount.setPassword(password);
                        
                        accCheck = true;
                    }

                    siteList.setModel(populateModelDirectory(currDir));
                    accountList.setModel(populateModelSite(changedSite));
                    
                    siteList.setSelectedIndex(currSite);
                    if (accCheck) {
                        accountList.setSelectedIndex(currAcc);
                    }
                    setTitle("*"+title);
                    changesMade = true;
                }
            }
        });
        
        mainField.add(formPanel);
    }

    private void setupSaveFileArea() {
        saveFileArea = new JPanel(new GridLayout(2,1));
        JPanel saveFilePathPanel = new JPanel(new FlowLayout());
        JPanel saveFilePasswordPanel = new JPanel(new FlowLayout());
        
        JLabel saveFileLabel = new JLabel("Output Path:");
        JButton saveFileSelectButton = new JButton("Select file");
        saveFileField = new JTextField();
        
        JLabel saveFilePasswordLabel = new JLabel("Master Password:");
        JButton saveFileButton = new JButton("Save File");
        saveFilePasswordField = new JPasswordField();
        
        PasswordUtils.setSizeAndFont(saveFileLabel, readableFont, 150, 40);
        PasswordUtils.setSizeAndFont(saveFileField, readableFont, 500, 40);
        PasswordUtils.setSizeAndFont(saveFileSelectButton, readableFont, 125, 40);
        PasswordUtils.setSizeAndFont(saveFilePasswordLabel, readableFont, 150, 40);
        PasswordUtils.setSizeAndFont(saveFilePasswordField, readableFont, 500, 40);
        PasswordUtils.setSizeAndFont(saveFileButton, readableFont, 125, 40);
        
        saveFileLabel.setHorizontalAlignment(JLabel.TRAILING);
        saveFilePasswordLabel.setHorizontalAlignment(JLabel.TRAILING);

        saveFilePathPanel.add(saveFileLabel);
        saveFilePathPanel.add(saveFileField);
        saveFilePathPanel.add(saveFileSelectButton);
        saveFilePasswordPanel.add(saveFilePasswordLabel);
        saveFilePasswordPanel.add(saveFilePasswordField);
        saveFilePasswordPanel.add(saveFileButton);

        saveFileArea.add(saveFilePathPanel);
        saveFileArea.add(saveFilePasswordPanel);
        saveFileArea.setVisible(false);

        saveFileField.addMouseListener(new MouseListener() {
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseClicked(MouseEvent e) {
                PasswordUtils.selectFile(fileChooser, saveFileField);
            }
        });
        
        saveFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });
    }

    private void openFileDialogue() {
        new FileChooserUI(this, readableFont, mapper);
        this.setEnabled(false);
    }
    
    private boolean saveFile() {
        if (saveFileField.getText().length() == 0) {
            JOptionPane.showMessageDialog(null, "Please select a file");
        } else if (saveFilePasswordField.getPassword().length == 0) {
            JOptionPane.showMessageDialog(null, "Please enter a password for the file");
        } else {
            currDir.setFilePath(saveFileField.getText());
            currDir.setMasterPassword(String.valueOf(saveFilePasswordField.getPassword()));

            FileEncoder.encryptAndSaveFile(currDir, mapper);
            changesMade = false;
            setTitle(title);
            return true;
        }
        return false;
    }
    
    public void selectedDirectoryOrClosed(SiteDirectory dir) {
        this.toFront();
        this.setEnabled(true);
        if (dir != null) {
            currDir = dir;
            siteList.setModel(populateModelDirectory(dir));
      
            saveFileField.setText(dir.getFilePath());
            saveFilePasswordField.setText(dir.getMasterPassword());
      
            saveFileArea.setVisible(true);
            fileSelectPanel.setVisible(false);
            listPanelMain.setVisible(true);
            formPanel.setVisible(false);
        }
    }
    
    private void resetForm(JTextField field1, JTextField field2, JTextField field3) {
        field1.setText(null);
        field2.setText(null);
        if (field3 != null) {
            field3.setText(null);
        }
    }
    
    private void enableAccountFields(JTextField nameField, JTextField usernameField, JTextField passwordField, boolean flag) {
        nameField.setEnabled(flag);
        usernameField.setEnabled(flag);
        passwordField.setEnabled(flag);
    }

    private DefaultListModel<String> populateModelDirectory(SiteDirectory dir) {
        DefaultListModel<String> ret = new DefaultListModel<String>();
        
        dir.condense();
        
        for (Integer i: dir.getSites().keySet()) {
            ret.addElement(dir.getSites().get(i).getSiteName());
        }
        
        return ret;
    }

    private DefaultListModel<String> populateModelSite(Site site) {
        DefaultListModel<String> ret = new DefaultListModel<String>();
        
        site.condense();
        
        for (Integer i: site.getAccounts().keySet()) {
            ret.addElement(site.getAccounts().get(i).getAccountName());
        }
        
        return ret;
    }
    
}
