package de.grnx.mapeditor.helper.filehandler;
import de.grnx.mapeditor.helper.filehandler.ExtensionAPI;

public enum Extensions implements ExtensionAPI {
	WORLD{
		/** @return .Extension*/
		public String toString() {
			return ".swi";
		}		
		/** @return Filetype description */
		public String toName() {
			return "Static World Image";
		}
		
	},
	STRUCTURE{
		/** @return .Extension*/
		public String toString() {
			return ".lsb";
		}
		/** @return Filetype description */
		public String toName() {
			return "Linked Structure Blueprint";
			// hollow, hologhraphic, ;
		}
	},
	
	JSON{
		/** @return .Extension*/
		public String toString() {
			return ".json";
		}
		/** @return Filetype description */
		public String toName() {
			return "Readable Structure JSON Object";
			// hollow, hologhraphic, ;
		}
	},
	STRUCTURE_DPR{
		/** @return .Extension*/
		public String toString() {
			return ".hdb";
		}
		/** @return Filetype description */
		public String toName() {
			return "Heteroformed Dimensional Blueprint";// hexa, hybrid, density, design blocks
			// hollow, hologhraphic, ;
		}
	}
}