package net.fradantim.platformertutorial.entities;

public abstract class Projectile extends Entity implements Hitable{
	
	private EntityType ownerType;
	
	@Override
	public void update(float deltaTime, float gravity) {
		if(isDead())
			return;
		
		float ammountToMove = getSpeed();
		if(direction == Direction.LEFT)
			ammountToMove=-ammountToMove;		
		
		for(Hitable hitable : map.getHitablesByPosition(getPos())) {	
			if(hitable.equals(this) || getOwnerType() == hitable.getType()) {
				//the same projectile or is a projectile of it's owner
			} else if (hitable instanceof Projectile && ((Projectile) hitable).getOwnerType().equals(this.getOwnerType())) {
				//both are projectiles and share owner
			} else {
				hitable.getHit(this);
				explode();
				return;
			}
		}
				
		if (map.doesRectCollideWithMap(getX()+ammountToMove*deltaTime, getY(), getWidth(), getHeight())) {
			explode();
			return;
		}
		
		moveX(direction, getSpeed(), deltaTime);
		
		super.update(deltaTime, gravity); //Apply gravity
	}
	
	protected void explode() {
		//TODO hacer alguna magia?
		dispose();
	}
	
	public EntityType getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(EntityType ownerType) {
		this.ownerType = ownerType;
	}
	
	@Override
	public void getHit(Projectile projectile) {
		if(getOwnerType()!=projectile.getOwnerType()) {
			explode();
		}
	}
	
	public abstract EntityType getType();
}
