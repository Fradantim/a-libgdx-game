package net.fradantim.platformertutorial;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import net.fradantim.platformertutorial.entities.Entity;
import net.fradantim.platformertutorial.world.GameMapController;
import net.fradantim.platformertutorial.world.TMXGameMapController;

public class GameAdapter extends ApplicationAdapter implements StadisticsFeed {
	
	private static final int MIN_FONT_SIZE=12;
	
	private static final float MIN_GLOBAL_SCALE=0.1F;
	private static final float GLOBAL_SCALE_DELTA=0.1F;
	
	private static final float PIXEL_LINE_WIDTH=2F;
	
	private float globalScale =3F;
	
	private int screenWidth = 1280;
	
	private SpriteBatch batch;
	
	private ShapeRenderer shapeRenderer; //para las formas
	
	private OrthographicCamera camera;
	
	private GameMapController gameMapController;
	
	private BitmapFont font;
	
	private Vector2 camPos;
	
	private int fontSize = 0;
	private int newFontSize = 24;
	private boolean fullScreen = true, camFollowPlayer = true;
	
	//PARA DEBUG
	private boolean showStadistics = true, listStadistics = true, drawGrid = true, drawAttributes = true;
	private List<Entity> selectedEntities = Collections.emptyList();
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		Gdx.gl.glLineWidth(PIXEL_LINE_WIDTH);
		shapeRenderer = new ShapeRenderer(); //necesito inicializacion tardia
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, getScreenSize().x , getScreenSize().y);
		camera.update();
		
		/*
		Application.LOG_NONE: mutes all logging.
		Application.LOG_DEBUG: logs all messages.
		Application.LOG_ERROR: logs only error messages.
		Application.LOG_INFO: logs error and normal messages.
		*/
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		//gameMap = new CustomTiledGameMap();
		start();
		
		font =  getStadisticsFont(newFontSize);
		switchScreenMode(fullScreen);
		
		
	}
	
	public void start() {
		try {
			gameMapController = new TMXGameMapController();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			Gdx.app.error("PlatformerTutorial", "Hubo un error al crear el mapa del juego: "+e.getMessage());
			e.printStackTrace();
		}
	}

	 
	@Override
	public void render () {
		//System.out.println("show:"+showStadistics+" list:"+listStadistics+" attr:"+drawAttributes+" grid:"+drawGrid);
		batch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.position.set(new Vector3(getCamPos(globalScale),0));
		//camera.position.set(new Vector3(64*globalScale,640*globalScale,0));
		camera.update();
		gameMapController.update(Gdx.graphics.getDeltaTime());
		gameMapController.render(batch,globalScale);	
		
		if(showStadistics) {
			drawStadistics();
		}
		
		getInput();
		
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        for(int i=0; i<10 ;i++) {
        	shapeRenderer.line(0,1*i,100*i,100*i);
        }
        shapeRenderer.end();
	}
	
	@Override
	public void dispose () {
		gameMapController.dispose();
		batch.dispose();
	}
	
	private void getInput() {
		if(isKeyJustPressed(Keys.ESCAPE)) {
			Gdx.app.exit();
		}
		
		if(isKeyJustPressed(Keys.F1)) {
			start();
		}
		
		if(isKeyJustPressed(Keys.F3)) {
			showStadistics = (showStadistics) ? false : true;
			selectedEntities = Collections.emptyList();
		}
		
		if(isKeyJustPressed(Keys.NUM_1)) {
			listStadistics = (listStadistics) ? false : true;
			selectedEntities = Collections.emptyList();
		}
		
		if(isKeyJustPressed(Keys.NUM_2)) {
			drawGrid = (drawGrid) ? false : true;
		}
		
		if(isKeyJustPressed(Keys.NUM_3)) {
			drawAttributes = (drawAttributes) ? false : true;
		}
		
		
		if(isKeyPressed(Keys.F5)) {
			changeResolution(isKeyPressed(Keys.SHIFT_LEFT)); 
		}
		
		if(isKeyJustPressed(Keys.F6)) {
			changeFontSize(!isKeyPressed(Keys.SHIFT_LEFT));
		}
		
		if(isKeyJustPressed(Keys.F7)) {
			changeGlobalScale(!isKeyPressed(Keys.SHIFT_LEFT));
		}
		
		if(Gdx.input.isTouched()) {
			selectedEntities = gameMapController.getEntityByPosition(getWorldPointerPos());
		}
		
		if(isKeyJustPressed(Keys.F4)) {
			fullScreen = (fullScreen) ? false : true;
			switchScreenMode(fullScreen);
		}
	}
	
	private void drawStadistics() {
		batch.begin();
		font= getStadisticsFont(newFontSize);
		
		if(listStadistics) {
			String statsStr = String.join("\n",getStadistics(globalScale).stream().map(Stadistic::toString).collect(Collectors.toList()));
			font.draw(batch, statsStr, getCamTopLeft(globalScale).x, getCamTopLeft(globalScale).y);
			//System.out.println("------\n"+statsStr);
		}
		
		if(drawGrid) {
			drawGrid();
		}
		
		if(drawAttributes) {
			batch.end();
			gameMapController.renderAttributes(shapeRenderer, globalScale);
			/*shapeRenderer.begin(ShapeType.Filled);
			Color[] colors = {Color.WHITE,Color.BLACK};
			for (int i = 0; i< 20; i++) {
				shapeRenderer.setColor(colors[i%colors.length]);
				shapeRenderer.rect(
						i*32 * globalScale, i*32 * globalScale, 
						32 * globalScale, 32 * globalScale);
			}
			shapeRenderer.end();*/
			batch.begin();
		}
		
		batch.end();
	}
	
	/**
	 * Dibuja los ejes x e y en los bordes del mapa
	 */
	private void drawGrid() {
		int xDelta = (int) (160*globalScale);
		int yDelta = (int) (160*globalScale);
		//ejes horizontales sup e inf
		for(float y : Arrays.asList(0F,gameMapController.getPixelHeight(globalScale))) {
			for(int x=0; x < gameMapController.getPixelWidth(globalScale); x+=xDelta)
			font.draw(batch, "H("+x+","+y+")", x,y);
		}
		//ejes verticales sup e inf
		for(float x : Arrays.asList(0F,gameMapController.getPixelWidth(globalScale))) {
			for(int y=0; y < gameMapController.getPixelHeight(globalScale); y+=yDelta)
				font.draw(batch, "H("+x+","+y+")", x,y);
		}
	}
	
	
	/**
	 * Devuelve el pixel en relacion a la Ventana del SO
	 * @return
	 */
	private Vector2 getDisplayPointerPos() {
		return new Vector2(Gdx.input.getX(),Gdx.input.getY());
	}
	
	/**
	 * Devuelve el pixel en relacion a la pantalla del juego
	 * @return
	 */
	private Vector2 getScreenPointerPos() {
		return new Vector2(getDisplayPointerPos().x*getScreenSize().x/getDisplayResolution().x,getDisplayPointerPos().y*getScreenSize().y/getDisplayResolution().y);
	}
	
	/**
	 * Devuelve el pixel en relacion al mundo del juego sin escalar
	 * @return
	 */
	private Vector2 getWorldPointerPos() {
		return getWorldPointerPos(1F);
	}
	
	/**
	 * Devuelve el pixel en relacion al mundo del juego escalado
	 * @return
	 */
	private Vector2 getWorldPointerPos(float globalScale) {
		return getCamTopLeft().add(new Vector2(getScreenPointerPos().x, - getScreenPointerPos().y)).scl(globalScale);
	}
	
	private Vector2 getCamTopLeft() {
		return getCamTopLeft(1F);
	}
	
	private Vector2 getCamTopLeft(float globalScale) {//OK
		return new Vector2(getCamPos(globalScale).x - getScreenSize().x/2, getCamPos(globalScale).y + getScreenSize().y/2);
	}
	
	private Vector2 getScreenSize(float globalScale) {
		return new Vector2(screenWidth, screenWidth/16*9).scl(1/globalScale); //TODO SACAR HARDCODEO averiguar por que no puede hacerse la operacion directamente con el resultado de getDisplayAspectRatio()
	}
	
	private Vector2 getScreenSize() {
		return getScreenSize(1F);
	}
	
	private Vector2 getCamPos() {
		return getCamPos(1F);
	}
	
	private Vector2 getCamPos(float globalScale) {
		if (camPos == null) {
			camPos = new Vector2(0,0);
		}
		
		if(camFollowPlayer)
			camPos = gameMapController.getPlayer().getPos(globalScale).add(gameMapController.getPlayer().getSize(globalScale*0.5F));
				
		return camPos;
	}
	
	private BitmapFont getStadisticsFont(int newFontSize) {
		if(newFontSize!=fontSize) {
			fontSize=newFontSize;
			FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Courier Prime.ttf"));
			FreeTypeFontParameter parameter = new FreeTypeFontParameter();
			parameter.size = newFontSize;
			font = generator.generateFont(parameter); // font size 12
			generator.dispose(); // don't forget to dispose to avoid memory leaks!
		}
		return font;
	}
	
	private Vector2 getDisplayResolution() {
		return new Vector2(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
	}
	
	private float getDisplayAspectRatio() {
		return getDisplayResolution().x/getDisplayResolution().y;
	}	
	
	private void changeResolution(boolean up) {
		if(up)
			screenWidth+=gameMapController.getTileWidth();
		else
			screenWidth-=gameMapController.getTileWidth();
		camera.setToOrtho(false, getScreenSize().x, getScreenSize().y);
	}
	
	private void switchScreenMode(boolean fullScreen) {
		if (fullScreen)
			Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode(Gdx.graphics.getMonitor()));
		else
			Gdx.graphics.setWindowedMode((int) getScreenSize().x, (int) getScreenSize().y);
	}
	
	private void changeFontSize(boolean up) {
		if (up)
			newFontSize++;
		else if(newFontSize>MIN_FONT_SIZE)
			newFontSize--;
	}
	
	private void changeGlobalScale(boolean up) {
		if(up)
			globalScale+=GLOBAL_SCALE_DELTA;
		else if(globalScale>MIN_GLOBAL_SCALE)
			globalScale-=GLOBAL_SCALE_DELTA;
	}	
	
	private boolean isKeyJustPressed(int... keys) {
		for(int key: keys) {
			if(!Gdx.input.isKeyJustPressed(key)) {
				return false;
			}
		}
		return true;
	}
	
	private boolean isKeyPressed(int... keys) {
		for(int key: keys) {
			if(!Gdx.input.isKeyPressed(key)) {
				return false;
			}
		}
		return true;
	}


	@Override
	public List<Stadistic> getStadistics(float globalScale) {
		List<Stadistic> stats = new ArrayList<>();
		stats.add(new Stadistic("Gdx.gfx.FPS&deltaTime",String.valueOf(Gdx.graphics.getFramesPerSecond())+","+String.valueOf(Gdx.graphics.getDeltaTime())));
		stats.add(new Stadistic(this,"GlobalScale", globalScale));
		stats.add(new Stadistic(this,"ScreenScale", getScreenScaling()));
		stats.add(new Stadistic(this,"FontSize",String.valueOf(fontSize)));
		stats.add(new Stadistic(this,"DisplayAspectRatio (W/H)",getDisplayAspectRatio()));
		stats.add(new Stadistic(this,"DisplayPixRes (W,H)",getDisplayResolution()));
		stats.add(new Stadistic(this,"Screen_PixRes (W,H)",getScreenSize()+">"+getScreenSize(globalScale)));
		stats.add(new Stadistic(this,"CamPos____ (W,H)",getCamPos()+">"+getCamPos(globalScale)));
		stats.add(new Stadistic(this,"CamTopLeft (W,H)",getCamTopLeft()+">"+getCamTopLeft(globalScale)));
		stats.add(new Stadistic(this,"DisplayPointerPos (x,y)",getDisplayPointerPos()));
		stats.add(new Stadistic(this,"Screen_PointerPos (x,y)",getScreenPointerPos()));
		stats.add(new Stadistic(this,"World__PointerPos (x,y)",getWorldPointerPos()+">"+getWorldPointerPos(globalScale)));
		stats.addAll(gameMapController.getStadistics(globalScale));
		stats.add(new Stadistic(this,"Pointed Entities",gameMapController.getEntityByPosition(getWorldPointerPos())));
		
		for(Entity entity: selectedEntities)
			stats.addAll(entity.getStadistics(globalScale));
			
		return stats;
	}
	
	/**
	 * Devuelve la relacion entre la resolucion de la pantalla y la de la ventana
	 */
	public float getScreenScaling() {
		return getDisplayResolution().x/getScreenSize().x;
	}
}
