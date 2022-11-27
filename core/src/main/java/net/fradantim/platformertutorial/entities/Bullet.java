package net.fradantim.platformertutorial.entities;

import java.util.Arrays;
import java.util.List;

import net.fradantim.platformertutorial.entities.animation.AnimatedState;

public class Bullet extends Projectile {
	
	private final static AnimatedState ANIM_DEFAULT = new AnimatedState("bullet.png", "DEFAULT", false, false, false);
	private final static AnimatedState ANIM_RUNNING = new AnimatedState("bullet.anim.shot.png", "RUNNING", false, false, false);
	private final static AnimatedState ANIM_DEAD = new AnimatedState("bullet.png","DEAD", false, false, true);

	
	@Override
	protected List<AnimatedState> getAnimatedStates() {
		return Arrays.asList(ANIM_DEFAULT,ANIM_RUNNING,ANIM_DEAD);
	}
	
	@Override
	protected AnimatedState getDefaultAnimatedState() {
		return ANIM_RUNNING;
	}
	
	@Override
	protected AnimatedState getDeadState() {
		return ANIM_DEAD;
	}

	@Override
	public EntityType getType() {
		return EntityType.BULLET;
	}
}
