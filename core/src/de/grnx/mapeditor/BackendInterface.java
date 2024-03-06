package de.grnx.mapeditor;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;

import de.grnx.mapeditor.canvas.World;
import de.grnx.mapeditor.controllable.HistoryQueue;
import de.grnx.mapeditor.controllable.Controller;
import de.grnx.mapeditor.helper.filehandler.Saver;
import de.grnx.mapeditor.helper.filehandler.Extensions;
import de.grnx.mapeditor.helper.filehandler.FileHandler;
import de.grnx.mapeditor.helper.filehandler.SerQsaver;

public class BackendInterface {

	public static String lastSaved;
	public static String lastLoaded;
	public static String lastExported;
	// queue for undo and redo

	public static HistoryQueue loadStruct(Controller p) {

		DisplayMode currentMode = Gdx.graphics.getDisplayMode();
		boolean call = false;
		if (Gdx.graphics.isFullscreen()) {
			currentMode = Gdx.graphics.getDisplayMode();
			Gdx.graphics.setWindowedMode(currentMode.width, currentMode.height);
			call = true;
		}

		File selectedFile = FileHandler.winFileChooser(Extensions.STRUCTURE, lastLoaded);

		if (call)
			Gdx.graphics.setFullscreenMode(currentMode);

		if (selectedFile != null && selectedFile.exists()) {
			lastLoaded = selectedFile.getParent();
			return SerQsaver.load(selectedFile.getAbsolutePath());
		}

		return p.q.cloneLast();
	}

	public static void saveStruct(World world, HistoryQueue q, String path, boolean quicksave) {
		// check first for path
		// Saver.save(world);
		// SerQsaver.save(lastLoaded, q);
		if((path ==null||path.isEmpty())&&quicksave==false) {
			System.out.println(quicksave);

		DisplayMode currentMode = Gdx.graphics.getDisplayMode();
		boolean call = false;
		if (Gdx.graphics.isFullscreen()) {
			currentMode = Gdx.graphics.getDisplayMode();
			Gdx.graphics.setWindowedMode(currentMode.width, currentMode.height);
			call = true;
		}

		File selectedFile = FileHandler.winFileSaver(Extensions.STRUCTURE, lastSaved);
		System.out.println("selected file: " + selectedFile);
		if (selectedFile != null &&!selectedFile.isDirectory()&& (!selectedFile.exists() || selectedFile.canWrite())) {
			if (selectedFile.exists() 
					&& !(JOptionPane.showConfirmDialog(null, "File already exists, want to overwrite?", "File Exists",
							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)) {
				return;
			}
			
			lastSaved = selectedFile.getAbsolutePath();
			System.out.println("now saving:" + selectedFile.getAbsolutePath());
			if (!SerQsaver.save(selectedFile.getAbsolutePath(), q.cleanQueue()))
				JOptionPane.showMessageDialog(null, "The file could not be saved. Try a different path or filename",
						"Error", JOptionPane.ERROR_MESSAGE);
		}
		/*
		 * if(new File(path).exists()) { int result =
		 * JOptionPane.showConfirmDialog(null,
		 * "File already exists. Do you want to overwrite it?", "File Exists",
		 * JOptionPane.YES_NO_OPTION);
		 * 
		 * if (result == JOptionPane.NO_OPTION || result == JOptionPane.CLOSED_OPTION) {
		 * return false; } }
		 */
		if(call)Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		}else {
			if(path==null||path.isEmpty())path=javax.swing.filechooser.FileSystemView.getFileSystemView().getHomeDirectory()+"\\";
		        
			LocalDateTime now = LocalDateTime.now();
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy---HH_mm");
	        String formattedDateTime = now.format(formatter);
			path = path.concat("Quicksave@"+formattedDateTime+Extensions.STRUCTURE.toString());
			System.out.println(path);
			File selectedFile = new File(path);
			System.out.println("selected file: " + selectedFile);
			
			
			if (selectedFile != null && !selectedFile.isDirectory()&&(!selectedFile.exists() || selectedFile.canWrite())) {
				if (selectedFile.exists()
						&& !(JOptionPane.showConfirmDialog(null, "File already exists, want to overwrite?", "File Exists",
								JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)) {
					return;
				}

				System.out.println("now saving: " + selectedFile.getAbsolutePath());
				if (!SerQsaver.save(selectedFile.getAbsolutePath(), q.cleanQueue()))
					JOptionPane.showMessageDialog(null, "The file could not be saved. Try a different path or filename",
							"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		

	}

	/*
	 * public static HistoryQueue loadStruct() { // check first for path
	 * Saver.save(world); SerQsaver.save(lastLoaded, q); return }
	 */

	@Deprecated
	public static void saveWorld(World world) { // TODO waring! deprecated unused and
		String message = "Saving the world instead of exporting as structure is strongly discouraged. If you proceed, the resulting file will likely be mostly uneditable and behave unexpectedly!";
		int fuck = JOptionPane.showOptionDialog(null, message, "Warning", // horrible horrible code; i am ashamed of
																			// myself
				JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
				new Object[] { "Cancel", "Proceed Anyways" }, "Cancel");
		if (fuck != JOptionPane.CLOSED_OPTION && fuck != JOptionPane.OK_OPTION) {
			Saver.save(world, Extensions.WORLD);
		} else {
			return;
		}
	}

	@Deprecated
	public static World loadWorld() { // TODO waring! deprecated unused and
		return Saver.load(Extensions.WORLD);
	}

	public static void newWorld() {
//        showMessage("New World function");
	}

	public static void undo(Controller p) {
		if (p.q.initialized() && !p.q.startReached()) {
			do {
				undoOnce(p);
			} while (p.q.isCurrentImported() && !p.q.startReached());
		}

	}
	
	public static void undoOnce(Controller p) {
		p.undo();//i would have first checked if all conds were meet, but actually this is not needed
	}

	public static void redo(Controller p) {
		if (p.q.initialized() && !p.q.endReached()) {
			do {
				redoOnce(p);
				//System.out.println(p.q.getNext().getImports());
			} while (p.q.isNextImported() && !p.q.endReached());
		}
	}
	
	public static void redoOnce(Controller p) {
		p.redo();//i would have first checked if all conds were meet, but actually this is not needed
	}

	public static void restoreAll(Controller p) {
		// if(p.q.initialized()&&!p.q.endReached())p.redo();
		while (p.q.initialized() && !p.q.endReached()) {
			p.redo();
		}
		// p.q.clear();
	}
	
	public static void undoAll(Controller p) {
		// if(p.q.initialized()&&!p.q.endReached())p.redo();
		while (p.q.initialized() && !p.q.startReached()) {
			p.undo();
		}
		// p.q.clear();
	}
	
	public static void emptyCanvas(Controller p) {
        String message = "Proceeding will clear the current building canvas. This cannot be "
                + "<a href=\"https://www.youtube.com/watch?v=LHQqqM5sr7g\">undone</a>!";

        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setEditable(false);
        textPane.setText("<html>" + message + "</html>");

        textPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        Desktop.getDesktop().browse(new URI(e.getURL().toString()));
                    } catch (IOException | URISyntaxException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });

        int answer = JOptionPane.showOptionDialog(null, textPane, "Warning",
                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
                new Object[] { "Cancel", "Empty!!!" }, "Cancel");


        if (answer != JOptionPane.CLOSED_OPTION && answer != JOptionPane.OK_OPTION) {
			
			String WARNING = "Are you really sure you want start a new Structure? If you havent saved yet, all progress will be lost for eternity!";
			int res = JOptionPane.showOptionDialog(null, WARNING, "Warning", 
					JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
					new Object[] { "No, Im sorry I misclicked, do not empty", "Yes please destroy everything" }, "Cancel");
			if (res != JOptionPane.CLOSED_OPTION && res != JOptionPane.OK_OPTION) {
	        	System.out.println("Now its too late DD: its gone");

				while (p.q.initialized() && !p.q.startReached()) {
					p.undo();
				}
				p.q.clear();
				
			}else {
				return;
			}
			
			} else {
			return;
		}
		
	}

	public static void createSquare() {
		// showMessage("Create Square function");
	}

	public static void exit() {
		Gdx.app.exit();
	}

	public static void export(HistoryQueue q) {
	
		DisplayMode currentMode = Gdx.graphics.getDisplayMode();
		boolean call = false;
		if (Gdx.graphics.isFullscreen()) {
			currentMode = Gdx.graphics.getDisplayMode();
			Gdx.graphics.setWindowedMode(currentMode.width, currentMode.height);
			call = true;
		}

		File selectedFile = FileHandler.winFileSaver(Extensions.JSON, lastExported);
		System.out.println("selected file: " + selectedFile);
		if (selectedFile != null && (!selectedFile.exists() || selectedFile.canWrite())) {
			if (selectedFile.exists()
					&& !(JOptionPane.showConfirmDialog(null, "File already exists, want to overwrite?", "File Exists",
							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)) {
				return;
			}
			
			lastExported = selectedFile.getParent();
			System.out.println("now saving:" + selectedFile.getAbsolutePath());
			if (!SerQsaver.save(selectedFile.getAbsolutePath(), q.toJsonList()))
				JOptionPane.showMessageDialog(null, "The file could not be saved. Try a different path or filename",
						"Error", JOptionPane.ERROR_MESSAGE);
		}
		
		if(call)Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		
		
		

	}

}