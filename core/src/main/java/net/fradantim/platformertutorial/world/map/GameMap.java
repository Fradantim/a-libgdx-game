package net.fradantim.platformertutorial.world.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import com.badlogic.gdx.math.Vector2;

import net.fradantim.platformertutorial.Stadistic;
import net.fradantim.platformertutorial.StadisticsFeed;

public class GameMap implements StadisticsFeed {

	/**
	 * Solamente habra colision en la layer 2
	 */
	// private static final int COLLISION_LAYER = 2;

	private int tileHeight;

	private int tileWidth;

	// layer -> x -> y
	private int[][][] layers;

	public GameMap(int[][][] layers, int tileHeight, int tileWidth) {
		this.layers = layers;
		this.tileHeight = tileHeight;
		this.tileWidth = tileWidth;
	}
	
	public int getWidth() {
		return layers[0][0].length;
	}

	public int getHeight() {
		return layers[0].length;
	}
//
//	public int getLayers() {
//		return layers.size();
//	}

	@Deprecated //? 
	public int getMapContentIn(int layer, int column, int row) {
		return layers[layer][row][column];
	}

	public Stream<int[][]> getLowerLayers() {
		return Stream.of(layers[0], layers[1]);
	}

	public Stream<int[][]> getCollisionLayers() {
		return Stream.ofNullable(layers[2]);
	}

	public Stream<int[][]> getUpperLayers() {
		return Stream.ofNullable(layers[3]);
	}

	public int getTileWidth() {
		return tileWidth;
	}

	public int getTileHeight() {
		return tileHeight;
	}

	public Vector2 getTileSize() {
		return getTileSize(1F);
	}

	public Vector2 getTileSize(float globalScale) {
		return new Vector2(tileWidth, tileHeight).scl(globalScale);
	}

	@Override
	public List<Stadistic> getStadistics(float globalScale) {
		return Arrays.asList(new Stadistic(this, "TilePixSize(W,H)", getTileSize() + ">" + getTileSize(globalScale)),
				new Stadistic(this, "TileLength(W/H/L)",
						"(" + getWidth() + "/" + getHeight() + "/" + layers.length + ")"));
	}
}
