package starnubserver.connections.player.groups;

import java.util.HashSet;

public class NoAccountGroup {

    /**
     * Represents the only instance of this class - Singleton Pattern
     */
    private final static NoAccountGroup instance = new NoAccountGroup();

    private volatile String name;
    private volatile Tag tag;
    private final HashSet<String> GROUP_PERMISSIONS = new HashSet<>();

    /**
     * This constructor is private - Singleton Pattern
     */
    private NoAccountGroup(){
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public HashSet<String> getGROUP_PERMISSIONS() {
        return GROUP_PERMISSIONS;
    }
}
