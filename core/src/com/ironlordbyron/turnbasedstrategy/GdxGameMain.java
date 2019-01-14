package com.ironlordbyron.turnbasedstrategy;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector;
import com.ironlordbyron.turnbasedstrategy.tiledutils.TiledMapStageFactory;
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.BlankMapGenerator;
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider;
import com.ironlordbyron.turnbasedstrategy.view.ui.TacMapHudFactory;

public class GdxGameMain extends ApplicationAdapter  {

    public static Float TILEMAP_SCALING_FACTOR = 1.0f; // TODO: This super doesn't work, don't change it

    private final static int WINDOW_WIDTH = 1100;
    private final static int WINDOW_HEIGHT = 600;


    TiledMap tiledMap;
    OrthographicCamera tacMapCamera;
    TiledMapRenderer tiledMapRenderer;
    Stage tiledMapStage;
    Stage hudStage;
    OrthographicCamera hudCamera;
    @Override
    public void create() {
        Gdx.graphics.setWindowedMode(WINDOW_WIDTH, WINDOW_HEIGHT);


        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        tacMapCamera = new OrthographicCamera();
        tacMapCamera.setToOrtho(false, w, h);
        tacMapCamera.update();
        GameModuleInjector.Companion.initGameCameraProvider(tacMapCamera);


        BlankMapGenerator tileMapGenerator = GameModuleInjector.Companion.createTiledMapGenerator();
        tiledMap = tileMapGenerator.generateMap(BlankMapGenerator.Companion.getDefaultMapGenParams());
        TileMapProvider tileMapProvider = GameModuleInjector.Companion.createGameStateProvider();
        tileMapProvider.setTiledMap(tiledMap);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, TILEMAP_SCALING_FACTOR);
        TiledMapStageFactory tiledMapStageFactory = GameModuleInjector.Companion.createTiledMapStageFactory();
        tiledMapStage = tiledMapStageFactory.create(tiledMap, tacMapCamera);
        FitViewport tiledMapViewport = new FitViewport(w, h, tacMapCamera);
        tiledMapStage.setViewport(tiledMapViewport);
        tiledMapStage.getViewport().setCamera(tacMapCamera);
        hudCamera = new OrthographicCamera(w, h);

        TacMapHudFactory tacMapHudFactory = GameModuleInjector.Companion.createTacMapHudFactory();
        hudStage = tacMapHudFactory.create(new FitViewport(w, h, hudCamera));
        hudCamera.update();
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(hudStage);
        multiplexer.addProcessor(tiledMapStage);
        Gdx.input.setInputProcessor(multiplexer);
    }
    @Override
    public void resize(int width, int height) {

        tiledMapStage.getViewport().update(width, height);
        hudStage.getViewport().update(width, height);

    }
    @Override
    public void render() {
        Gdx.gl.glClearColor(.1f, .1f, .1f, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        tacMapCamera.update();
        tiledMapRenderer.setView(tacMapCamera);
        tiledMapRenderer.render();
        tiledMapStage.act();
        GameModuleInjector.Companion.getGameCameraProvider().render();
        GameModuleInjector.Companion.getSpecialEffectManager().renderSpecialEffects();
        hudCamera.update();
        tiledMapStage.draw();
        hudStage.draw();
        hudStage.act();
    }

    @Override
    public void dispose(){
        hudStage.dispose();
        tiledMapStage.dispose();
    }
}