package net.teekay.axess.access;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;

import java.util.*;

public class AccessNetwork {
    private HashMap<UUID, AccessLevel> accessLevelsHashMap = new HashMap<>();
    private ArrayList<AccessLevel> accessLevels = new ArrayList<>();

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

        ListTag list = new ListTag();

        sortPriorities();

        for (AccessLevel level : accessLevels) {
            list.add(level.toNBT());
        }

        tag.put("AccessLevels", list);

        System.out.println(tag);

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

        return newNetwork;
    }

    public ArrayList<AccessLevel> getAccessLevels() {
        return accessLevels;
    }

    public AccessLevel getAccessLevel(UUID uuid) {
        return accessLevelsHashMap.get(uuid);
    }

    public void sortPriorities() {
        accessLevels.sort(Comparator.comparingInt(AccessLevel::getPriority));

        for (int index = 0; index < accessLevels.size(); index++) {
            accessLevels.get(index).setPriority(index);
        }
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

    public void moveLevelToPriority(AccessLevel level, int desiredIndex) {
        if (!accessLevels.contains(level)) return;

        accessLevels.remove(level);

        int index = Math.max(0, Math.min(desiredIndex, accessLevels.size()));
        accessLevels.add(index, level);

        for (int i = 0; i < accessLevels.size(); i++) {
            accessLevels.get(i).setPriority(i);
        }
    }

    public boolean isOwnedBy(Player player) {
        return player.getUUID().equals(ownerUUID);
    }

}
