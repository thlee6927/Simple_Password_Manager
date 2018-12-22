package tools;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JTextField;

/**
 * 
 * @author Thomas Lee
 *
 */

public class PasswordUtils {
    
    public static void setSizeAndFont(JComponent comp, Font font, int width, int height) {
        comp.setPreferredSize(new Dimension(width, height));
        comp.setFont(font);
    }
    
    public static void selectFile(JFileChooser fileChooser, JTextField textField) {
        int retVal = fileChooser.showOpenDialog(null);
        
        if (retVal == JFileChooser.APPROVE_OPTION) {
            textField.setText(fileChooser.getSelectedFile().getPath());
        }
    }
}
