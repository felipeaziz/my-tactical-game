package com.ibra.tacticalrpg.action;

import com.ibra.tacticalrpg.entities.Entity;

public interface Action {
    void execute(Entity actor, Entity target);
}
