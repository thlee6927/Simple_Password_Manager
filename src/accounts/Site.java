package accounts;

import java.util.TreeMap;

/**
 * 
 * @author Thomas Lee
 *
 */

public class Site {

    private String siteName;
    private String url;
    private TreeMap<Integer, Account> accounts;
    
    public Site() {
        this("", "", new TreeMap<Integer, Account>());
    }

    public Site(String siteName, String url) {
        this(siteName, url, new TreeMap<Integer, Account>());
    }

    public Site(String siteName, String url, TreeMap<Integer, Account> accounts) {
        this.siteName = siteName;
        this.url = url;
        this.accounts = accounts;
    }
    
    public void condense() {
        if (accounts.isEmpty()) {
            return;
        }
        int numAccs = 0;
        for(Integer i: accounts.keySet()) {
            if (i != 0 && !accounts.containsKey(i-1)) {
                accounts.put(numAccs, accounts.get(i));
                accounts.remove(i);
            }
            numAccs++;
        }
    }
    
    public void addAccount(Account acc) {
        if (accounts.isEmpty()) {
            accounts.put(0, acc);
        } else {
            accounts.put(accounts.lastKey()+1, acc);
        }
    }
    
    public void removeAccount(Integer i) {
        accounts.remove(i);
    }
    
    public boolean shiftUp(Integer i) {
        if (accounts.isEmpty() || !accounts.containsKey(i)) {
            return false;
        }
        
        Integer beforeKey = accounts.ceilingKey(i - 1);
        
        if (beforeKey != null) {
            Account beforeAcc = accounts.get(beforeKey);
            
            accounts.put(beforeKey, accounts.get(i));
            accounts.put(i, beforeAcc);
            
            return true;
        }
        return false;
    }
    
    public boolean shiftDown(Integer i) {
        if (accounts.isEmpty() || !accounts.containsKey(i)) {
            return false;
        }
        
        Integer afterKey = accounts.higherKey(i);
        
        if (afterKey != null) {
            Account afterAcc = accounts.get(afterKey);
            
            accounts.put(afterKey, accounts.get(i));
            accounts.put(i, afterAcc);
            return true;
        }
        return false;
    }

    public String getSiteName() {
        return siteName;
    }

    public String getUrl() {
        return url;
    }

    public TreeMap<Integer, Account> getAccounts() {
        return accounts;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setAccounts(TreeMap<Integer, Account> accounts) {
        this.accounts = accounts;
    }
    
}
