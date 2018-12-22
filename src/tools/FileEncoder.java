package tools;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import accounts.SiteDirectory;

/**
 * 
 * @author Thomas Lee
 *
 */

public class FileEncoder {
    
    @SuppressWarnings("resource")
    public static SiteDirectory decryptFile(File f, String key, ObjectMapper mapper) {
        SiteDirectory direct = null;
        
        try {
            Scanner scan = new Scanner(f);
            String decrypted = AESTool.decrypt(scan.nextLine(), key);
            
            if (decrypted == null) {
                return null;
            }
            direct = mapper.readValue(decrypted, SiteDirectory.class);
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return direct;
    }

    public static void encryptAndSaveFile(SiteDirectory directory, ObjectMapper mapper) {
        try {
            File f = new File(directory.getFilePath());
            f.createNewFile();
            
            String data = mapper.writeValueAsString(directory);
            
            String encrypted = AESTool.encrypt(data, directory.getMasterPassword());
            
            FileWriter writer = new FileWriter(f);
            
            writer.write(encrypted);
            writer.close();
        } catch (IOException e) {
        }
    }
}
