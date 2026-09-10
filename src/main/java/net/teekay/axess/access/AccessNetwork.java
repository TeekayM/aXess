package net.teekay.axess.access;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.teekay.axess.AxessConfig;

import java.util.*;

public class AccessNetwork {
    private HashMap<UUID, AccessLevel> accessLevelsHashMap = new HashMap<>();
    private ArrayList<AccessLevel> accessLevels = new ArrayList<>();

    private HashMap<UUID, EnumSet<AccessPermission>> permissions = new HashMap<>();

    private final UUID uuid;
    private final UUID ownerUUID;

    public String name;

    public AccessNetwork(UUID ownerUUID) {
        this(ownerUUID, UUID.randomUUID());
    }

    public AccessNetwork(UUID ownerUUID, UUID forcedNetworkUUID) {
        this.uuid = forcedNetworkUUID;
        this.ownerUUID = ownerUUID;
        this.name = "New Access Network";
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }
    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();

        tag.putUUID("UUID", uuid);
        tag.putUUID("OwnerUUID", ownerUUID);
        tag.putString("Name", name);

        ListTag accessLevelListTag = new ListTag();

        sortPriorities();

        for (AccessLevel level : accessLevels) {
            accessLevelListTag.add(level.toNBT());
        }

        tag.put("AccessLevels", accessLevelListTag);

        ListTag permissionsTag = new ListTag();

        for (Map.Entry<UUID, EnumSet<AccessPermission>> entry : permissions.entrySet()) {

            CompoundTag permEntry = new CompoundTag();
            permEntry.putUUID("PlayerUUID", entry.getKey());

            ListTag perms = new ListTag();
            for (AccessPermission permission : entry.getValue()) {
                perms.add(StringTag.valueOf(permission.name()));
            }

            permEntry.put("Permissions", perms);
            permissionsTag.add(permEntry);
        }

        tag.put("PlayerPermissions", permissionsTag);

        return tag;
    }

    public static AccessNetwork fromNBT(CompoundTag tag) {
        UUID uuid = tag.getUUID("UUID");
        UUID ownerUUID = tag.getUUID("OwnerUUID");

        AccessNetwork newNetwork = new AccessNetwork(ownerUUID, uuid);

        newNetwork.name = tag.getString("Name");

        ListTag accessLevelList = (ListTag) tag.get("AccessLevels");

        for (int i = 0; i < accessLevelList.size(); i++) {
            AccessLevel level = AccessLevel.fromNBT((CompoundTag) accessLevelList.get(i));
            newNetwork.accessLevels.add(level);
            newNetwork.accessLevelsHashMap.put(level.getUUID(), level);
        }

        ListTag permEntries = (ListTag) tag.get("PlayerPermissions");

        if (permEntries != null) for (int i = 0; i < permEntries.size(); i++) {
            CompoundTag permTag = (CompoundTag) permEntries.get(i);
            EnumSet<AccessPermission> permissionEnumSet = EnumSet.noneOf(AccessPermission.class);

            ListTag perms = (ListTag) permTag.get("Permissions");
            if (perms != null) for (int j = 0; j < perms.size(); j++) {
                permissionEnumSet.add(AccessPermission.valueOf(((StringTag)perms.get(j)).getAsString()));
            }

            newNetwork.permissions.put(permTag.getUUID("PlayerUUID"), permissionEnumSet);
        }

        return newNetwork;
    }

    public ArrayList<AccessLevel> getAccessLevels() {
        return accessLevels;
    }

    public AccessLevel getAccessLevel(UUID uuid) {
        return accessLevelsHashMap.get(uuid);
    }

    public void addAccessLevel(AccessLevel level) {
        accessLevelsHashMap.put(level.getUUID(), level);
        accessLevels.add(level);
        sortPriorities();
    }

    public void replaceAccessLevel(AccessLevel level) {
        accessLevelsHashMap.put(level.getUUID(), level);
        for (int index = 0; index < accessLevels.size(); index++) {
            if (accessLevels.get(index).getUUID() == level.getUUID()) {
                accessLevels.set(index, level);
                break;
            }
        }
        sortPriorities();
    }

    public void removeAccessLevel(AccessLevel level) {
        accessLevelsHashMap.remove(level.getUUID());
        accessLevels.remove(level);

        sortPriorities();
    }
    public void removeAccessLevel(UUID uuid) {
        accessLevels.remove(uuid);
        accessLevelsHashMap.remove(uuid);

        sortPriorities();
    }

    public boolean hasAccessLevel(AccessLevel level) {
        return hasAccessLevel(level.getUUID());
    }
    public boolean hasAccessLevel(UUID uuid) {
        return accessLevelsHashMap.containsKey(uuid);
    }

    public void moveLevelToPriority(AccessLevel level, int desiredIndex) {
        if (!accessLevels.contains(level)) return;

        accessLevels.remove(level);

        int index = Math.max(0, Math.min(desiredIndex, accessLevels.size()));
        accessLevels.add(index, level);

        for (int i = 0; i < accessLevels.size(); i++) {
            accessLevels.get(i).setPriority(i);
        }
    }

    public void sortPriorities() {
        accessLevels.sort(Comparator.comparingInt(AccessLevel::getPriority));

        for (int index = 0; index < accessLevels.size(); index++) {
            accessLevels.get(index).setPriority(index);
        }
    }

    public boolean isOwnedBy(Player player) {
        return isOwnedBy(player.getUUID());
    }
    public boolean isOwnedBy(UUID playerUUID) {
        return playerUUID.equals(ownerUUID);
    }

    public HashMap<UUID, EnumSet<AccessPermission>> getPermissions() {
        return permissions;
    }

    public boolean hasPermission(UUID playerUUID, AccessPermission perm) {
        if (AxessConfig.experimentalLetEveryoneEditEverything) return true;
        if (isOwnedBy(playerUUID)) return true;
        if (!permissions.containsKey(playerUUID)) return false;

        return permissions.get(playerUUID).contains(perm);
    }
    public boolean hasPermission(Player player, AccessPermission perm) {
        return hasPermission(player.getUUID(), perm);
    }

    public Set<UUID> getPlayersInPerms() {
        return permissions.keySet();
    }

    public void setPermissions(UUID playerUUID, EnumSet<AccessPermission> perm) {
        permissions.put(playerUUID, perm);
    }
    public void setPermissions(Player player, EnumSet<AccessPermission> perm) {
        setPermissions(player.getUUID(), perm);
    }

    public void setAllPermissions(HashMap<UUID, EnumSet<AccessPermission>> permissions) {
        this.permissions = permissions;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AccessNetwork oLevel) {
            return (
                    uuid.equals(oLevel.getUUID())
            );
        }
        return super.equals(obj);
    }
}
