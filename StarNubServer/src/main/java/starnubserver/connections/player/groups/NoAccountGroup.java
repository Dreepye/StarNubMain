package starnubserver.connections.player.groups;

import starnubserver.database.tables.GroupPermissions;
import starnubserver.database.tables.Groups;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class NoAccountGroup {

    /**
     * Represents the only instance of this class - Singleton Pattern
     */
    private static final NoAccountGroup instance = new NoAccountGroup();


    /**
     * This constructor is private - Singleton Pattern
     */
    private NoAccountGroup(){
        String noAccountGroup = "";
//        Map<String, Object> groups = StarNub.getStarboundServer().getConnectionss().getGroupSync().getGroups();
//        for (String groupName : groups.keySet()){
//            Map groupMap = (Map) groups.get(groupName);
//            if (((String) groupMap.get("type")).equalsIgnoreCase("noaccount")){
//                noAccountGroup = groupName;
//            }
//        }
        Group group = Groups.getInstance().getGroupByName(noAccountGroup);
        loadPermissions(group);
        this.name = group.getLadderName();
        this.tag = group.getTag();
    }

    /**
     * Recommended: For connections use with StarNub.
     * <p>
     * Uses: This will set and get Connections Singleton instance
     ** @return Connections Singleton Instance
     */
    @SuppressWarnings("unchecked")
    public static NoAccountGroup getInstance() {
        return instance;
    }


    private volatile String name;
    private volatile Tag tag;
    private ConcurrentHashMap<String, ConcurrentHashMap<String, ArrayList<String>>> permissions;

    public String getName() {
        return name;
    }
    public Tag getTag() {
        return tag;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public ConcurrentHashMap<String, ConcurrentHashMap<String, ArrayList<String>>> getPermissions() {
        return permissions;
    }

    public void setPermissions(ConcurrentHashMap<String, ConcurrentHashMap<String, ArrayList<String>>> permissions) {
        this.permissions = permissions;
    }

    public void reloadPermissions() {
        permissions.clear();
        String noAccountGroup = null;

//                = (String) ((Map)StarNub.getConfiguration().getConfiguration().get("groups")).get("no_account_group");
        Group group = Groups.getInstance().getGroupByName(noAccountGroup);
        loadPermissions(group);
    }

}
