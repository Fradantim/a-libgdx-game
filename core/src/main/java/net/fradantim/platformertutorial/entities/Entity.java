package net.fradantim.platformertutorial.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import net.fradantim.platformertutorial.Renderizable;
import net.fradantim.platformertutorial.Stadistic;
import net.fradantim.platformertutorial.StadisticsFeed;
import net.fradantim.platformertutorial.entities.animation.AnimatedState;
import net.fradantim.platformertutorial.entities.animation.AnimationImpl;
import net.fradantim.platformertutorial.world.GameMapController;

public abstract class Entity implements StadisticsFeed, Renderizable {

	public enum EntityLayer {
		/** Elementos fijos del fondo, todo lo tapa */
		BACK,
		/** elementos fijos o moviles */
		BEHIND,
		/** Elementos moviles, tapa todo */
		FRONT;

		private EntityLayer lower, upper;

		static {
			BACK.lower = BACK;
			BACK.upper = BEHIND;

			BEHIND.lower = BACK;
			BEHIND.upper = FRONT;

			FRONT.lower = BEHIND;
			FRONT.upper = FRONT;
		}

		public EntityLayer getLower() {
			return lower;
		}

		public EntityLayer getUpper() {
			return upper;
		}

		public EntityLayer getByName(String name) {
			for (EntityLayer el : values())
				if (el.name().equals(name))
					return el;
			return null;
		}
	}

	protected EntityLayer entityLayer = EntityLayer.FRONT;

	protected enum Direction {
		LEFT, RIGHT;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (other == this)
			return true;
		if (!(other instanceof Entity))
			return false;
		return this.getId() == ((Entity) other).getId();
	}

	/** colores */
	protected final static Color COLOR_HITBOX = Color.GREEN;
	protected final static Color COLOR_DIRECTION = Color.RED;

	private static int idGenerator = 0;
	private int id = idGenerator++;
	private static final Float DEFAULT_SCALE = 1F;
	private static final Float DEFAULT_SPEED = 1F;
	private static final Float DEFAULT_MAX_VELOCITY_X = 50F;
	private static final Float DEFAULT_ACCELERATION_X = 2.5F;
	protected final static Float ANIMATION_FRAME_DURATION = 1 / 2F;

	protected Map<AnimatedState, AnimationImpl> animations;
	private AnimatedState currentState;

	protected Vector2 pos;
	protected EntityType type;
	protected float velocityX = 0F;
	protected float velocityY;
	protected GameMapController map; // para evaluar colisiones
	protected boolean grounded = false;
	protected float scale = DEFAULT_SCALE;
	protected float speed = DEFAULT_SPEED;
	protected Float accelerationX = DEFAULT_ACCELERATION_X;

	protected Direction direction = Direction.RIGHT;

	public void create(EntitySnapshot snapshot, EntityType type, GameMapController map) {
		create(new Vector2(snapshot.getX(), snapshot.getY()), type, map);
		this.scale = snapshot.getFloat("scale", DEFAULT_SCALE);
		this.speed = snapshot.getFloat("scale", DEFAULT_SPEED);
	}

	public void create(Vector2 pos, EntityType type, GameMapController map) {
		this.pos = pos;
		this.type = type;
		this.map = map;
		loadAnimations();
		currentState = getDefaultAnimatedState();
	}

	/**
	 * Aplica gravedad
	 */
	public void update(float deltaTime, float gravity) {
		if (isDead())
			return;
		// aceleracion vertical
		float newY = pos.y;

		this.velocityY += gravity * deltaTime * getWeight(); // cuanto me muevo por segundo en eje Y
		newY += velocityY * deltaTime * scale;

		if (map.doesRectCollideWithMap(getX(), newY, getWidth(), getHeight())) {
			if (velocityY < 0) {
				this.pos.y = (float) Math.floor(pos.y);
				grounded = true;
			}
			this.velocityY = 0;
		} else {
			this.pos.y = newY;
			grounded = false;
		}
	}

	public final void render(SpriteBatch batch, float globalScale) {
		if (isDead())
			return;

		TextureRegion actualTexture = getActualTexture();
		float x = getX(globalScale) + (flipHorizontal() ? getWidth(globalScale) : 0);
		float y = getY(globalScale);
		float w = getWidth();
		float h = getHeight();
		float hScale = (flipHorizontal() ? -1 : 1) * globalScale * scale;
		float wScale = globalScale * scale;

		batch.draw(actualTexture, x, y, 0, 0, w, h, hScale, wScale, 0F);
	}

	protected void moveX(Direction direction, float ammount, float deltaTime) {
		/*
		 * float newX = pos.x + ammount * deltaTime;
		 * if(!map.doesRectCollideWithMap(newX, getY(), getWidth(), getHeight())){
		 * this.pos.x=newX; }
		 */

		/*
		 * float newX = pos.x + ammount * deltaTime + velocityX * deltaTime;
		 * 
		 * if(Math.abs(velocityX)<DEFAULT_MAX_VELOCITY_X) velocityX+=accelerationX *
		 * (flipHorizontal() ? -1 : 1);
		 * 
		 * if(!map.doesRectCollideWithMap(newX, getY(), getWidth(), getHeight())){
		 * this.pos.x=newX; }
		 */
		changeDirection(direction);
		ammount = ammount * ((direction == Direction.LEFT) ? -1 : 1);
		float newDeltaTime = deltaTime;
		float newX = pos.x + ammount * newDeltaTime + velocityX * deltaTime;
		while (map.doesRectCollideWithMap(newX, getY(), getWidth(), getHeight()) && newDeltaTime > Float.MIN_NORMAL) {
			newDeltaTime /= 2;
			newX = pos.x + ammount * newDeltaTime + velocityX * deltaTime;
		}
		this.pos.x = newX;
	}

	private void loadAnimations() {
		animations = new HashMap<>();
		for (AnimatedState state : getAnimatedStates()) {
			animations.put(state, state.toAnimationImpl(ANIMATION_FRAME_DURATION));
		}
	}

	protected void dispose() {
		changeCurrState(getDeadState());
	}

	public int getId() {
		return id;
	}

	public Vector2 getPos() {
		return getPos(1F);
	}

	public Vector2 getPos(float globalScale) {
		return pos.cpy().scl(globalScale);
	}

	public EntityType getType() {
		return type;
	}

	public GameMapController getMap() {
		return map;
	}

	public boolean isGrounded() {
		return grounded;
	}

	public float getX() {
		return getX(1F);
	}

	public float getX(float globalScale) {
		return pos.x * globalScale;
	}

	public float getY() {
		return getY(1F);
	}

	public float getY(float globalScale) {
		return pos.y * globalScale;
	}

	public int getWidth() {
		return getWidth(1F);
	}

	public int getWidth(float globalScale) {
		return (int) (getActualTexture().getRegionWidth() * scale * globalScale);
	}

	public int getHeight() {
		return getHeight(1F);
	}

	public int getHeight(float globalScale) {
		return (int) (getActualTexture().getRegionHeight() * scale * globalScale);
	}

	public float getWeight() {
		return type.getWeight();
	}

	public float getScale() {
		return scale;
	}

	private boolean flipHorizontal() {
		return direction == Direction.LEFT;
	}

	protected void changeDirection(Direction newDirection) {
		if (!currentState.locks())
			setDirection(newDirection);
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		if (!this.direction.equals(direction))
			resetVelocityX();
		this.direction = direction;
	}

	protected void resetVelocityX() {
		this.velocityX = 0F;
	}

	public EntitySnapshot getSaveSnapshot() {
		return new EntitySnapshot(getType().getId(), getX(), getY());
	}

	protected AnimatedState getCurrentState() {
		return currentState;
	}

	/**
	 * Si no puede cambiar de estado devuelve falso
	 */
	protected boolean changeCurrState(AnimatedState newState) {
		// Si la animacion actual es lockeable y todavia no termino
		if (currentState.locks() && !animations.get(currentState).isFinshed())
			return false;
		if (currentState != newState)
			animations.get(currentState).restart();
		this.currentState = newState;
		return true;
	}

	protected abstract List<AnimatedState> getAnimatedStates();

	protected abstract AnimatedState getDefaultAnimatedState();

	protected abstract AnimatedState getDeadState();

	protected TextureRegion getActualTexture() {
		return animations.get(getCurrentState()).getActualTextureRegion();
	}

	public boolean isDead() {
		return getCurrentState().isDead();
	}

	protected Float getAccelerationX() {
		return accelerationX;
	}

	public void setAccelerationX(Float accelerationX) {
		this.accelerationX = accelerationX;
	}

	public Vector2 getSize() {
		return getSize(1F);
	}

	public Vector2 getSize(float globalScale) {
		return new Vector2(getWidth(globalScale), getHeight(globalScale));
	}

	@Override
	public List<Stadistic> getStadistics(float globalScale) {
		List<Stadistic> stadistics = new ArrayList<>();
		stadistics.add(new Stadistic(this, "ID & Type", this.toString()));
		stadistics.add(new Stadistic(this, "Size(W,H)", getSize() + ">" + getSize(globalScale)));
		stadistics.add(new Stadistic(this, "PixPos(x,y)", getPos() + ">" + getPos(globalScale)));
		stadistics.add(new Stadistic(this, "TilPos(x,y)",
				"(" + fmtFlt(pos.x / map.getTileWidth()) + "," + fmtFlt(pos.y / map.getTileHeight()) + ")"));
		stadistics.add(new Stadistic(this, "Vel(x,y)", getVelocity() + ">" + getVelocity(globalScale)));
		stadistics.add(new Stadistic(this, "Direction", direction.name()));
		stadistics.add(new Stadistic(this, "State", getCurrentState().getName()));
		return stadistics;
	}

	private String fmtFlt(Float f) {
		int integers = 9;
		int decimals = 3;
		return String.format("%+0" + integers + "." + decimals + "f", f);
	}

	public EntityLayer getEntityLayer() {
		return entityLayer;
	}

	public void setEntityLayer(EntityLayer entityLayer) {
		this.entityLayer = entityLayer;
	}

	public Rectangle getArea(float globalScale) {
		return new Rectangle(getX(globalScale), getY(globalScale), getWidth(globalScale), getHeight(globalScale));
	}

	public Rectangle getArea() {
		return new Rectangle(getX(), getY(), getWidth(), getHeight());
	}

	@Override
	public void renderAttributes(ShapeRenderer shapeRenderer, float globalScale) {
		/*
		 * TODO Entity.renderAttributes(...) una flecha mostrando direccion un punto en
		 * el centro del cuerpo
		 */
		renderHitBox(shapeRenderer, globalScale);
		renderDirection(shapeRenderer, globalScale);
	}

	public void renderHitBox(ShapeRenderer shapeRenderer, float globalScale) {
		shapeRenderer.setColor(COLOR_HITBOX);
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.rect(getArea(globalScale).getX(), getArea(globalScale).getY(), getArea(globalScale).getWidth(),
				getArea(globalScale).getHeight());
		shapeRenderer.end();
	}

	public void renderDirection(ShapeRenderer shapeRenderer, float globalScale) {
		shapeRenderer.setColor(COLOR_DIRECTION);
		shapeRenderer.begin(ShapeType.Line);
		Vector2 end = new Vector2(
				getArea(globalScale).x + ((direction == Direction.RIGHT) ? getArea(globalScale).width : 0F),
				getArea(globalScale).y + getArea(globalScale).height / 2);

		// linea inferior
		Vector2 start = new Vector2(
				getArea(globalScale).x + ((direction == Direction.RIGHT) ? 0F : getArea(globalScale).width),
				getArea(globalScale).y);
		shapeRenderer.line(start, end);

		// linea superior
		start.set(start.x, start.y + getArea(globalScale).height);
		shapeRenderer.line(start, end);

		shapeRenderer.end();
	}

	public void renderCircle(ShapeRenderer shapeRenderer, float globalScale, float raius, Color color) {
		shapeRenderer.setColor(color);
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.circle(getCenter(globalScale).x, getCenter(globalScale).y, raius * globalScale);
		shapeRenderer.end();
	}

	public Vector2 getVelocity() {
		return getVelocity(1F);
	}

	public Vector2 getVelocity(float globalScale) {
		return new Vector2(velocityX, velocityY).scl(globalScale);
	}

	@Override
	public String toString() {
		return "(" + id + ") " + type;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public Vector2 getCenter(float globalScale) {
		return getPos(globalScale).add(getSize(globalScale).scl(0.5F));
	}

	public Vector2 getCenter() {
		return getCenter(1F);
	}

	protected boolean canSeeEntity(Entity entity) {
		// TODO lanzar un punto desde this Entidad a la entity, si choca con el mapa
		// retornar falso
		return true;
	}

	protected float getDistanceToEntity(Entity entity) {
		return getCenter().dst(entity.getCenter());
	}

	protected Direction getDirectionToEntity(Entity entity) {
		return (getX() > entity.getX()) ? Direction.LEFT : Direction.RIGHT;
	}

}
