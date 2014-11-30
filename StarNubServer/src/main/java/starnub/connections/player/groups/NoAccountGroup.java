package starnub.connections.player.groups;

import starnub.StarNub;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class NoAccountGroup {


    private volatile String name;
    private volatile Tag tag;

    public String getName() {
        return name;
    }

    public Tag getTag() {
        return tag;
    }

    /**
     * Represents the permissions an account has. They are stored in a tree for mate.
     * <p>
     * Example: commandname -|
     *                          -sub_permissions
     *                                            - commands
     *
     * starnub.whisper.pm
     * starnub.whisper.msg
     * starnub.whisper.w
     * starnub.whisper.whisper
     * Where "starnub" is the command name, "whisper" is the sub permission, pm, msg, w, whisper are the commands that can be
     * used to invoke the whisper command. If an account only has starnub.whisper.pm, then only /sn pm, /starnub pm or /pm would
     * work. In this ConcurrentHashMap.... ConcurrentHashMap<{commandname}ConcurrentHashMap<subpermission, ArrayList<commands>>>
     * <p>
     * Lets take the above commands and a few more and show that the tree would look like. starboundmanager.kick.kick, starboundmanager.admin.broadcast,
     * starboundmanager.admin.killplayer, someplugin.chatness.chatcolors
     * starnub -|
     *          |-whisper |- List containing (w, msg, pm, whisper)
     * starboundmanager -|
     *            |- kick  |- List containing kick
     *            |- admin |- List containing broadcast, killplayer
     * someplugin -|
     *             |- chatness |- chatcolors
     */
    private volatile ConcurrentHashMap<String, ConcurrentHashMap<String, ArrayList<String>>> permissions;

    public NoAccountGroup(){
        String noAccountGroup = "";
//        Map<String, Object> groups = StarNub.getStarboundServer().getConnectionss().getGroupSync().getGroups();
//        for (String groupName : groups.keySet()){
//            Map groupMap = (Map) groups.get(groupName);
//            if (((String) groupMap.get("type")).equalsIgnoreCase("noaccount")){
//                noAccountGroup = groupName;
//            }
//        }
        Group group = StarNub.getDatabaseTables().getGroups().getGroupByName(noAccountGroup);
        loadPermissions(group);
        this.name = group.getLadderName();
        this.tag = group.getTag();
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

    public void deletePermission(String permission){
        String[] permissionBreak = permission.split("\\.", 3);
        if (permissionBreak.length == 3) {
            permissions.get(permissionBreak[0]).get(permissionBreak[1]).remove(permissionBreak[2]);
            if (permissions.get(permissionBreak[0]).get(permissionBreak[1]).isEmpty()) {
                permissions.get(permissionBreak[0]).remove(permissionBreak[1]);
                if (permissions.get(permissionBreak[0]).isEmpty()){
                    permissions.remove(permissionBreak[0]);
                }
            }
        } else if (permissionBreak.length == 2 ) {
            permissions.get(permissionBreak[0]).remove(permissionBreak[1]);
            if (permissions.get(permissionBreak[0]).isEmpty()) {
                permissions.remove(permissionBreak[0]);
            }
        } else if (permissionBreak.length == 1){
            permissions.remove(permissionBreak[0]);
        }
    }

    public void reloadPermissions() {
        permissions.clear();
        String noAccountGroup = null;

//                = (String) ((Map)StarNub.getConfiguration().getConfiguration().get("groups")).get("no_account_group");
        Group group = StarNub.getDatabaseTables().getGroups().getGroupByName(noAccountGroup);
        loadPermissions(group);
    }

    public void loadPermissions(Group group){
        permissions = new ConcurrentHashMap<String, ConcurrentHashMap<String, ArrayList<String>>>();
        HashSet<String> permissionsToLoad = new HashSet<String>();
        List<GroupPermission> groupPermissionList = StarNub.getDatabaseTables().getGroupPermissions().getGroupPermissions(group);
        permissionsToLoad.addAll(groupPermissionList.stream().map(GroupPermission::getPermission).collect(Collectors.toList()));
        HashSet<GroupPermission> groupAssignmentHashSet = new HashSet<>();
        permissionsToLoad.addAll(groupAssignmentHashSet.stream().map(GroupPermission::getPermission).collect(Collectors.toList()));
        permissionsToLoad.forEach(this::addPermissionToMap);
    }

    private void addPermissionToMap(String permissionFinal){
        String[] permissionBreak = permissionFinal.split("\\.", 3);
        if (permissionBreak.length == 3) {
            permissions.putIfAbsent(permissionBreak[0], new ConcurrentHashMap<String, ArrayList<String>>());
            permissions.get(permissionBreak[0]).putIfAbsent(permissionBreak[1], new ArrayList<String>());
            permissions.get(permissionBreak[0]).get(permissionBreak[1]).add(permissionBreak[2]);
        } else if (permissionBreak.length == 2 ) {
            permissions.putIfAbsent(permissionBreak[0], new ConcurrentHashMap<String, ArrayList<String>>());
            permissions.get(permissionBreak[0]).putIfAbsent(permissionBreak[1], new ArrayList<String>());
        } else if (permissionBreak.length == 1){
            permissions.putIfAbsent(permissionBreak[0], new ConcurrentHashMap<String, ArrayList<String>>());
        }
    }

    public boolean hasBasePermission(String basePermission){
        return permissions.containsKey("*") || permissions.containsKey(basePermission);
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will return if a player has a permission. It first for a wildcard "*" basically OP,
     * Then this method will see if the person even has the plugin permission. If yes, then we check to see if
     * they have the wildcard "*" for all of that plugins permissions. If not we check for that plugins sub permission,
     * If they have the sub permission, we then check the wildcard "*" for all of the commands or permission ends that
     * fall under that plugins sub permission. If no wild card, we check to see if they have the actual command.
     * <P>
     * Example: {plugname}.{subpermission}.{command}
     *          starnub.starbounddata.packets.starbounddata.packets.starnub.shutdown
     *          starnub.starbounddata.packets.starbounddata.packets.starnub.start
     *          starnub.starbounddata.packets.starbounddata.packets.starnub.*
     *          starnub.*
     * <br>
     * The first line would give me shutdown. The second line would be start. The third line give me anything under
     * starnub.starbounddata.packets.starbounddata.packets.starnub The fourth line gives me all permissions for the starnub plugin.
     *
     * <p>
     * @param startPermissionString String representing the plugin command name to check
     * @param subPermissionString String representing the plugins command specific sub permission
     * @param endPermissionString String representing the plugins sub permission specific Command
     * @return boolean if the account has the permission
     */
    public boolean hasPermission(String startPermissionString, String subPermissionString, String endPermissionString, boolean fullPermission, boolean checkWildCards) {
        if (checkWildCards) {
            try {
                if (permissions.containsKey("*")) {
                    return true;
                }
                if (!fullPermission) {
                    if (permissions.get(startPermissionString).containsKey(subPermissionString)
                            || permissions.get(startPermissionString).containsKey("*")) {
                        return true;
                    }
                }
                if (permissions.get(startPermissionString).get(subPermissionString).contains("*")
                        || permissions.get(startPermissionString).get(subPermissionString).contains(endPermissionString))
                    return true;
            } catch (NullPointerException e) {
                return false;
            }
        } else {
            try {
                if (!fullPermission) {
                    if (permissions.get(startPermissionString).containsKey(subPermissionString)) {
                        return true;
                    }
                }
                if (permissions.get(startPermissionString).get(subPermissionString).contains(endPermissionString))
                    return true;
            } catch (NullPointerException e) {
                return false;
            }
        }
        return false;
    }

    public ArrayList<String> getPermission(String startPermissionString, String subPermissionString, String endPermissionString) {
        try {
            return permissions.get(startPermissionString).get(subPermissionString);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public ArrayList<String> getPermission(String permission){
        String perm3 = null;
        String[] perms;
        try {
            perms = permission.split("\\.", 3);
            perm3 = perms[2];
        } catch (ArrayIndexOutOfBoundsException e) {
            perms = permission.split("\\.", 2);
        }
        return getPermission(perms[0], perms[1], perm3);
    }

    //Only needs the command, sub permission
    public String getPermissionSpecific(String startPermissionString, String subPermissionString) {
        try {
            return permissions.get(startPermissionString).get(subPermissionString).get(0);
        } catch (NullPointerException e) {
            return null;
        }
    }

    //only needs the first parts {base}.{sub}
    public String getPermissionSpecific(String permission){
        String[] strings = permission.split("\\.", 3);
        return getPermissionSpecific(strings[0], strings[1]);
    }


}
