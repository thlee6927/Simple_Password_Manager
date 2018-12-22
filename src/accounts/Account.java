package accounts;

/**
 * 
 * @author Thomas Lee
 *
 */

public class Account {
    
    private String accountName;
    private String username;
    private String password;
    
    public Account() {
        this("", "", "");
    }
    
    public Account(String accountName, String username, String password) {
        this.accountName = accountName;
        this.username = username;
        this.password = password;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
//    public String descripCharString() {
//        String s = "";
//        
//        for(char c: description.toCharArray()) {
//            s = s + ((int) c) + ",";
//        }
//        
//        if (s.length() > 0) {
//            s = s.substring(0, s.length()-1);
//        }
//        
//        return s;
//    }
//    
//    public String toString() {
//        return accountName + "\n" + url + "\n" + descripCharString() + "\n" + username + "\n" + password;
//    }
    
}
