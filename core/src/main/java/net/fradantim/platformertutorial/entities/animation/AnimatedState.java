package net.fradantim.platformertutorial.entities.animation;

import java.io.IOException;

public class AnimatedState {
	private String image, name;

	private boolean rewind, locks, dead;
	
	public boolean isRewindable() {
		return rewind;
	}
	
	public boolean locks() { 
		return locks;
	}
	
	public boolean isDead() {
		return dead;
	}
	
	public String getImage() {
		return image;
	}
	
	public String getName() {
		return name;
	}
	
	public AnimatedState(String image, String name, boolean rewind, boolean lockable, boolean dead) {
		this.image = image;
		this.name = name; 
		this.rewind = rewind; 
		this.locks = lockable; 
		this.dead = dead;
	}
	
	public AnimationImpl toAnimationImpl(float frameDuration) throws IOException {
		return new AnimationImpl(image, frameDuration, rewind);
	}
}
