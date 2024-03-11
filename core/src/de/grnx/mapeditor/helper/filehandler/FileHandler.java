package de.grnx.mapeditor.helper.filehandler;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import de.grnx.mapeditor.helper.filehandler.WorldFilter;
import de.grnx.mapeditor.helper.filehandler.ExtensionAPI;
import de.grnx.mapeditor.helper.filehandler.Extensions;
import static de.grnx.mapeditor.Options.jarExport;

public class FileHandler {



	public static FileHandle getFile_internal(String path) {
		return Gdx.files.internal(path);
	}
	
	/**Method to switch between internal and external file because eclipse compiling buggy so temporly get the absolute instead of the internal file path**/
	public static FileHandle getFile(String path) {
		if (jarExport) {
			return Gdx.files.internal(path);

		} else {
			return Gdx.files.absolute("C:\\Users\\bened\\eclipse-workspace\\libGDXmapEditorLWJGL2\\core\\assets\\" + path);

		}
	}
	
	/**Method to switch between internal and external file because eclipse compiling buggy so temporly get the absolute instead of the internal file path**/
	public static FileHandle getBlock(String blockName) {
		if (jarExport) {
			return Gdx.files.internal("internalBlocks\\" +blockName);

		} else {
			return Gdx.files.absolute("C:\\Users\\bened\\eclipse-workspace\\libGDXmapEditorLWJGL2\\core\\assets\\internalBlocks\\" + blockName);

		}
	}

	/**Method to switch between internal and external file because eclipse compiling buggy so temporly get the absolute instead of the internal file path**/
	public static FileHandle getFont(String fontName) {
		if (jarExport) {
			return Gdx.files.internal("skin\\font\\" +fontName);

		} else {
			return Gdx.files.absolute("C:\\Users\\bened\\eclipse-workspace\\libGDXmapEditorLWJGL2\\core\\assets\\skin\\font\\" + fontName);

		}
	}
	
	public static FileHandle getAbsoluteHandle(String path) {
		return Gdx.files.absolute(path);
	}

	@Deprecated /**this method is only used in the deprecated save world feature and should be removed, also normalize io operation and centeralize; right now there are countles methods and helper methods, none are used uniformly */
	public static boolean writeAbsoluteBytes(String path, byte[] bytes) {
			try {
				Gdx.files.absolute(path).writeBytes(bytes, false);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

	}

	public static byte[] readAbsoluteBytes_native(String path) {
		try {
			return Gdx.files.absolute(path).readBytes();// Files.readAllBytes(Paths.get(selectedFile.getAbsolutePath()));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	
	public static byte[] readAbsoluteBytes(String path) {
		try {
			return Files.readAllBytes(Paths.get(path));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}


	public static File winFileChooser(ExtensionAPI ext, String path) {
		JFileChooser chooser = new JFileChooser();
		FileFilter filter = new WorldFilter(ext);

		chooser.setFileFilter(filter);
	    if(path!= null &&new File(path).exists())chooser.setCurrentDirectory(new File(path));
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		}
		return null;
	}
	
    /*public static File winFileSaver(ExtensionAPI ext) {
        JFileChooser chooser = new JFileChooser();
        FileFilter filter = new WorldFilter(ext);

        chooser.setFileFilter(filter);
        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();

            // Ensure the selected file has the ".hdb" extension
            if (!selectedFile.getName().toLowerCase().endsWith(ext.toString())) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ext.toString());
            }

            return selectedFile;
        }
        return null;
    }*/



public static File winFileSaver(ExtensionAPI ext, String path) {
    JFileChooser chooser = new JFileChooser();
    FileFilter filter = new WorldFilter(ext);

    chooser.setFileFilter(filter);
    if(path!= null &&new File(path).exists())chooser.setCurrentDirectory(new File(path));
    if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
        File selectedFile = chooser.getSelectedFile();

        // Ensure the selected file has the ".hdb" extension
        if (!selectedFile.getName().toLowerCase().endsWith(ext.toString())) {
            selectedFile = new File(selectedFile.getAbsolutePath() + ext.toString());
        }

        return selectedFile;
    }
    return null;
}

}

class WorldFilter extends FileFilter {
	public ExtensionAPI handler;
	
	public WorldFilter(ExtensionAPI handler) {
		this.handler = handler;
	}
	
	@Override
	public boolean accept(File f) {
		if (f.isDirectory())
			return true;
		String name = f.getName().toLowerCase();
		return name.endsWith(handler.toString());
	}

	@Override
	public String getDescription() {
		return handler.toName() + " (*" + handler.toString() + ")";
	}

}




