package com.ibra.tacticalrpg.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ibra.tacticalrpg.controller.SkillMenuController;
import com.ibra.tacticalrpg.skill.Skill;

import java.util.List;

public class SkillMenuRenderer extends BaseMenuRenderer {

    public SkillMenuRenderer(BitmapFont font, ShapeRenderer shapeRenderer) {
        super(font, shapeRenderer);
    }

    public void renderSkillMenu(SpriteBatch batch, SkillMenuController skillController) {
        if (skillController.getMenuState() != SkillMenuState.SELECTING_SKILL) {
            return;
        }
        List<Skill> skills = skillController.getSkills();
        if (skills == null || skills.isEmpty()) {
            return;
        }

        clearItemBounds();
        String title = "Selecione uma Habilidade:";
        String[] menuSkills = new String[skills.size()];
        for (int i = 0; i < skills.size(); i++) {
            menuSkills[i] = (i + 1) + ". " + skills.get(i).getName();
        }
        String instructions = "Clique na habilidade para selecionar | Esc: Cancelar";

        // Calcular largura máxima considerando todos os textos
        float maxWidth = Math.max(
            calculateMaxWidth(new String[]{title, instructions}),
            calculateMaxWidth(menuSkills)
        );

        float boxWidth = maxWidth + 2 * PADDING;
        float boxHeight = LINE_HEIGHT * (skills.size() + 3) + PADDING * 2; // +3 para título e instruções
        float boxX = 95f;
        float boxY = boxHeight + 20f;

        renderMenuBox(batch, boxX, boxY, boxWidth, boxHeight);

        batch.begin();
        float y = boxY - PADDING - LINE_HEIGHT;

        // Título
        font.setColor(Color.YELLOW);
        font.draw(batch, title, boxX + PADDING, y);
        y -= LINE_HEIGHT;

        // Skills
        for (int i = 0; i < menuSkills.length; i++) {
            addItemBound(
                boxX + PADDING,
                y - LINE_HEIGHT,
                boxWidth - 2 * PADDING,
                LINE_HEIGHT
            );

            boolean isHighlighted = i == getClickedIndex(Gdx.input.getX(), Gdx.input.getY());
            renderMenuOption(batch, menuSkills[i], boxX, y, boxWidth, isHighlighted);
            y -= LINE_HEIGHT;
        }

        // Instruções
        y -= LINE_HEIGHT * 0.5f;
        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, instructions, boxX + PADDING, y);

        batch.end();
        font.setColor(Color.WHITE);
    }
}
