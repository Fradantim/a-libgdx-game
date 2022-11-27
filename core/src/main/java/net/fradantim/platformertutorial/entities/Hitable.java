package net.fradantim.platformertutorial.entities;

public interface Hitable {

	public void getHit(Projectile projectile);
	public EntityType getType();
	
}
