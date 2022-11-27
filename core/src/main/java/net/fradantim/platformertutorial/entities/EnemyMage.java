package net.fradantim.platformertutorial.entities;

import java.util.Arrays;
import java.util.List;

import net.fradantim.platformertutorial.entities.animation.AnimatedState;

public class EnemyMage extends Enemy {
	
	private static final float SPEED = Player.SPEED*1.01F;
	private static final int BULLET_SPEED = 120;
	private static final int JUMP_VELOCITY = 6;
	
	private final static AnimatedState ANIM_IDLE = new AnimatedState("enemy.png", "IDLE", false, false, false);
	private final static AnimatedState ANIM_RUNNING = new AnimatedState("enemy.png", "RUNNING", false, false, false);
	private final static AnimatedState ANIM_SHOOTING = new AnimatedState("enemy.png", "SHOOTING", true, true, false);
	private final static AnimatedState ANIM_DEAD = new AnimatedState("enemy.png", "DEAD", false, false, true);
	
	@Override
	public void update(float deltaTime, float gravity) {
		if(isDead())
			return;
		
		if(hasPlayer) {
			//changeDirection((direction == Direction.LEFT )? Direction.RIGHT : Direction.LEFT);
			//jump(deltaTime);
			Entity player = map.getPlayer();
			if(player!=null)
				moveX(getDirectionToEntity(player),SPEED,deltaTime);
		}
		
		super.update(deltaTime, gravity);
		
		if(shoots() && !getCurrentState().locks()) {
			shoot();
		}
		
		super.update(deltaTime, gravity);
	}
	
	@Override
	protected void moveX(Direction direction, float ammount, float deltaTime) {
		super.moveX(direction, ammount ,deltaTime);
		changeCurrState(ANIM_RUNNING);
	}
	
	private void jump(float deltaTime) {
		if(grounded) {
			velocityY += JUMP_VELOCITY * getWeight();
		} else if (!grounded && velocityY > 0) {
			velocityY += JUMP_VELOCITY * getWeight() * deltaTime;
		}
	}
	
	private boolean shoots() {
		return false;
		//TODO hacer AI
		//return Gdx.input.isKeyPressed(Keys.SPACE);
	}
	
	private void shoot() {
		shoot(ANIM_SHOOTING, new Bullet(), BULLET_SPEED);
	}
	
	@Override
	protected List<AnimatedState> getAnimatedStates() {
		return Arrays.asList(ANIM_IDLE,ANIM_RUNNING,ANIM_SHOOTING,ANIM_DEAD);
	}

	@Override
	protected AnimatedState getDefaultAnimatedState() {
		return ANIM_IDLE;
	}
	
	@Override
	protected AnimatedState getDeadState() {
		return ANIM_DEAD;
	}

	@Override
	public void getHit(Projectile projectile) {
		// TODO Recibir balazo, perder vida?
		dispose();
	}
}
