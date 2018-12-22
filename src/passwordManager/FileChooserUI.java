package passwordManager;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.fasterxml.jackson.databind.ObjectMapper;

import accounts.SiteDirectory;
import tools.FileEncoder;
import tools.PasswordUtils;

/**
 * 
 * @author Thomas Lee
 *
 */

public class FileChooserUI extends JFrame{

    private static final long serialVersionUID = -4152826293988278363L;

    private ObjectMapper mapper;
    
    private PasswordManager parent;
    private Font readableFont;
    
    private SiteDirectory directory;
    
    public FileChooserUI(PasswordManager parent, Font font, ObjectMapper mapper) {
        super("Choose Password File");
        setLayout(new GridLayout(3, 1));
        setBounds(600, 300, 650, 200);
        this.parent = parent;
        this.readableFont = font;
        this.mapper = mapper;
        this.directory = null;
        
        JPanel fileTextArea = new JPanel(new FlowLayout());
        JPanel filePasswordArea = new JPanel(new FlowLayout());
        JPanel fileOptionArea = new JPanel(new FlowLayout());
        
        JLabel fileSelectLabel = new JLabel("File Path:");
        JTextField fileSelectTextField = new JTextField();
        
        JLabel filePasswordLabel = new JLabel("Password:");
        JPasswordField filePasswordField = new JPasswordField();
        
        JButton fileCancelButton = new JButton("Cancel");
        JButton filePasswordButton = new JButton("Open");
        
        PasswordUtils.setSizeAndFont(fileSelectLabel, readableFont, 90, 40);
        PasswordUtils.setSizeAndFont(fileSelectTextField, readableFont, 500, 40);
        PasswordUtils.setSizeAndFont(filePasswordLabel, readableFont, 90, 40);
        PasswordUtils.setSizeAndFont(filePasswordField, readableFont, 500, 40);
        PasswordUtils.setSizeAndFont(filePasswordButton, readableFont, 125, 40);
        PasswordUtils.setSizeAndFont(fileCancelButton, readableFont, 125, 40);
        
        fileSelectLabel.setHorizontalAlignment(JLabel.TRAILING);
        filePasswordLabel.setHorizontalAlignment(JLabel.TRAILING);

        fileTextArea.add(fileSelectLabel);
        fileTextArea.add(fileSelectTextField);
        filePasswordArea.add(filePasswordLabel);
        filePasswordArea.add(filePasswordField);
        fileOptionArea.add(fileCancelButton);
        fileOptionArea.add(filePasswordButton);

        add(fileTextArea);
        add(filePasswordArea);
        add(fileOptionArea);
        
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setPreferredSize(new Dimension(750,500));
        
        fileSelectTextField.addMouseListener(new MouseListener() {
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
                PasswordUtils.selectFile(fileChooser, fileSelectTextField);
            }
        });
        
        filePasswordField.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {}
            public void keyReleased(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    openFile(fileSelectTextField, filePasswordField);
                }
            }
        });
        
        filePasswordButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openFile(fileSelectTextField, filePasswordField);
            }
        });
        
        fileCancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.selectedDirectoryOrClosed(null);
                dispose();
            }
        });
        
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
                parent.selectedDirectoryOrClosed(null);
                dispose();
            }
        });
        
        setVisible(true);
    }
    
    public void openFile(JTextField filePathField, JPasswordField passwordField) {
        if (filePathField.getText().length() == 0) {
            JOptionPane.showMessageDialog(this, "Please select a file");
        } else if (passwordField.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Please Enter a password");
        } else {
            String pass = String.valueOf(passwordField.getPassword());
            
            directory = FileEncoder.decryptFile(new File(filePathField.getText()), pass, mapper);
            
            if (directory == null) {
                JOptionPane.showMessageDialog(this, "Invalid passcode for file.");
                return;
            }
            
            parent.selectedDirectoryOrClosed(directory);
            this.dispose();
        }
    }
    
    
}
