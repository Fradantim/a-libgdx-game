package net.fradantim.platformertutorial.entities;

import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

import net.fradantim.platformertutorial.entities.animation.AnimatedState;

public class Player extends Shooter implements Hitable{
	
	public static final int SPEED = 80;
	public static final int BULLET_SPEED = 100;
	public static final int JUMP_VELOCITY = 5;
	
	private final static AnimatedState ANIM_IDLE = new AnimatedState("player.anim.idle.png", "IDLE", false, false, false);
	private final static AnimatedState ANIM_RUNNING = new AnimatedState("player.anim.run.png", "RUNNING", false, false, false);
	private final static AnimatedState ANIM_SHOOTING = new AnimatedState("player.anim.shoot.png", "SHOOTING", true, true, false);
	private final static AnimatedState ANIM_DEAD = new AnimatedState("player.png", "DEAD", false, false, true);
	
	@Override
	public void update(float deltaTime, float gravity) {
		if(isJumpPressed() && grounded) {
			velocityY += JUMP_VELOCITY * getWeight();
		} else if (isJumpPressed() && !grounded && velocityY > 0) {
			velocityY += JUMP_VELOCITY * getWeight() * deltaTime;
		}
		
		super.update(deltaTime, gravity); //Apply gravity
		
		if(isLeftPressed()) {
			moveX(Direction.LEFT, SPEED,deltaTime);
		} else if (isRightPressed()) {
			moveX(Direction.RIGHT, SPEED,deltaTime);
		} else {
			changeCurrState(ANIM_IDLE);
		}
		
		if(isShootPressed() && !getCurrentState().locks()) {
			shoot();
		}
		
		//DEBUG!!!
		if(isDebugUpPressed()) { pos.y+=16; }
		if(isDebugDownPressed()) { pos.y-=16; }
		if(isDebugRightPressed()) { pos.x+=16; }
		if(isDebugLeftPressed()) { pos.x-=16; }
	}
	
	@Override
	protected void moveX(Direction direction, float ammount, float deltaTime) {
		super.moveX(direction, ammount ,deltaTime);
		changeCurrState(ANIM_RUNNING);
	}

	private boolean isJumpPressed() {
		return Gdx.input.isKeyPressed(Keys.UP);
	}
	
	private boolean isShootPressed() {
		return Gdx.input.isKeyPressed(Keys.SPACE);
	}
	
	private boolean isLeftPressed() {
		return Gdx.input.isKeyPressed(Keys.LEFT);
	}
	
	private boolean isRightPressed() {
		return Gdx.input.isKeyPressed(Keys.RIGHT);
	}
	
	private boolean isDebugDownPressed() {
		return Gdx.input.isKeyPressed(Keys.S);
	}
	
	private boolean isDebugUpPressed() {
		return Gdx.input.isKeyPressed(Keys.W);
	}
	
	private boolean isDebugRightPressed() {
		return Gdx.input.isKeyPressed(Keys.D);
	}
	
	private boolean isDebugLeftPressed() {
		return Gdx.input.isKeyPressed(Keys.A);
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
