package com.ibra.tacticalrpg.entities;

import com.ibra.tacticalrpg.ai.EnemyBehaviorTree;
import com.ibra.tacticalrpg.ai.Task;
import com.ibra.tacticalrpg.job.Job;

public class EnemyEntity extends Entity {
    private final Task<EnemyEntity> behaviorTree;

    public EnemyEntity(String name, Job job) {
        super(name, job);
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
