package net.fradantim.platformertutorial;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public interface Renderizable {
	
	public void render(SpriteBatch batch, float globalScale);
	
	public void renderAttributes(ShapeRenderer shapeRenderer, float globalScale);
}
