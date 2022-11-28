package net.fradantim.platformertutorial.entities;

import com.badlogic.gdx.math.Vector2;

import net.fradantim.platformertutorial.entities.animation.AnimatedState;

public abstract class Shooter extends Entity {

	protected void shoot(AnimatedState state, Projectile projectile, float bulletSpeed) {
		if (changeCurrState(state)) {
			// TODO agregar knockback a los disparos
			projectile.setDirection(direction);
			projectile.setEntityLayer(entityLayer.getLower());
			projectile.setSpeed(bulletSpeed);
			projectile.setOwnerType(getType());
			map.addEntity(projectile, () -> projectile.create(new Vector2(pos), projectile.getType(), map));
		}
	}
}
