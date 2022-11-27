package net.fradantim.platformertutorial.world;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.fradantim.platformertutorial.Stadistic;
import net.fradantim.platformertutorial.world.map.GameMap.Column;
import net.fradantim.platformertutorial.world.map.GameMap.Layer;
import net.fradantim.platformertutorial.world.map.GameMap.Row;
import net.fradantim.platformertutorial.world.map.TMXMapLoader;

public class TMXGameMapController extends GameMapController{
	
	private List<TextureRegion> tiles;
	
	public TMXGameMapController() throws ParserConfigurationException, SAXException, IOException {
		TMXMapLoader mapLoader= new TMXMapLoader();
		String levelFile= "maps/level1.tmx";
		map = mapLoader.generateFromTMX(levelFile);
		Gdx.app.debug("TMXGameMapController", "Map:"+levelFile+" loaded.");
		
		tiles = new ArrayList<>();
		
		TextureRegion[][] tileMatrix = TextureRegion.split(new Texture("tiles/tiles.png"), map.getTilePixelWidth(), map.getTilePixelHeight());
		
		for(int i=0; i< tileMatrix.length ; i++) {
			for(int j=0; j< tileMatrix[i].length ; j++) {
				tiles.add(tileMatrix[i][j]);
			}
		}
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
	
	private void renderLayers(SpriteBatch batch, List<Layer> layers, float globalScale) {
		for(Layer layer : layers) {
			int rowIndex=0;
			for(Row row: layer.getRows()) {
				int columnIndex=0;
				for(Column col: row.getColumns()) {
					if(col.hasTile()) {
						batch.draw(tiles.get(col.getId()),
								columnIndex * getTileWidth(globalScale), rowIndex * getTileHeight(globalScale),
								0, 0, 
								getTileWidth(), getTileHeight(), 
								globalScale, globalScale,
								0F);
					}
					columnIndex++;
				}
				rowIndex++;
			}
		}
	}

	@Override
	public void update(float deltaTimee) {
		super.update(deltaTimee);
	}

	@Override
	public void dispose() {

	}

	@Override
	public int getTileByCoordinate(int layer, int column, int row) {
		if(isOutOfScreen(column, row))
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

	@Override
	public int getLayers() {
		return map.getLayers();
	}

	private boolean isOutOfScreen(int column, int row) {
		return column< 0 || column >= getWidth() || row <0 || row >= getHeight();
	}


	@Override
	public List<Stadistic> getStadistics(float globalScale) {
		List<Stadistic> stadistics = new ArrayList<>();
		stadistics.add(new Stadistic(this,"#tiles", tiles.size()));
		stadistics.addAll(super.getStadistics(globalScale));
		return stadistics;
	}


	@Override
	public float getTileWidth(float globalScale) {
		return map.getTilePixelWidth()*globalScale;
	}

	@Override
	public float getTileHeight(float globalScale) {
		return map.getTilePixelHeight()*globalScale;
	}
}
