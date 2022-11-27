package net.fradantim.platformertutorial.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

import net.fradantim.platformertutorial.Renderizable;
import net.fradantim.platformertutorial.Stadistic;
import net.fradantim.platformertutorial.StadisticsFeed;
import net.fradantim.platformertutorial.entities.Entity;
import net.fradantim.platformertutorial.entities.Entity.EntityLayer;
import net.fradantim.platformertutorial.entities.EntityLoader;
import net.fradantim.platformertutorial.entities.EntityType;
import net.fradantim.platformertutorial.entities.Hitable;
import net.fradantim.platformertutorial.world.map.GameMap;
import net.fradantim.platformertutorial.world.map.GameMap.Column;
import net.fradantim.platformertutorial.world.map.GameMap.Layer;
import net.fradantim.platformertutorial.world.map.GameMap.Row;

public abstract class GameMapController implements StadisticsFeed, Renderizable{

	protected Color COLOR_COLISSIONABLE_TILE = Color.WHITE;
	protected Color COLOR_GRID = Color.BLACK; 
	
	protected int CHUNK_SIZE = 8;
	
	private static final float GRAVITY=-9.8F;
	protected List<Entity> entities;
	
	protected GameMap map;

	private Entity player;
	
	public GameMapController() {
		entities = new CopyOnWriteArrayList<Entity>();
		
		entities.addAll(EntityLoader.loadEntities("entities", this));
		for (Entity entity : entities) {
			if (entity.getType().equals(EntityType.PLAYER))
				player= entity;
		}
	}
	
	public Entity getPlayer() {
		return player;
	}
	
	public void render(SpriteBatch batch, float globalScale) {
		renderEntities(batch, globalScale);
	}
	
	private void renderEntities(SpriteBatch batch, float globalScale) {
		for(EntityLayer entitylayer: EntityLayer.values()) {
			renderEntities(batch, globalScale, entitylayer);
		}
	}
	
	private void renderEntities(SpriteBatch batch, float globalScale, EntityLayer entitylayer) {
		for(Entity entity :  entities) {
			if(entity.getEntityLayer() == entitylayer) {
				entity.render(batch, globalScale);
			}
		}
	}
	
	public void renderAttributes(ShapeRenderer shapeRenderer, float globalScale) {
		renderGrid(shapeRenderer, globalScale);
		for(Entity entity : entities) {
			entity.renderAttributes(shapeRenderer, globalScale);
		}
		
		for(Layer layer : map.getCollisionLayers()) {
			renderLayerHitboxes(shapeRenderer, globalScale, layer, COLOR_COLISSIONABLE_TILE);
		}
	}
	
	private void renderLayerHitboxes(ShapeRenderer shapeRenderer, float globalScale, Layer layer, Color color) {
		shapeRenderer.setColor(color);
		shapeRenderer.begin(ShapeType.Line);
		
		int rowIndex=0;
		for(Row row: layer.getRows()) {
			int columnIndex=0;
			for(Column col: row.getColumns()) {
				if(col.hasTile()) {
					shapeRenderer.rect(
							columnIndex * getTileWidth(globalScale), rowIndex * getTileHeight(globalScale), 
							getTileWidth(globalScale), getTileHeight(globalScale));
					
					//diagonal \
					Vector2 start = new Vector2(columnIndex * getTileWidth(globalScale), rowIndex * getTileHeight(globalScale));
					Vector2 end = new Vector2( (columnIndex+1) * getTileWidth(globalScale), (rowIndex+1) * getTileHeight(globalScale));
					
					shapeRenderer.line(start, end);
					//diagonal /
					start = new Vector2( (columnIndex + 1) * getTileWidth(globalScale), rowIndex * getTileHeight(globalScale));
					end= new Vector2( (columnIndex) * getTileWidth(globalScale), (rowIndex + 1) * getTileHeight(globalScale));
					
					shapeRenderer.line(start, end);
				}
				columnIndex++;
			}
			rowIndex++;
		}
		
		shapeRenderer.end();
	}
	
	public void renderGrid(ShapeRenderer shapeRenderer, float globalScale) {
		shapeRenderer.setColor(COLOR_GRID);
		shapeRenderer.begin(ShapeType.Line);
		
		//vertical		
		Vector2 start = new Vector2(0,0);
		Vector2 end = new Vector2(0,getPixelHeight(globalScale));
		
		for(int columnIndex = 0; columnIndex < map.getWidth(); columnIndex+=CHUNK_SIZE) {
			for(Vector2 vector: Arrays.asList(start,end)) {
				vector.add(CHUNK_SIZE * map.getTileSize(globalScale).x, 0);
			}
			shapeRenderer.line(start, end);
		}
		
		//horizontal
		start = new Vector2(0,0);
		end = new Vector2(getPixelWidth(globalScale),0);
		
		for(int rowIndex = 0; rowIndex < map.getHeight(); rowIndex+=CHUNK_SIZE) {
			for(Vector2 vector: Arrays.asList(start,end)) {
				vector.add(0, CHUNK_SIZE * map.getTileSize(globalScale).y);
			}
			shapeRenderer.line(start, end);
		}
		
		shapeRenderer.end();
	}
	
	public void update (float deltaTime) {
		for(Entity entity :  entities ) {
			entity.update(deltaTime,GRAVITY);
		}
	}
	
	public abstract void dispose();
	
	public int getTileTypeByLocation(int layer, float x, float y) {
		return getTileByCoordinate(layer, (int) (x / getTileWidth()), (int) (y / getTileHeight()));
	}
	
	public int getTileTypeByLocation(int layer, Vector2 vector) {
		return getTileTypeByLocation(layer, vector.x, vector.y);
	}
	
	public float getTileWidth() {
		return getTileWidth(1F);
	}
	
	public abstract float getTileWidth(float globalScale);
	
	public float getTileHeight() {
		return getTileHeight(1F);
	}
	
	public float getTileSize() {
		return getTileSize(1F);
	}
	
	/**
	 * Devuelve un promedio entre el alto y ancho del tile 
	 */
	public float getTileSize(float globalScale) {
		return (getTileHeight(globalScale)+getTileWidth(globalScale))/2;
	}
	
	public abstract float getTileHeight(float globalScale);
	
	public abstract int getTileByCoordinate(int layer, int column, int row);
	
	public List<Entity> getEntityByPosition(Vector2 position){
		return getEntityByPosition(position, 1F);
	}
	
	public List<Entity> getEntityByPosition(Vector2 position, float globalScale){
		List<Entity> foundEntities = new ArrayList<>();
		for(Entity entity: entities) {
			if(entity.getArea(globalScale).contains(position)) {
				foundEntities.add(entity);
			}
		}
		return foundEntities;
	}
	
	public List<Hitable> getHitablesByPosition(Vector2 position){
		return getHitablesByPosition(position, 1F);
	}
	
	public List<Hitable> getHitablesByPosition(Vector2 position, float globalScale){
		List<Hitable> foundEntities = new ArrayList<>();
		for(Entity entity: getEntityByPosition(position, globalScale)) {
			if(entity instanceof Hitable) {
				foundEntities.add((Hitable) entity);
			}
		}
		return foundEntities;
	}
	
	/**
	 * Metodo para deteccion de colisiones
	 */
	public boolean doesRectCollideWithMap(float x, float y, int width, int height) {
		if( x < 0 || y < 0 || x + width > getPixelWidth() || y + height > getPixelHeight())
			return true;

		for(Layer layer : map.getCollisionLayers()) {
			for (int rowIndex = (int) (y / getTileHeight()); rowIndex < Math.ceil( (y + height) / getTileHeight()); rowIndex++ ) {
				Row row= layer.getRows().get(rowIndex);
				for (int colIndex = (int) (x / getTileWidth()); colIndex < Math.ceil( (x + width) / getTileWidth()); colIndex++ ) {
					Column col = row.getColumns().get(colIndex);
					if(col.hasTile()) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public abstract int getWidth();
	public abstract int getHeight();
	public abstract int getLayers();
	
	public float getPixelWidth() {
		return getPixelWidth(1F);
	}
	
	public float getPixelWidth(float globalScale) {
		return getWidth() * getTileWidth(globalScale);
	}
	
	public float getPixelHeight() {
		return getPixelHeight(1F);
	}
	
	public float getPixelHeight(float globalScale) {
		return getHeight() * getTileHeight(globalScale);
	}
	
	public void removeEntity(Entity entity) {
		entities.remove(entity);
		entity=null;
	}
	
	public void addEntity(Entity entity) {
		entities.add(entity);
	}
	
	@Override
	public List<Stadistic> getStadistics(float globalScale) {
		List<Stadistic> stadistics = new ArrayList<>();
		stadistics.add(new Stadistic(this,"#entities", entities.size()));
		stadistics.addAll(map.getStadistics(globalScale));
		stadistics.addAll(getPlayer().getStadistics(globalScale));
		return stadistics;
	}
}
