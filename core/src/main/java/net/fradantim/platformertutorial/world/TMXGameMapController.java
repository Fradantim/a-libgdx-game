package net.fradantim.platformertutorial.world;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.fradantim.platformertutorial.Stadistic;
import net.fradantim.platformertutorial.world.map.TMXMapLoader;

public class TMXGameMapController extends GameMapController {

	private TextureRegion[] tiles;

	public TMXGameMapController() throws ParserConfigurationException, SAXException, IOException {
		TMXMapLoader mapLoader = new TMXMapLoader();
		String levelFile = "maps/level1.tmx";
		map = mapLoader.generateFromTMX(levelFile);
		Gdx.app.debug("TMXGameMapController", "Map:" + levelFile + " loaded.");

		List<TextureRegion> tilesAsList = new ArrayList<>();

		TextureRegion[][] tileMatrix = TextureRegion.split(new Texture("tiles/tiles.png"), map.getTileWidth(),
				map.getTileHeight());

		tilesAsList.add(null); // ocupo la posicion 0 para que sea mas facil de usar el array
		for (int i = 0; i < tileMatrix.length; i++) {
			for (int j = 0; j < tileMatrix[i].length; j++) {
				tilesAsList.add(tileMatrix[i][j]);
			}
		}

		tiles = tilesAsList.toArray(new TextureRegion[tilesAsList.size()]);
	}

	@Override
	public void render(SpriteBatch batch, float globalScale) {
		batch.begin();

		renderLayers(batch, map.getLowerLayers(), globalScale);
		renderLayers(batch, map.getCollisionLayers(), globalScale);
		super.render(batch, globalScale);
		renderLayers(batch, map.getUpperLayers(), globalScale);

		batch.end();
	}

	private void renderLayers(SpriteBatch batch, Stream<int[][]> layers, float globalScale) {
		float tileWidth = getTileWidth();
		float tileHeight = getTileHeight();

		float scaledTileWidth = getTileWidth(globalScale);
		float scaledTileHeight = getTileHeight(globalScale);

		layers.forEach(layer -> {
			for (int r = 0; r < layer.length; r++) {
				for (int c = 0; c < layer[r].length; c++) {
					int tile = layer[r][c];
					if (tile > 0) {
						batch.draw(tiles[tile], c * scaledTileWidth, r * scaledTileHeight, 0, 0, tileWidth, tileHeight,
								globalScale, globalScale, 0F);
					}
				}
			}
		});
	}

	@Override
	public void update(float deltaTimee) {
		super.update(deltaTimee);
	}

	@Override
	public void dispose() {

	}

	@Deprecated // ?
	@Override
	public int getTileByCoordinate(int layer, int column, int row) {
		if (isOutOfScreen(column, row))
			return -1;
		return map.getMapContentIn(layer, column, row);
	}

	@Override
	public int getWidth() {
		return map.getWidth();
	}

	@Override
	public int getHeight() {
		return map.getHeight();
	}

	@Deprecated // ?
	@Override
	public int getLayers() {
//		return map.getLayers();
		return 0;
	}

	private boolean isOutOfScreen(int column, int row) {
		return column < 0 || column >= getWidth() || row < 0 || row >= getHeight();
	}

	@Override
	public List<Stadistic> getStadistics(float globalScale) {
		List<Stadistic> stadistics = new ArrayList<>();
		stadistics.add(new Stadistic(this, "#tiles", tiles.length));
		stadistics.addAll(super.getStadistics(globalScale));
		return stadistics;
	}

	@Override
	public float getTileWidth(float globalScale) {
		return map.getTileWidth() * globalScale;
	}

	@Override
	public float getTileHeight(float globalScale) {
		return map.getTileHeight() * globalScale;
	}
}
