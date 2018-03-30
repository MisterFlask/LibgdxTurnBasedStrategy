package com.ironlordbyron.turnbasedstrategy;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector;
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.TiledMapStageFactory;
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.mapgen.BlankMapGenerator;
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.mapgen.TileMapProvider;

public class GdxGameMain extends ApplicationAdapter  {
    TiledMap tiledMap;
    OrthographicCamera camera;
    TiledMapRenderer tiledMapRenderer;
    Stage stage;

    @Override
    public void create() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, w, h);
        camera.update();
        BlankMapGenerator tileMapGenerator = GameModuleInjector.Companion.createTiledMapGenerator();
        tiledMap = tileMapGenerator.generateMap(BlankMapGenerator.Companion.getDefaultMapGenParams());
        TileMapProvider tileMapProvider = GameModuleInjector.Companion.createGameStateProvider();
        tileMapProvider.setTiledMap(tiledMap);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        TiledMapStageFactory tiledMapStageFactory = GameModuleInjector.Companion.createTiledMapStageFactory();
        stage = tiledMapStageFactory.create(tiledMap, camera);
        stage.setViewport(new FitViewport(800, 480, camera));
        Gdx.input.setInputProcessor(stage);
        stage.getViewport().setCamera(camera);
    }
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }
    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
        stage.act();
        stage.draw();
    }
}