package net.fradantim.platformertutorial.entities.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.fradantim.platformertutorial.util.ImageUtil;

public class AnimationImpl {

	private Animation<TextureRegion> animation;
	private TextureRegion[] animationFrames;
	private float elapsedTime;
	
	private static final float LOOP_PINGPONG_COMPLETITION_TIME_PERCENTAGE = 0.6F;
	
	public AnimationImpl (String imagePath, float frameDuration, boolean rewind, int tileWidth, int tileHeight) {
		elapsedTime=0F;
		this.animation= getAnimation(imagePath, frameDuration, tileWidth, tileHeight);
		if(rewind)
			animation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
		else
			animation.setPlayMode(Animation.PlayMode.LOOP);
	}
	
	//TODO: En algun momento evaluar animaciones estaticas
	private Animation<TextureRegion> getAnimation(String imagePath, float frameDuration, int spriteWidth, int spriteHeight) {
		TextureRegion[][] tmpFrames = TextureRegion.split(new Texture(imagePath), spriteWidth, spriteHeight);
		int index = 0;
		animationFrames = new TextureRegion[tmpFrames.length * tmpFrames[0].length];
		for(int x=0; x<tmpFrames.length; x++) {
			for(int y=0; y<tmpFrames[x].length; y++) {
				animationFrames[index++] = tmpFrames[x][y];
			}
		}
		Gdx.app.debug("AnimationImpl", imagePath+" "+animationFrames.length+" frames loaded.");
		return new Animation<TextureRegion>(frameDuration, animationFrames);
	}
	
	/**
	 * Construccion para animaciones del mismo alto que ancho. La animacion tiene
	 * que estar en frames horizontales
	 */
	public static AnimationImpl fromImage(String imagePath, float frameDuration, boolean rewind) {
		int height = ImageUtil.getImageHeight(imagePath);
		return new AnimationImpl(imagePath, frameDuration, rewind, height, height);
	}
	
	public TextureRegion getActualTextureRegion() {
		return animation.getKeyFrame(getElapsedTime(),true);
	}
	
	/**
	 * Devuelve al primer frame
	 */
	public void restart() {
		elapsedTime=0F;
	}
	
	public boolean isFinshed() {
		float fixedElapsedTime=getElapsedTime();
		if(animation.getPlayMode().equals(Animation.PlayMode.LOOP_PINGPONG)) {
			fixedElapsedTime=fixedElapsedTime*LOOP_PINGPONG_COMPLETITION_TIME_PERCENTAGE;
		}
		return animation.isAnimationFinished(fixedElapsedTime);
	}
	
	private float getElapsedTime() {
		elapsedTime+=Gdx.graphics.getDeltaTime();
		return elapsedTime;
	}
}
