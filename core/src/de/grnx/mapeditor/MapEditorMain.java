package de.grnx.mapeditor;
//TODO: Fix dialog focus, urgently!
import static de.grnx.mapeditor.buildableConf.Blocks.blocks;
import static de.grnx.mapeditor.helper.Util.screen;
//import static de.grnx.mapeditor.BackendInterface;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.CountDownLatch;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import de.grnx.mapeditor.buildableConf.Blocks;
import de.grnx.mapeditor.canvas.World;
import de.grnx.mapeditor.chunk.Cregion;
import de.grnx.mapeditor.controllable.Overlay;
import de.grnx.mapeditor.controllable.HistoryQueue;
import de.grnx.mapeditor.controllable.InputHolder;
import de.grnx.mapeditor.controllable.Inputs;
import de.grnx.mapeditor.controllable.Controller;
import de.grnx.mapeditor.controllable.BlockTrace;
import de.grnx.mapeditor.controllable.BlockTrace.RayInfo;
import de.grnx.mapeditor.helper.BlockPos;
import de.grnx.mapeditor.helper.Camera;
import de.grnx.mapeditor.helper.TerrainBinder;
import de.grnx.mapeditor.helper.Tim_eWatch;
import de.grnx.mapeditor.helper.Util;
import de.grnx.mapeditor.helper.filehandler.FileHandler;
import de.grnx.mapeditor.texture.BoxRenderer;
import de.grnx.mapeditor.texture.ImportRenderer;
import de.grnx.mapeditor.texture.ManagerInit;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

//public class MapEditorMain implements ApplicationListener {
public class MapEditorMain extends ApplicationAdapter {
	public AssetManager manager = new AssetManager();

	public volatile static boolean MENUFLAG = false;

	private static TimeWatch twQueue;

	private static ImportRenderer ir;
	private static BlockPos importRendererOffset;
	private static TimeWatch twImportRenderer;
	private static HistoryQueue onHoldQueue;

	private Stage stage;
	private Camera cam;
	private Inputs input;
	private BitmapFont font;

	public static World world;
	public Controller player;
	public ScreenViewport view = new ScreenViewport();

	private Matrix4 combined;
	private SpriteBatch batch;
	ShapeRenderer shapeRenderer;
	private Skin skin;

	// private Stage stage;
	private Table table;
//	private final SwingInterface interfaces;

	/*
	 * public MapEditorMain(World world) { this.world = world; }
	 */

	public MapEditorMain(World world) {

		this.world = world;
	}

	@Override
	public void create() {
		normalSartup();

		
		stage = new Stage(new ScreenViewport());
		shapeRenderer = new ShapeRenderer();

		screen.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam = new Camera(70, screen.width, screen.height);
		cam.lookAt(500, 30, 500);
		cam.near = 0.2f;
		cam.far = 600f; // 300f
		cam.update();
		
		
		

		createWorld();
		final int center = World.defaultSize * 8;
		for (int y = Cregion.HEIGHT - 1; y > -1; y--) {
			if (blocks[world.getBlock(center, y, center)].collision) {
				cam.position.set(center, y + 6, center);
				break;
			}
		}
		batch = new SpriteBatch(100);
		combined = view.getCamera().combined;

		if (Options.mouseCaught)
			Gdx.input.setCursorCatched(true);
//		if(Options.mouseCaught)Gdx.input.setCursorCatched(Options.tempCursorCaughtInternal);
		Gdx.input.setCursorCatched(Options.mouseCaught);

		Gdx.input.setInputProcessor(new InputMultiplexer(new InputHolder(), input = new Inputs(), stage));
		if (Options.mouseCaught)
			Inputs.resetMouse();
		Overlay.update();

		box = new BoxRenderer(cam);

		player = new Controller(cam, world);
		skin = new Skin(FileHandler.getFile("skin\\skin.json"));
			

		Table root = new Table();

		stage.addActor(root);
		createButtons(root);

		// world.setBlock(new BlockPos(world.CENTER, 0, world.CENTER), blocks[1].id);
		
		twQueue=new TimeWatch((float)1/2, (float)1/8);
		twImportRenderer = new TimeWatch((float)1/2, (float)1/16, true);
	}

	private void createButtons(Table root) {
		root.setFillParent(true);
		//root.setDebug(true, true);
		root.top().right();
		root.defaults().width(150).height(43);
		TextButton resume = new TextButton("Resume", skin);
		root.add(resume).row();
		resume.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.input.setCursorCatched(true);
				Options.mouseCaught = true;
				MENUFLAG = false;
				InputHolder.holder.clear();
				input.clearJustPressed();


			}
		});
		
		
		
		TextButton button_load_Struct = new TextButton("Load Struct", skin);
		root.add(button_load_Struct).row();
		button_load_Struct.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				showCoordinateDialog();
				
				
				/*onHoldQueue = BackendInterface.loadStruct(player); 
				ir = new ImportRenderer(cam, onHoldQueue.getPosArray());
				importRendererOffset = new BlockPos(0,0,0);*/
				
				 /*player.q.append(BackendInterface.loadStruct(player), new BlockPos(showCoordinateDialog()));
				 System.out.println(Arrays.toString(player.q.getPosArray()));
				 BackendInterface.restoreAll(player);*/
				 
			}
		});

		TextButton button_save = new TextButton("Save Stuct", skin);
		root.add(button_save).row();
		button_save.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				BackendInterface.saveStruct(world, player.q, null, false);
				
			}
		});

		TextButton button_load = new TextButton("Load World", skin);
		root.add(button_load).row();
		button_load.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				String message = "Importing a world file instead of importing structure files is strongly discouraged. If you proceed, the resulting file will likely be mostly uneditable and behave unexpectedly!";
				int fuck = JOptionPane.showOptionDialog(null, message, "Warning", //horrible horrible code; i am ashamed of myself
						JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
						new Object[]{"Cancel", "Proceed Anyways"}, "Cancel") ;
				if (fuck != JOptionPane.CLOSED_OPTION && fuck != JOptionPane.OK_OPTION ) {
					world.dispose();
					world = BackendInterface.loadWorld();
					world.intsRender(cam, World.renderDist);
					world.forceDirty();
					OpenGL();
				}else {
					return;
				}

			}
		});
		
		TextButton button_saveAs = new TextButton("Save World", skin);
		root.add(button_saveAs).row();
		button_saveAs.addListener(new ChangeListener() {//TODO implement null/exit safety
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				BackendInterface.saveWorld(world);//TODO: warn deprecated Method; discourage from using 
			}
		});

		TextButton button_newWorld = new TextButton("New World", skin);
		//root.add(button_newWorld).row();
		button_newWorld.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				world.dispose();
				world = new World(true);
				world.intsRender(cam, World.renderDist);
				normalSartup();
			}
		});
		
		TextButton button_emptyCanvas = new TextButton("Empty Canvas", skin);
		root.add(button_emptyCanvas).row();
		button_emptyCanvas.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				BackendInterface.emptyCanvas(player);
				importRendererCancel();
			}
		});

		TextButton button_undo = new TextButton("Undo", skin);
		root.add(button_undo).row();
		button_undo.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(Inputs.isKeyPressed(Keys.SHIFT_LEFT)) {
					BackendInterface.undoOnce(player);
					System.out.println("undo and shift held");
				}else {
				BackendInterface.undo(player);
				}			}
		});

		TextButton button_redo = new TextButton("Redo", skin);
		root.add(button_redo).row();
		button_redo.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(Inputs.isKeyPressed(Keys.SHIFT_LEFT)) {
					System.out.println("redo and shift held");
					BackendInterface.redoOnce(player);


					//player.q.redo();
				}else {
				BackendInterface.redo(player);
				}
			}
		});

		TextButton button_createSquare = new TextButton("Create Square", skin);
		//root.add(button_createSquare).row();
		button_createSquare.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				BackendInterface.createSquare();
			}
		});
		
		TextButton button_tScreen = new TextButton("t-Screen", skin);
		root.add(button_tScreen).row();
		button_tScreen.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Boolean fullScreen = Gdx.graphics.isFullscreen();
				Graphics.DisplayMode currentMode = Gdx.graphics.getDisplayMode();
				if (fullScreen == true) {
					Gdx.graphics.setWindowedMode(currentMode.width, currentMode.height);
				} else {
					Gdx.graphics.setFullscreenMode(currentMode);
				}			}
		});
		
		TextButton button_export = new TextButton("Export", skin);
		root.add(button_export).row();
		button_export.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				BackendInterface.export(player.q);
			}
		});

		TextButton button_exit = new TextButton("Exit", skin);
		root.add(button_exit).row();
		button_exit.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				BackendInterface.exit();
			}
		});

	}

	BoxRenderer box;

	@Override
	public void render() {
		if(manager.update()) { //manager.finishLoading(); force loads everything and stops current tasks; not useful for large amounts of assets and async loading //done anyways because async chunk load
		try {
			update();
			if (MENUFLAG)
				stage.draw();
		} catch (Exception e) {
			e.printStackTrace();
			Gdx.app.exit();
			return;
		}
		}else {
			//assetmanager not done loading progress bar maybe
		}
		
	}

	GLProfiler per;

	private void update() {
		// Gdx.app.log("FuckTag", "asdjasd");
		if (Inputs.isKeyJustPressed(Keys.ESCAPE)) {
			//System.out.println(Gdx.input.isCursorCatched());
				
				
			if (Gdx.input.isCursorCatched()) {
				//pause();
				Gdx.input.setCursorCatched(false);
				Options.mouseCaught = false;
				Options.mouseCaught = false;
				MENUFLAG = true;
				InputHolder.holder.clear();
				input.clearJustPressed();

				Gdx.gl.glEnable(GL20.GL_BLEND);// ----- non functional gray menu screen :(
				Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
				shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
				shapeRenderer.setColor(new Color(1, 1, 1, 1f));
				shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
				shapeRenderer.end();
				Gdx.gl.glDisable(GL20.GL_BLEND);// -----

			} else {
				//pause();
				Overlay.update();
				Gdx.input.setCursorCatched(true);
				Options.mouseCaught = true;
				Options.mouseCaught = true;
				MENUFLAG = false;
			}

//			Gdx.input.setCursorCatched(true);
			// interfaces.showFunctionDialog();
		}
		// Gdx.app.exit();
		// Options.mouseCaught =false;
		// Options.tempCursorCaughtInternal = false;

		if ((Inputs.isKeyPressed(Keys.SHIFT_LEFT))
				&& Inputs.isKeyJustPressed(Keys.Q)) {
			//Saver.save(world, Extensions.WORLD);
			//BackendInterface.saveStruct(world, player.q);
			BackendInterface.saveStruct(world, player.q, BackendInterface.lastSaved, false);
		}
		
		if(Inputs.isKeyJustPressed(Keys.R) && Inputs.isKeyPressed(Keys.SHIFT_LEFT)) {
			BackendInterface.restoreAll(player);
		}
		
		if(Inputs.isKeyJustPressed(Keys.U) && Inputs.isKeyPressed(Keys.SHIFT_LEFT)) {
			BackendInterface.undoAll(player);
		}
		
		if (Inputs.isKeyJustPressed(Keys.Z) && Inputs.isKeyPressed(Keys.SHIFT_LEFT)) {
			BackendInterface.undo(player);
		}
		if (Inputs.isKeyJustPressed(Keys.Y) && Inputs.isKeyPressed(Keys.SHIFT_LEFT)) {
			BackendInterface.redo(player);

		}
		
		if (Inputs.isKeyPressed(Keys.Z) && Inputs.isKeyPressed(Keys.SHIFT_LEFT)) {
			if(twQueue.canExecute())BackendInterface.undo(player);
				
			
		}
		
		if (Inputs.isKeyPressed(Keys.Y) && Inputs.isKeyPressed(Keys.SHIFT_LEFT)) {
		
				if(twQueue.canExecute())BackendInterface.redo(player);
			
		}
		
		twQueue.render();
		
		
		
		if (Inputs.isKeyJustPressed(Keys.TAB)) {
			Boolean fullScreen = Gdx.graphics.isFullscreen();
			Graphics.DisplayMode currentMode = Gdx.graphics.getDisplayMode();
			if (fullScreen == true) {
				Gdx.graphics.setWindowedMode(currentMode.width, currentMode.height);
			} else {
				Gdx.graphics.setFullscreenMode(currentMode);
			}
		}
		
		if (Inputs.isKeyJustPressed(Keys.C)) {
			//math isnt mathin, im having loads of problems and no time
				cam.position.set(world.CENTER-25, 25, world.CENTER-25);
			//cam.position.set(world.LENGHT-25, 25, world.LENGHT-25);
				// calculate so that the camera is looking at the center of the world

				//cam.lookAt(world.CENTER, 0, world.CENTER);
				//cam.lookAt(0, 0, 0);

				//cam.yaw = 50;
				//cam.pitch = 50;



			float dx = world.CENTER - cam.position.x;
			float dy =  cam.position.y;
			float dz = world.CENTER - cam.position.z;

			// Calculate pitch and yaw
//			float pitch = (float) Math.atan2(dy, (float)Math.sqrt(dx * dx + dz * dz));
//			float yaw = (float) Math.atan2(dz, dx);

			float pitch = (float) Math.atan((float)dy/dx);
			float yaw = (float) Math.atan(Math.sqrt(dx*dx+dy*dy)/dz);
			// Convert to degrees
			pitch = pitch * (180 / (float) Math.PI);
			yaw = yaw * (180 / (float) Math.PI);

			// Set the camera's pitch and yaw
			cam.pitch = pitch;
			cam.yaw = yaw;

			// Update the camera
			cam.update();

		}

	

		/*
		 * if (Inputs.isKeyJustPressed(Keys.ESCAPE)){
		 * System.out.println("tab pressed!"); Boolean fullScreen =
		 * Gdx.graphics.isFullscreen(); Graphics.DisplayMode currentMode =
		 * Gdx.graphics.getDisplayMode(); if (fullScreen == true) {
		 * Gdx.graphics.setWindowedMode(currentMode.width, currentMode.height); }else {
		 * Gdx.graphics.setFullscreenMode(currentMode); }}
		 */

		Gdx.gl.glUseProgram(0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

//		if (Inputs.isKeyJustPressed(Keys.P)) {
//			PartiBench.exec(cam);
//		}
		
		
		
if(ir!=null&&importRendererOffset!=null&&onHoldQueue!=null&&!MENUFLAG) {
	//y axis
	
	if (Inputs.isKeyJustPressed(Keys.UP)) {
		if(Inputs.isKeyPressed(Keys.SHIFT_LEFT)) {
			importRendererOffset.up();

		}else {
			importRendererOffset.north();

		}
	}
	if (Inputs.isKeyJustPressed(Keys.DOWN)) {
		if(Inputs.isKeyPressed(Keys.SHIFT_LEFT)) {
			importRendererOffset.down();
		}else {
			importRendererOffset.south();

		}
		
	}
	
	if (Inputs.isKeyPressed(Keys.UP)) {
		if(Inputs.isKeyPressed(Keys.SHIFT_LEFT)) {
			if(twImportRenderer.canExecute())importRendererOffset.up();

		}else {
			if(twImportRenderer.canExecute())importRendererOffset.north();

		}
	}
	if (Inputs.isKeyPressed(Keys.DOWN)) {
		if(Inputs.isKeyPressed(Keys.SHIFT_LEFT)) {
			if(twImportRenderer.canExecute())importRendererOffset.down();
		}else {
			if(twImportRenderer.canExecute())importRendererOffset.south();

		}
		
	}
	
	/*if (Inputs.isKeyPressed(Keys.UP)&&Inputs.isKeyPressed(Keys.SHIFT_LEFT)) {
		if(twImportRenderer.canExecute())importRendererOffset.up();
	}
	if (Inputs.isKeyPressed(Keys.DOWN)&&Inputs.isKeyPressed(Keys.SHIFT_LEFT)) {
		if(twImportRenderer.canExecute())importRendererOffset.down();
	}
	
	
			if (Inputs.isKeyJustPressed(Keys.UP)&&!Inputs.isKeyPressed(Keys.SHIFT_LEFT)) {
			}else if(Inputs.isKeyJustPressed(Keys.UP)&&Inputs.isKeyPressed(Keys.SHIFT_LEFT)) {
				importRendererOffset.up();

			
			if (Inputs.isKeyJustPressed(Keys.DOWN)&&!Inputs.isKeyPressed(Keys.SHIFT_LEFT)) {
			}
			
			if (Inputs.isKeyPressed(Keys.UP)&&!Inputs.isKeyPressed(Keys.SHIFT_LEFT)) {
				if(twImportRenderer.canExecute())importRendererOffset.north();
			}
			if (Inputs.isKeyPressed(Keys.DOWN)&&!Inputs.isKeyPressed(Keys.SHIFT_LEFT)) {
				if(twImportRenderer.canExecute())importRendererOffset.south();
			}*/
			
			if (Inputs.isKeyJustPressed(Keys.LEFT)) {
				importRendererOffset.west();
			}
			if (Inputs.isKeyJustPressed(Keys.RIGHT)) {
				importRendererOffset.east();
			}
			
			if (Inputs.isKeyPressed(Keys.LEFT)) {
				if(twImportRenderer.canExecute())importRendererOffset.west();
			}
			if (Inputs.isKeyPressed(Keys.RIGHT)) {
				if(twImportRenderer.canExecute())importRendererOffset.east();
			}
			
			
			
			
			
			
			ir.render(importRendererOffset);

			if (Inputs.isKeyJustPressed(Keys.ENTER)) {
				importRendererDone();
			}else if((Inputs.isKeyJustPressed(Keys.BACKSPACE))) {
				importRendererCancel();
				input.clear();
			}

			twImportRenderer.render();
		}
		
		
		

		RayInfo ray = null;
		 if (!MENUFLAG) {
		player.cam.move();
		ray = BlockTrace.ray(player);
		player.update(ray);
		}
		cam.update();

		if(cam!=null)world.render(cam);
		box.render(ray, world);

		batch.setProjectionMatrix(combined);
		batch.begin();
		// GUI.render(batch);
		Overlay.render(batch, cam, ray, world);
		// font.draw(batch, Integer.toString(world.parts.getSize()), 10, 10);
		batch.end();

		input.clearJustPressed();
		

	}

	@Override
	public void resize(int width, int height) {
		view.update(width, height, true);
		Util.screen.setSize(width, height);
		Util.world.setSize(MathUtils.roundPositive(view.getWorldWidth()), MathUtils.roundPositive(view.getWorldHeight()));
		input.clear();
		cam.viewportWidth = width;
		cam.viewportHeight = height;
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void dispose() {
		System.out.println("disposed");
if(true) {		// GdxUtil.disposes(batch, font, box);
		disposes(batch, font, box);
		if (manager != null)
			manager.dispose();
		world.dispose();}
	}

	private static void disposes(Disposable... obj) {
		for (int i = 0, s = obj.length; i < s; i++) {
			if (obj[i] != null)
				obj[i].dispose();
		}
	}

	private void createWorld() {
		long a = System.currentTimeMillis();
		if (world != null)
			return;
		System.out.println(System.currentTimeMillis() - a);
		System.gc();
		try {
			Thread.sleep(500L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void normalSartup() {
		font = new BitmapFont(FileHandler.getFont("minecraftia.fnt"), FileHandler.getFont("minecraftia.png"), false,
				true);
		
		manager = ManagerInit.initialize();
		

        
        
		manager.finishLoading();//TODO: halts rendering and processing thread
		
		
        Array<Texture> textureArray = new Array<>();
        manager.getAll(Texture.class, textureArray);
        System.out.println("manager internal loaded texts test" + textureArray);
		//manager.get("C:/Users/bened/eclipse-workspace/libGDXmapEditorLWJGL2/core/assets/internalBlocks/stone.png", Texture.class);
		//System.out.println((ManagerInit.prefixPath + "missing.png").replace("\\", "/"));
		//manager.get((ManagerInit.prefixPath + "missing.png").replace("\\", "/"), Texture.class);
		
		//manager.get(ManagerInit.prefixPath + "missing.png", Texture.class);
		
		Blocks.loadTextures(manager);

        
		//TexLib.loadTexture();
		TerrainBinder.loadShaders();
		world.intsRender(cam, World.renderDist); // 10
		OpenGL();
	}

	private void OpenGL() {
		final GL20 gl = Gdx.gl;

		gl.glClearColor(0.45f, 0.60f, 0.94f, 1);

		gl.glLineWidth(2);

		//gl.glCullFace(GL20.GL_BACK);
		gl.glEnable(GL20.GL_DEPTH_TEST);
		gl.glDepthFunc(GL20.GL_LEQUAL);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}
	private void showCoordinateDialog() {//move somewhere else TODO
		onHoldQueue = BackendInterface.loadStruct(player); 
		ir = new ImportRenderer(cam, onHoldQueue.getPosArray());
		importRendererOffset = new BlockPos(0,0,0);
		
		
        JTextField xField = new JTextField();
        JTextField yField = new JTextField();
        JTextField zField = new JTextField();

        JPanel masterPanel = new JPanel(new BorderLayout());
        JPanel panel = new JPanel(new GridLayout(0, 2));
        masterPanel.add(panel, BorderLayout.CENTER);
        panel.add(new JLabel("X:"));
        panel.add(xField);
        panel.add(new JLabel("Y:"));
        panel.add(yField);
        panel.add(new JLabel("Z:"));
        panel.add(zField);
        JLabel infoLabel = new JLabel("<html>The offset is applied to the<br>imported structure and does not<br>directly correspond to the new<br>structure's absolute coordinates,<br>as the structure itself can be<br>shifted on the plane. If no input<br>is received, the offset will be 0</html>");
        masterPanel.add(infoLabel, BorderLayout.NORTH);


        int result = JOptionPane.showConfirmDialog(null, masterPanel, "Enter Coordinates Offset for Structure",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int x = Integer.parseInt(xField.getText());
                int y = Integer.parseInt(yField.getText());
                int z = Integer.parseInt(zField.getText());
                importRendererOffset = new BlockPos(x,y,z);
                importRendererDone();
            } catch (NumberFormatException e) {
                // Invalid input, return default values (0, 0, 0)
            }
        }

//        // User canceled or input failed, return default values (0, 0, 0)
    }
	
	private void importRendererDone() {
		player.q.append(onHoldQueue, importRendererOffset);
		BackendInterface.restoreAll(player);
		ir.dispose();
		ir=null;
		importRendererOffset=null;
		onHoldQueue = null;
		
	}
	private void importRendererCancel() {
		try {
		ir.dispose();
		ir=null;
		importRendererOffset=null;
		onHoldQueue = null;
		}catch(Exception e) {
			//nothing; will be thrown on empty canvas 
		}
	}

    public boolean canClose() {
        final boolean[] action = new boolean[]{false};
        final CountDownLatch latch = new CountDownLatch(1);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final JDialog dialog = new JDialog();
                dialog.setTitle("Terminating Application");
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

                JPanel panel = new JPanel();
                panel.setLayout(new GridLayout(1,4));

                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        dialog.dispose();
                        action[0] = false;
                        latch.countDown();
                    }
                });

                JButton quicksaveButton = new JButton("Quicksave");
                quicksaveButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
            			BackendInterface.saveStruct(world, player.q, BackendInterface.lastSaved, true);
                        dialog.dispose();
                        action[0] = true;
                        latch.countDown();
                    }
                });

                JButton saveAndExitButton = new JButton("Save");
                saveAndExitButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
            			BackendInterface.saveStruct(world, player.q, BackendInterface.lastSaved, false);
                        dialog.dispose();
                        action[0] = true;
                        latch.countDown();
                    }
                });

                JButton saveAndStayButton = new JButton("Save+Stay");
                saveAndStayButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
            			BackendInterface.saveStruct(world, player.q, BackendInterface.lastSaved, false);
                        dialog.dispose();
                        action[0] = false;
                        latch.countDown();
                    }
                });

                JButton yesButton = new JButton("Exit");
                yesButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        action[0] = true;
                        latch.countDown();
                    }
                });
                
                dialog.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        dialog.dispose();
                        action[0] = false;
                        latch.countDown();

                    }
                });
                
                panel.add(quicksaveButton);
                dialog.getRootPane().setDefaultButton(quicksaveButton);
                panel.add(cancelButton);
                panel.add(saveAndExitButton);
                panel.add(saveAndStayButton);
                panel.add(yesButton);
                
                dialog.getContentPane().add(BorderLayout.SOUTH, panel);
                JTextArea textArea = new JTextArea("You are about to exit. Make sure you've saved your work. " +
                        "If you have already saved a file in this session, the quicksave feature will save it in the same directory. " +
                        "Otherwise, a new file will be saved to your desktop.");
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                textArea.setEditable(false);
                textArea.setBackground(new JLabel().getBackground()); // Make background same as JLabel

                dialog.getContentPane().add(BorderLayout.CENTER, textArea);
                dialog.setSize(600, 100);
                dialog.setResizable(false);
                dialog.setLocationRelativeTo(null); // Center the dialog
                dialog.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
                dialog.setVisible(true);
            }
        });

        try {
            latch.await(); // Wait for user input
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return action[0];
    }
}
