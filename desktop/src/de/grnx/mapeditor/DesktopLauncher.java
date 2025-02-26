package de.grnx.mapeditor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.time.Duration;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.badlogic.gdx.tools.particleeditor.Chart.Point;
import com.badlogic.gdx.backends.lwjgl.*;
//import com.badlogic.gdx.backends.lwjgl.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;

import de.grnx.mapeditor.helper.filehandler.Extensions;
import de.grnx.mapeditor.buildableConf.Blocks;
import de.grnx.mapeditor.canvas.World;
import de.grnx.mapeditor.helper.filehandler.Saver;

import com.formdev.flatlaf.FlatLightLaf;

public class DesktopLauncher extends JFrame {
	//cluster fuck of 4 methods to launch my application embedded in different frames and canvasses,
	// back when i was desperately trying to launch it inside a jframe; well to be honest this would have been damn
	// elegant but sadly im constrained by my lwjgl version and glwf and when i finally got it working, the mouse
	// catching did not work at all :(
    private Point centerPoint;
    private static MouseMotionListener input;
    private static KeyListener kInput;
    private static volatile boolean tempCaught = true;
    public static World wrld;
    
    public DesktopLauncher(World world) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final Container container = getContentPane();
        container.setLayout(new BorderLayout());
        input = new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
		        //if(tempCaught)Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		};
		
		kInput = new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				this.keyPressed(e);

			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				this.keyPressed(e);
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER) {
					tempCaught=false;
					//Gdx.app.exit();
					//Options.tempCursorCaughtInternal = false;
				}
			}
		};
        LwjglAWTCanvas canvas = new LwjglAWTCanvas(new MapEditorMain(world));
        //canvas.getCanvas().mouse
        
        canvas.getCanvas().requestFocusInWindow();
        canvas.getCanvas().addMouseMotionListener(input);
        addKeyListener(kInput);
		canvas.getCanvas().addKeyListener(kInput);
        canvas.getCanvas().setFocusable(true);
		canvas.getCanvas().setFocusable(true);
        container.add(canvas.getCanvas(), BorderLayout.CENTER);

        // Calculate the center point of the canvas
        centerPoint = new Point(canvas.getGraphics().getWidth() / 2, canvas.getGraphics().getHeight() / 2);

       /* final Canvas awtCanvas = canvas.getCanvas();
        awtCanvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                // If the mouse is near the edge of the canvas, move it back to the center
                if (e.getX() < 10 || e.getX() > awtCanvas.getWidth() - 10 || e.getY() < 10 || e.getY() > awtCanvas.getHeight() - 10) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Robot robot = new Robot();
                                robot.mouseMove((int)centerPoint.x, (int)centerPoint.y);
                            } catch (AWTException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                }
            }
        });*/
        container.add(new JTextArea(), BorderLayout.NORTH);

        //addMouseMotionListener(input);
        setTitle("MapEditor");
        pack();
        setVisible(true);
        setSize(800, 600);
		  // Add a MouseMotionListener to the canvas
	       /* canvas.addMouseMotionListener(new MouseMotionAdapter() {
	            @Override
	            public void mouseMoved(MouseEvent e) {
	                // If the mouse is near the edge of the canvas, move it back to the center
	                if (e.getX() < 10 || e.getX() > canvas.getGraphics().getWidth() - 10 || e.getY() < 10 || e.getY() > canvas.getGraphics().getHeight() - 10) {
	                    SwingUtilities.invokeLater(() -> {
	                        try {
	                            Robot robot = new Robot();
	                            robot.mouseMove((int)centerPoint.x, (int)centerPoint.y);
	                        } catch (AWTException ex) {
	                            ex.printStackTrace();
	                        }
	                    });
	                }
	            }
	        });*/
	        
	}

	public static void main(String[] args) {
		//FlatDarkLaf.setup();
		FlatLightLaf.setup();
		//LWJGLUtil.PLATFORM_WINDOWS;
		//launchSwingEmbedd();
		Options.mouseCaught = true;
		if(args.length >=1 ) {
			launchApplication(args);
		}else {
					launchApplication();

		}
		
		
	}

	public static void launchApplication() {

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title ="MapEditor";
		config.initialBackgroundColor =Color.BLACK;
		System.setProperty("org.lwjgl.opengl.Window.undecorated", "false");
		
		/*try {
            org.lwjgl.opengl.DisplayMode displayMode = new org.lwjgl.opengl.DisplayMode(800, 600);
            Display.setDisplayMode(displayMode);
            Display.setTitle("My Game");

            // Hide the window title bar (undecorated) by setting resizable to false
            Display.setResizable(false);

            // You can also set other parameters like fullscreen if needed
            // Display.setFullscreen(true);

            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
        }*/ //deprecated fuck	
				/*config.enableGLDebugOutput(true, System.err);
		config.disableAudio(true);

		config.useVsync(true);
		config.setIdleFPS(1);
		config.setTitle("MapEditor");
		config.useOpenGL3(Options.GL3, 3, 2);*/

		/*if (false) { //
			DisplayMode display = LwjglApplicationConfiguration.getDisplayMode();
			System.out.println(display.toString());
			config.setFullscreenMode(display);
		} else {
			config.setWindowedMode(1280, 700);
		}*/

		try {
//			//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); \n 
			
			
			//UIManager.setLookAndFeel();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// GL43.glMultiDrawElements(mode, count, type, indices);

		Blocks.loadBlocks(); //TODO!! uncommented; dealt with in normal startup of desktop main; might crash

		//World world = Saver.load();
		System.gc();
		try {
			Thread.sleep(500L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		try {
///			new LwjglApplication(new MapEditorMain(world, new SwingMenu())/*config*/);
			LwjglApplication appl = new LwjglApplication(new MapEditorMain(new World(true)), config) {
			 @Override
	            public void exit()
	            {
	                if(((MapEditorMain)getApplicationListener()).canClose())
	                    super.exit(); 
	            }
			};
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	

	public static void launchApplication(String...strings) {
		try {
			//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); \n 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String validPath = null;
		
		for(String str : strings) {
			validPath = str;
			if(new File(validPath).exists()) {
				break;
			}
		}
		
		
		World world;

		world = Saver.load(validPath, Extensions.WORLD);


		Blocks.loadBlocks(); //TODO!! uncommented; dealt with in normal startup of desktop main; might crash

		System.gc();
		try {
			Thread.sleep(500L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		try {
			new LwjglApplication(new MapEditorMain(world)/*config*/);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	
	public static void launchApplication_recr() {

	
		try {
			//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); \n 
			
		} catch (Exception e) {
			e.printStackTrace();
		}


		 Blocks.loadBlocks(); ////TODO!! uncommented; dealt with in normal startup of desktop main; might crash

		//World world = Saver.load();
		System.gc();
		try {
			Thread.sleep(500L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
			wrld =new World(true);
			MapEditorMain mp= new MapEditorMain(wrld);
			final MapEditorMain[] mparr = new  MapEditorMain[] {new MapEditorMain(wrld)};
			
		try {
			final LwjglApplication appl =new LwjglApplication(mparr[0]);
			new Thread(new Runnable() {
			    public void run() {
			    	System.out.println("sleep");
			    	try {
						//Thread.sleep(Duration.ofSeconds(5).toMillis());
						Thread.sleep(5000l);
						System.out.println("done");

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    	System.out.println("reset");
			    	appl.stop();
			    	mparr[0]=new MapEditorMain(new World(true));
			    	
			    	
			    	new Thread(new Runnable() {
					    public void run() {
			    	try {
						Thread.sleep(Duration.ofSeconds(5).toMillis());

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    	
			    	new LwjglApplication(mparr[0]);
					    }
					    }).start();
			    }
			}).start();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		
	}
	public static void launchSwingEmbedd() {
		try {
			//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); \n 
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		// GL43.glMultiDrawElements(mode, count, type, indices);

		Blocks.loadBlocks(); ////TODO!! uncommented; dealt with in normal startup of desktop main; might crash

		//final World world = Saver.load();
		System.gc();
		try {
			Thread.sleep(500L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new DesktopLauncher(new World(true));
			}
		});
	}
}
