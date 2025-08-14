package com.ibra.tacticalrpg.action;

import com.ibra.tacticalrpg.entities.Entity;
import com.ibra.tacticalrpg.item.consumable.ConsumableItem;
import com.ibra.tacticalrpg.map.isometric.GameMap;
import com.ibra.tacticalrpg.map.isometric.Tile;

public class ItemUseAction implements Action {
    private final ConsumableItem item;
    private final Tile targetTile;
    private final GameMap gameMap;

    public ItemUseAction(ConsumableItem item, Tile targetTile, GameMap gameMap) {
        this.item = item;
        this.targetTile = targetTile;
        this.gameMap = gameMap;
    }

    public ItemUseAction(GameMap gameMap, ConsumableItem item) {
        this(item, null, gameMap); // Para items usados em si mesmo
    }

    @Override
    public void execute(Entity actor, Entity target) {
        if(item == null || actor == null) {
            return;
        }
        Entity actualTarget = target;
        if (targetTile != null && targetTile.isOccupied()) {
            actualTarget = targetTile.getOccupant();
        } else if (targetTile == null) {
            actualTarget = actor; // Usa em si mesmo
        }
        if (actualTarget == null) {
            System.out.println("Alvo inválido para usar o item!");
            return;
        }

        if (actor.getPersonalInventory().removeItem(item, 1)) {
            item.use(actualTarget);
            System.out.println(actor.getName() + " usou " + item.getName() +
                (actualTarget == actor ? " em si mesmo" : " em " + actualTarget.getName()));
        } else {
            System.out.println("Não foi possível usar o item " + item.getName());
        }
    }

    public ConsumableItem getItem() {
        return item;
    }
}
