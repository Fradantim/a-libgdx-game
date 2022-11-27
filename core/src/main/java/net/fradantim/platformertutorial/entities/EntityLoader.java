package net.fradantim.platformertutorial.entities;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

import net.fradantim.platformertutorial.world.GameMapController;

public class EntityLoader {
	
	private static Json json = new Json();
	
	public static List<Entity> loadEntities (String id, GameMapController map){
		//Gdx.files.internal(id+".json").file().mkdirs();
		FileHandle file = Gdx.files.internal(id+".json");
		
		if(file.exists()) {
			EntitySnapshot[] snapshots = json.fromJson(EntitySnapshot[].class, file.readString());
			List<Entity> entities = new ArrayList<>();
			for (EntitySnapshot snap : snapshots) {
				entities.add(EntityType.createEntityUsingSnapshot(snap, map));
			}
			return entities;
		} else {
			Gdx.app.error("Entity loader", "Could not load entities.");
			return null;
		}
	}
}
