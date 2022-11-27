package net.fradantim.platformertutorial.world.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

import net.fradantim.platformertutorial.Stadistic;
import net.fradantim.platformertutorial.StadisticsFeed;

public class GameMap implements StadisticsFeed{

	public class Layer {
		private List<Row> rows;

		public Layer() {
			rows = new ArrayList<>();
		}
		
		public List<Row> getRows() {
			return rows;
		}
		
		public void addRow(Row row) {
			rows.add(row);
		}
	}
	
	public class Row {
		private List<Column> columns;
		
		public Row() {
			columns = new ArrayList<>();
		}
		
		public List<Column> getColumns() {
			return columns;
		}
		
		public void addColumn(Column column) {
			columns.add(column);
		}
	}
	
	public class Column {
		private int id;
		
		public Column (int id) {
			this.id=id;
		}
		
		public int getId() {
			return id-1;
		}
		
		public boolean hasTile() {
			return getId()>0;
		}
	}
		
	/**
	 * Solamente habra colision en la layer 2
	 */
	private static final int COLLISION_LAYER = 2;
	
	private String name;
	
	private int width;

	private int height;
	
	private int tilePixelWidth;
	
	private int tilePixelHeight;
	
	private List<Layer> layers;
	
	public GameMap() {
		layers = new ArrayList<>();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getLayers() {
		return layers.size();
	}
	
	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public void addLayer(Layer layer) {
		layers.add(layer);
	}
	
	public int getMapContentIn(int layer, int column, int row) {
		return layers.get(layer).getRows().get(row).getColumns().get(column).getId();
	}
	
	public List<Layer> getLowerLayers(){
		return layers.subList(0, COLLISION_LAYER);
	}
	
	public List<Layer> getCollisionLayers(){
		return layers.subList(COLLISION_LAYER, COLLISION_LAYER+1);
	}
	
	public List<Layer> getUpperLayers(){
		return layers.subList(COLLISION_LAYER+1, layers.size());
	}

	public int getTilePixelWidth() {
		return tilePixelWidth;
	}

	public void setTilePixelWidth(int tilePixelWidth) {
		this.tilePixelWidth = tilePixelWidth;
	}
	
	public int getTilePixelHeight() {
		return tilePixelHeight;
	}
	
	public Vector2 getTileSize() {
		return getTileSize(1F);
	}
	
	public Vector2 getTileSize(float globalScale) {
		return new Vector2(tilePixelWidth,tilePixelHeight).scl(globalScale);
	}

	public void setTilePixelHeight(int tilePixelHeight) {
		this.tilePixelHeight = tilePixelHeight;
	}
	
	@Override
	public List<Stadistic> getStadistics(float globalScale) {
		return Arrays.asList(
				new Stadistic(this,"TilePixSize(W,H)", getTileSize()+">"+getTileSize(globalScale)),
				new Stadistic(this,"TileLength(W/H/L)", "("+getWidth()+"/"+getHeight()+"/"+getLayers()+")")
			);
	}	
}
