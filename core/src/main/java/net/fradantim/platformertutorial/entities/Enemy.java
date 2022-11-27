package net.fradantim.platformertutorial.entities;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import net.fradantim.platformertutorial.Stadistic;

public abstract class Enemy extends Shooter implements Hitable {
	
	private static final float DEFAULT_PLAYER_CATCH_RADIUS = 4F;
	private static final float DEFAULT_PLAYER_RELEASE_RADIUS = 5F;
	
	protected static final Color COLOR_CATCH_CIRCLE = Color.YELLOW;
	protected static final Color COLOR_RELEASE_CIRCLE = Color.BROWN;
	
	protected boolean hasPlayer = false;
	protected float playerCatchRadius = DEFAULT_PLAYER_CATCH_RADIUS;
	protected float playerReleaseRadius = DEFAULT_PLAYER_RELEASE_RADIUS;

	@Override
	public void update(float deltaTime, float gravity) {
		hasPlayer=seesPlayer();
		if(hasPlayer) {
			changeDirection((getX()>map.getPlayer().getX())? Direction.LEFT : Direction.RIGHT);
		}
		super.update(deltaTime, gravity); //Apply gravity
	}

	private boolean seesPlayer() {
		Entity player = map.getPlayer();
		if(player != null) {
			if(Gdx.input.isKeyJustPressed(Keys.ENTER) && this.getId() == 2) {
				System.out.println("IN");
			}
			float distance = getDistanceToEntity(player);
			if( ( distance < getPlayerCatchRadiusSize(1F) || (hasPlayer && distance <= getPlayerReleaseRadiusSize(1F))) && canSeeEntity(player)) {
				return true;
			}
			if(distance > getPlayerReleaseRadiusSize(1F) || !canSeeEntity(player)) {
				return false;
			}
		}
		return false;
	}
	
	
	
	@Override
	public void renderAttributes(ShapeRenderer shapeRenderer, float globalScale) {
		//TODO RADIAL ENEMY un radio donde ve o deja de ver al jugador
		//Mostrar ? para indicar que no ve al player
		super.renderAttributes(shapeRenderer, globalScale);
		
		//radio catch
		renderCircle(shapeRenderer, globalScale, getPlayerCatchRadiusSize(1F), COLOR_CATCH_CIRCLE);
		
		//radio release
		renderCircle(shapeRenderer, globalScale, getPlayerReleaseRadiusSize(1F), COLOR_RELEASE_CIRCLE);
	}
	
	@Override
	public List<Stadistic> getStadistics(float globalScale) {
		List<Stadistic> stadistics = new ArrayList<>();
		stadistics.addAll(super.getStadistics(globalScale));
		stadistics.add(new Stadistic(this,"hasPlayer", hasPlayer));
		stadistics.add(new Stadistic(this,"dst2Player",getDistanceToEntity(map.getPlayer())));
		stadistics.add(new Stadistic(this,"catchRadius", "("+playerCatchRadius+") "+getPlayerCatchRadiusSize(1F)+">"+getPlayerCatchRadiusSize(globalScale)));
		stadistics.add(new Stadistic(this,"releaseRadius", "("+playerReleaseRadius+") "+getPlayerReleaseRadiusSize(1F)+">"+getPlayerReleaseRadiusSize(globalScale)));
		return stadistics;
	}	
	
	public float getPlayerCatchRadiusSize(float globalScale) {
		return getPlayerCatchRadius(globalScale)*map.getTileSize();
	}
	
	public float getPlayerCatchRadius(float globalScale) {
		return playerCatchRadius*globalScale;
	}

	public void setPlayerCatchRadius(float playerCatchRadius) {
		this.playerCatchRadius = playerCatchRadius;
	}

	
	public float getPlayerReleaseRadiusSize(float globalScale) {
		return getPlayerReleaseRadius(globalScale)*map.getTileSize();
	}
		
	public float getPlayerReleaseRadius(float globalScale) {
		return playerReleaseRadius*globalScale;
	}

	public void setPlayerReleaseRadius(float playerReleaseRadius) {
		this.playerReleaseRadius = playerReleaseRadius;
	}
}
