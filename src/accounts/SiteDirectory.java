package accounts;

import java.util.TreeMap;

/**
 * 
 * @author Thomas Lee
 *
 */

public class SiteDirectory {

    private String masterPassword;
    private String filePath;
    private TreeMap<Integer, Site> sites;
    
    public SiteDirectory() {
        this(null, null, new TreeMap<Integer, Site>());
    }
    
    public SiteDirectory(String masterPassword, String filePath, TreeMap<Integer, Site> sites) {
        this.masterPassword = masterPassword;
        this.filePath = filePath;
        this.sites = sites;
    }
    
    public void condense() {
        if (sites.isEmpty()) {
            return;
        }
        int numAccs = 0;
        for(Integer i: sites.keySet()) {
            sites.get(i).condense();
            if (i != 0 && !sites.containsKey(i-1)) {
                sites.put(numAccs, sites.get(i));
                sites.remove(i);
            }
            numAccs++;
        }
    }
    
    public void addSite(Site site) {
        if (sites.isEmpty()) {
            sites.put(0, site);
        } else {
            sites.put(sites.lastKey() + 1, site);
        }
    }
    
    public void removeSite(Integer i) {
        sites.remove(i);
    }
    
    public boolean shiftUp(Integer i) {
        if (sites.isEmpty() || !sites.containsKey(i)) {
            return false;
        }
        
        Integer beforeKey = sites.ceilingKey(i - 1);
        
        if (beforeKey != null) {
            Site beforeSite = sites.get(beforeKey);
            
            sites.put(beforeKey, sites.get(i));
            sites.put(i, beforeSite);
            
            return true;
        }
        return false;
    }
    
    public boolean shiftDown(Integer i) {
        if (sites.isEmpty() || !sites.containsKey(i)) {
            return false;
        }
        
        Integer afterKey = sites.higherKey(i);
        
        if (afterKey != null) {
            Site afterSite = sites.get(afterKey);
            
            sites.put(afterKey, sites.get(i));
            sites.put(i, afterSite);
            return true;
        }
        return false;
    }

    public String getMasterPassword() {
        return masterPassword;
    }

    public String getFilePath() {
        return filePath;
    }

    public TreeMap<Integer, Site> getSites() {
        return sites;
    }

    public void setMasterPassword(String masterPassword) {
        this.masterPassword = masterPassword;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setSites(TreeMap<Integer, Site> sites) {
        this.sites = sites;
    }
    
}
