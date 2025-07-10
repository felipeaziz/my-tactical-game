package com.ibra.tacticalrpg.entities;

import com.badlogic.gdx.graphics.Texture;
import com.ibra.tacticalrpg.ai.EnemyBehaviorTree;
import com.ibra.tacticalrpg.ai.Task;
import com.ibra.tacticalrpg.job.Job;

public class EnemyEntity extends Entity {
    private final Task<EnemyEntity> behaviorTree;
    private transient Texture texture;

    public EnemyEntity(String name, Job job) {
        super(name, job);
        this.behaviorTree = EnemyBehaviorTree.createBehaviorTree();
        this.texture = new Texture("job/enemy/" + job.getName().toLowerCase() + ".png");
    }

    @Override
    public void takeTurn() {
        if (!isAlive()) return;

        // Executa a árvore de comportamento
        behaviorTree.setObject(this);
        behaviorTree.run();
    }

    @Override
    public Texture getTexture() {
        return texture;
    }
}
