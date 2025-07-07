package com.ibra.tacticalrpg.entities;

import com.ibra.tacticalrpg.ai.Task;
import com.ibra.tacticalrpg.ai.EnemyBehaviorTree;
import com.ibra.tacticalrpg.job.Job;

import java.util.List;

public class EnemyEntity extends Entity {
    private final Task<EnemyEntity> behaviorTree;

    public EnemyEntity(String name, EntityStats stats, Job job) {
        super(name, stats, job);
        this.behaviorTree = EnemyBehaviorTree.createBehaviorTree();
    }

    @Override
    public void takeTurn() {
        if (!isAlive()) return;

        // Executa a árvore de comportamento
        behaviorTree.setObject(this);
        behaviorTree.run();
    }
}
