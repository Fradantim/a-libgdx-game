package net.fradantim.platformertutorial.entities;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import net.fradantim.platformertutorial.world.GameMapController;

public enum EntityType {

	PLAYER(Player.class,40),
	BULLET(Bullet.class,2),
	ENEMY_MAGE(EnemyMage.class,30);
	
	private String id;
	private Class loaderClass;
	private float weight;
	
	private EntityType(Class loaderClass, float weight) {
		this.id = loaderClass.getSimpleName();
		this.loaderClass = loaderClass;
		this.weight = weight;
	}

	public String getId() {
		return id;
	}

	public float getWeight() {
		return weight;
	}
	
	public static Entity createEntityUsingSnapshot (EntitySnapshot entitySnapshot, GameMapController map) {
		EntityType type = entityTypes.get(entitySnapshot.getType());
		try {
			Entity entity = (Entity) ClassReflection.newInstance(type.loaderClass);
			entity.create(entitySnapshot, type, map);;
			return entity;
		} catch (ReflectionException e) {
			Gdx.app.error("Entity Loader", "Could not load entity of type " + type.id);
			return null;
		}
	}
	
	private static Map<String,EntityType> entityTypes;
	
	static {
		entityTypes = new HashMap<>();
		for(EntityType type : values()) {
			entityTypes.put(type.id, type);
		}
	}
}
