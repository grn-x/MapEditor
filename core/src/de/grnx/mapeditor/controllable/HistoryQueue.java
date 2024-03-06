package de.grnx.mapeditor.controllable;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;

import de.grnx.mapeditor.buildableConf.Block;
import de.grnx.mapeditor.buildableConf.Blocks;
import de.grnx.mapeditor.helper.BlockPos;
import de.grnx.mapeditor.helper.filehandler.SerializationUtils;
import javax.json.*;
import javax.json.stream.JsonGenerator;

import java.lang.Cloneable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map; 

public class HistoryQueue implements Serializable {

	private static final long serialVersionUID = 1L;

	// private int pointer;
	private TstampNode current;
	private TstampNode first;
	private TstampNode last;
	
	//Change to private! TODO

	//only for first object
	public HistoryQueue(BlockPos p, Block b) {
		this.first = this.last = this.current = new TstampNode(p, b, b);

		// this.pointer = 0;
	}
	
	private HistoryQueue(TstampNode t) {
		this.first=this.last=this.current = t;
	}

	@Deprecated
	public HistoryQueue push(BlockPos pos, Block oldBlock, Block newBlock, TstampNode prev) {
		last.setNextNode(new TstampNode(pos, oldBlock, newBlock, prev));
		return this;
	}

	public void clear() {
		this.first.next = this.first;
		this.current = this.last = this.first;
		//System.out.println(this.last.qPos);
	}

	public HistoryQueue pushCurrent(BlockPos pos, Block oldBlock, Block newBlock) {

		if (first == null) {
			first = last = current = new TstampNode(pos, oldBlock, newBlock);
		} else {
//		current.next = current.setNextNode(new TstampNode(pos, oldBlock, newBlock, current)); //operation is from right to left you say?
			current = last = current.next = current.setNextNode(new TstampNode(pos, oldBlock, newBlock, current));
		}
		return this;
	}

	public HistoryQueue pushCurrent(TstampNode t) {

		if (first == null) {
			
			t.prev=t;
			first = last = current = t;
		} else {
//		current.next = current.setNextNode(new TstampNode(pos, oldBlock, newBlock, current)); //operation is from right to left you say?
			current = last = current.next = current.setNextNode(t);
		}
		return this;
	}
	
	@Deprecated
	public boolean isTerminal() {
		return current.containsSelfRef();
	}

	public boolean startReached() {
		// return current.isFirst();
		return this.current == this.first ? true : false;
	}

	public boolean endReached() {
		// return current.isLast();
		//return this.current == this.last ? true : false;
		//return this.current.next ==this.current? true : false;
		return this.current == this.current.next ?true:false;
	}

	public boolean initialized() {
		return first == null ? false : true;

	}

	/** pushCurrent self references itself on first node creation **/
	public TstampNode getPrevious() {
		return current = current.getPrevNode();
	}

	public TstampNode undo() {
		current = current.getPrevNode();
		return current.getNextNode();
	}

	/** Node Object self references itself on initialization **/
	public TstampNode getNext() {
		return current = current.getNextNode();
	}
	
	/** forgot what the above method exactly did, oopsie, this returns the current node and then increments to the next node afterwards*/
	public TstampNode increment() {
		current=current.getNextNode();
		return current.prev;
	}

	public TstampNode redo() {
		return current = current.getNextNode();
		/*
		 * if(current.isLast()) { return last.getNextNode(); } return
		 * current.getPrevNode();
		 */
	}
	public boolean isPrevImported() {
		return this.current.prev.imports!=null?true :false;
	}
	
	public boolean isCurrentImported() {
		return this.current.imports!=null?true :false;

	}
	
	public boolean isNextImported() {
		return this.current.next.imports!=null?true :false;
	}
	
	public BlockPos[] getPosArray() {
		//BlockPos[] pos = new BlockPos[last.qPos+1];//array out of bounds shit im currently too tired to fix
		List<BlockPos> pos = new ArrayList<BlockPos>();
		TstampNode firstRef = this.first;
		//int i = 0;
		do{
			//pos[i++]=firstRef.pos.cpy();
			pos.add(firstRef.pos.cpy());
			firstRef = firstRef.next;
//		}while(firstRef!=this.last); 
		}while(firstRef.next!=firstRef); 
		
		pos.add(firstRef.next.pos.cpy());
		
		return pos.toArray(new BlockPos[pos.size()]);
	}

	@Deprecated
	public TstampNode getPrevNode(TstampNode n) {
		return n.getPrevNode();
	}

	@Deprecated 
	public TstampNode getNextNode(TstampNode n) {
		return n.getNextNode();
	}

	public BlockPos getPos(TstampNode n) {
		return n.getPos();
	}

	public Block getOldBlock(TstampNode n) {
		return n.getOldBlock();
	}

	public Block getNewBlock(TstampNode n) {
		return n.getNewBlock();
	}

	public String getFirstBytes() {
		return SerializationUtils.bytesToHex(SerializationUtils.serialize(last));
	}
	
	@Deprecated /** for debugging only!**/
	public void tempToStart() {
		this.current =this.first;
	}
	
	/** in case deserialization fails, return new queue with last entry cloned instead of null to append**/
	public HistoryQueue cloneLast() {
		return new HistoryQueue(last.clone());
//		return new HistoryQueue(last.clone(last));
	}
	
	/*public void append(HistoryQueue que) { works but unused
		que.current = que.first;
		while(!que.endReached()) {
			
			if(que.current.newBlock.id>Blocks.blocks.length-1) {
				//que.current.newBlock = Blocks.blocks[Blocks.UNSUPPORTED];
				TstampNode prevRef = que.current.prev;
				TstampNode nextRef = que.current.next;
				que.current= new TstampNode(que.current.pos, que.current.oldBlock, Blocks.blocks[Blocks.UNSUPPORTED], prevRef); 
				que.current.next=nextRef;
				prevRef.next=nextRef.prev=que.current;

			}
			if(que.current.oldBlock.id>Blocks.blocks.length-1) {
				TstampNode prevRef = que.current.prev;
				TstampNode nextRef = que.current.next;
				que.current= new TstampNode(que.current.pos,   Blocks.blocks[Blocks.UNSUPPORTED], que.current.newBlock, prevRef).next=nextRef; 
				que.current.next=nextRef;

				prevRef.next=nextRef.prev=que.current;
				

				
			}
			que.current= que.current.next;
		}
		this.append_unchecked(que);
		
	}*/
	
	/**overloaded append method with option to move structs*/
	public void append(HistoryQueue que, BlockPos moved) {
		//this.append_unchecked(que);
		this.append_unchecked(checkAndMove(que, moved));
		
	}
	
	
	
	private HistoryQueue checkAndMove(HistoryQueue que, BlockPos moved) {
		que.current = que.first;
		while(que.current!=que.last) {
			if(que.current.newBlock.id>Blocks.blocks_internal.length-1) {
				//que.current.newBlock = Blocks.blocks[Blocks.UNSUPPORTED];
				TstampNode prevRef = que.current.prev;
				TstampNode nextRef = que.current.next;
				que.current= new TstampNode(que.current.pos, que.current.oldBlock, Blocks.blocks[Blocks.UNSUPPORTED], prevRef); 
				que.current.next=nextRef;
				prevRef.next=nextRef.prev=que.current;
			}
			if(que.current.oldBlock.id>Blocks.blocks_internal.length-1) {
				TstampNode prevRef = que.current.prev;
				TstampNode nextRef = que.current.next;
				que.current= new TstampNode(que.current.pos,   Blocks.blocks[Blocks.UNSUPPORTED], que.current.newBlock, prevRef).next=nextRef; 
				que.current.next=nextRef;

				prevRef.next=nextRef.prev=que.current;	

			}
			
			que.current.setImports(this.current);
			que.current.pos.add(moved);
			que.current= que.current.next;
		}
		que.current.pos.add(moved);
		que.current.setImports(this.current);
		
		return que;
	}
	
	
	/** avoid! unchecked and doesnt support moving blocks! only use if the que was modified and tested before*/
	private void append_unchecked(HistoryQueue que) {
		this.last.next=que.first;
		 this.last.next.prev=this.last;
		 this.last=que.last;
		 
		/* System.out.println("arrayTime");
		 this.current=this.first;
			while(this.current!=this.last) {
				System.out.println(this.current.oldBlock.id + "\t" + this.current.newBlock.id);
				this.current= this.current.next;
			}*/
	}
	
    public String toJsonList() {
    	this.cleanQueue();
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

        TstampNode currentRef = this.first;
        while (currentRef != this.last) {
        	jsonArrayBuilder.add(Json.createReader(new StringReader(currentRef.toJSON())).readObject());
            //jsonArrayBuilder.add(currentRef.toJSON()); // >:( will fuck up line breaks etc.. D-:<
            currentRef = currentRef.next;
        }
    	jsonArrayBuilder.add(Json.createReader(new StringReader(currentRef.toJSON())).readObject());

        JsonArray jsonArray = jsonArrayBuilder.build();
        return jsonArray.toString();
    }
    /*public String toJsonList() {
    	this.cleanQueue();
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

        TstampNode currentRef = this.first;
        while (currentRef != this.last) {
           // jsonArrayBuilder.add(Json.createReader(new StringReader(currentRef.toJSON())).readObject());
            jsonArrayBuilder.add(currentRef.toJSON()); // >:( will fuck up line breaks etc.. D-:<
            currentRef = currentRef.next;
        }
        
        JsonArray jsonArray = jsonArrayBuilder.build();
        StringWriter sw = new StringWriter();
        try {
           JsonReader jsonReader = Json.createReader(new StringReader(jsonArray.toString()));
           JsonObject jsonObj = jsonReader.readObject();
           Map<String, Object> map = new HashMap<>();
           map.put(JsonGenerator.PRETTY_PRINTING, true);
           JsonWriterFactory writerFactory = Json.createWriterFactory(map);
           JsonWriter jsonWriter = writerFactory.createWriter(sw);
           jsonWriter.writeObject(jsonObj);
           jsonWriter.close();
        } catch(Exception e) {
           e.printStackTrace();
        }
        String prettyPrint = sw.toString();
        return prettyPrint;
    }*/
   /* public String toJsonList() {
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

        TstampNode currentRef = this.first;
        while (currentRef != null) {
            JsonObject jsonObject = Json.createReader(new StringReader(currentRef.toJSON())).readObject();
            jsonArrayBuilder.add(jsonObject);
            currentRef = currentRef.getNextNode();
        }

        JsonArray jsonArray = jsonArrayBuilder.build();

        // Configure JSON writer with formatting options
        Map<String, Object> properties = new HashMap<>(1);
        properties.put(JsonGenerator.PRETTY_PRINTING, true);

        JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
        StringWriter stringWriter = new StringWriter();
        try (JsonWriter jsonWriter = writerFactory.createWriter(stringWriter)) {
            jsonWriter.writeArray(jsonArray);
        }

        return stringWriter.toString();
    }*/
	
	/**if a queue gets imported where blocks were placed and later broken these ghost blocks will will be placed nevertheless 
	 * and replace existing blocks of buildings with air. This method removes those blocks and should used right before exporting*/
	public HistoryQueue cleanQueue(){
		HashMap<BlockPos, TstampNode> dB = new HashMap<BlockPos, HistoryQueue.TstampNode>();
		
		TstampNode lastRef = this.last;
		while(lastRef!=this.first) {
			if(lastRef.newBlock==Blocks.blocks[Blocks.AIR]) {
				dB.put(lastRef.pos, lastRef);
				//is it safe to remove these already? i think so
				this.removeNodeAndStitch(lastRef);
				
				
			}else {
				TstampNode airRef = dB.get(lastRef.pos);
				if(airRef!=null/*&&lastRef.qPos<airRef.qPos*/){//second condition should never be true, as we iterate from last to first, thus the air block is not present inside the hashmap yet
					//remove 
					this.removeNodeAndStitch(lastRef);
				}
			}
			
			lastRef = lastRef.prev;
			
		}
		return this;
	}
	
	private void removeNodeAndStitch(TstampNode t) {
		if(t.containsSelfRef())return;
		
		TstampNode prev = t.prev;
		TstampNode next = t.next;
		
		prev.next=next;
		next.prev=prev;
	}
	
	public final class TstampNode implements Serializable, Cloneable{

		private static final long serialVersionUID = 1L;
		public final BlockPos pos;
		public final Block oldBlock;
		public final Block newBlock;
		
		
/*why was this public?*/private /*final*/ TstampNode prev; //non final only because binding between old and new queue in append() needs two way back and forth connection 
		private TstampNode next;
		private TstampNode imports;

		//public final int qPos;

		private TstampNode(BlockPos pos, Block oldBlock, Block newBlock, TstampNode prev) {
			this.pos = pos;
			this.oldBlock = oldBlock;
			this.newBlock = newBlock;
			this.prev = prev;
			this.next = this;
			//this.qPos = prev.qPos + 1;// will corrupt if remove and stitch; the only way to fix would be to loop and update every reference which is too time consuming and useless
		}
		
		//only for first object; qPos!
		private TstampNode(BlockPos pos, Block oldBlock, Block newBlock) {
			this.pos = pos;
			this.oldBlock = oldBlock;
			this.newBlock = newBlock;
			this.prev = this;
			this.next = this;
			// this.qPos = prev.qPos+1;//prev pos should be 0 because first object; first
			// queue object pos thus is one and first usable queue pos is 2 || Dont like
			// this approach; hardcode 0
			// this.qPos = 0; || dont like this approach either
			//this.qPos = 1 + this.prev.qPos == 0 ? -1 : this.prev.qPos; // will corrupt if remove and stitch; the only way to fix would be to loop and update every reference which is too time consuming and useless
		}

		public TstampNode getImports() {
			return imports;
		}

		public void setImports(TstampNode imports) {
			this.imports = imports;
		}

		public String toString(){
			return "Time Stamp Object Fields and References:"
					+"\n\t Position: " + pos
					//+ "\n\t Queue Position: " + qPos
					+ "\n\t Previous Block: " + oldBlock
					+ "\n\t Replacing New Block: " + newBlock;
		}
		
		private BlockPos getPos() {
			return this.pos;
		}

		private Block getOldBlock() {
			return this.oldBlock;
		}

		private Block getNewBlock() {
			return this.newBlock;
		}

		private TstampNode getPrevNode() {
			return this.prev;
		}

		private TstampNode getNextNode() {
			return this.next;
		}

		private TstampNode setNextNode(TstampNode next) {
			this.next = next;
			return next;
		}

		@SuppressWarnings(value = { "unused" })
		@Deprecated /** buggy **/
		private boolean isFirst() {
			return this == this.prev ? true : false;
		}

		@SuppressWarnings(value = { "unused" })
		@Deprecated /** buggy **/
		private boolean isLast() {
			return this == this.prev ? true : false;
		}

		@Deprecated
		private boolean containsSelfRef() {
			return (this == this.prev || this == this.next) ? true : false;
		}

		
		
		/*private void writeObject(java.io.ObjectOutputStream out) throws IOException {
			byte oldBlock = this.oldBlock.id;
			byte newBlock = this.newBlock.id;

		}

		private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
			this.oldBlock = new Block;
		}

		public final class SerBlock*/
		
	    public String toJSON() {
	        JsonObjectBuilder builder = Json.createObjectBuilder()
	                .add("pos", pos.toString())
	                //.add("qPos", qPos)
	                					;
	        // Add oldBlock information
	        JsonObjectBuilder oldBlockBuilder = Json.createObjectBuilder()
	                .add("id", oldBlock.id & 0xFF)
	                .add("name", oldBlock.name)
	                .add("isSolid", oldBlock.isSolid)
	                .add("isTrans", oldBlock.isTrans)
	                .add("collision", oldBlock.collision)
	                .add("type", oldBlock.type.toString())
	                .add("top", oldBlock.top==null?"":oldBlock.top)
	                .add("side", oldBlock.side==null?"":oldBlock.side)
	                .add("bottom", oldBlock.bottom==null?"":oldBlock.bottom);

	        builder.add("oldBlock", oldBlockBuilder);

	        // Add newBlock information (similar to oldBlock)
	        JsonObjectBuilder newBlockBuilder = Json.createObjectBuilder()
	                .add("id", newBlock.id & 0xFF)
	                .add("name", newBlock.name)
	                .add("isSolid", newBlock.isSolid)
	                .add("isTrans", newBlock.isTrans)
	                .add("collision", newBlock.collision)
	                .add("type", newBlock.type.toString())
	                .add("top", newBlock.top==null?"":newBlock.top)
	                .add("side", newBlock.side==null?"":newBlock.side)
	                .add("bottom", newBlock.bottom==null?"":newBlock.bottom);

	        builder.add("newBlock", newBlockBuilder);

	        
	        String res = builder.build().toString();
	        return res;
	    }
		
		@Override /** only used in HistoryQueue.cloneLast(); serves same purpose; adjust previous reference to last parent queue ref*/
		public TstampNode clone() {
			//TstampNode t = new TstampNode(new BlockPos(this.pos), new Block(this.oldBlock), new Block(this.newBlock));
			TstampNode t = new TstampNode(new BlockPos(this.pos), new Block(this.oldBlock), new Block(this.newBlock), this);
			return t;
		}
		
		public TstampNode clone(TstampNode reference) {
			//TstampNode t = new TstampNode(new BlockPos(this.pos), new Block(this.oldBlock), new Block(this.newBlock));
			TstampNode t = new TstampNode(new BlockPos(this.pos), new Block(this.oldBlock), new Block(this.newBlock), reference);
			return t;
		}
	}

}
