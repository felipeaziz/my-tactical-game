package com.ibra.tacticalrpg.item.equipment;

import com.ibra.tacticalrpg.item.Item;
import com.ibra.tacticalrpg.item.ItemType;
import com.ibra.tacticalrpg.job.Job;

import java.util.List;
import java.util.Map;

public abstract class Equipment extends Item {
    protected final EquipmentSlot slot;
    protected Map<String, Integer> statBonuses;
    protected List<Job> allowedJobs;

    public Equipment(String name, String description, ItemType type, EquipmentSlot slot) {
        super(name, description, type);
        this.slot = slot;
    }

    public EquipmentSlot getSlot() {
        return slot;
    }

    public int getStatBonus(String stat) {
        return statBonuses.get(stat) != null ? statBonuses.get(stat) : 0;
    }
}
