package de.grnx.mapeditor.controllable;

import de.grnx.mapeditor.helper.BlockPos;
import de.grnx.mapeditor.helper.Camera;

import static de.grnx.mapeditor.buildableConf.Blocks.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import de.grnx.mapeditor.buildableConf.Blocks;
import de.grnx.mapeditor.canvas.World;
import de.grnx.mapeditor.controllable.HistoryQueue.TstampNode;
import de.grnx.mapeditor.controllable.BlockTrace.RayInfo;

//import static de.grnx.mapeditor.Options.bottom_unbreakable;//initialized afterwards, has no effect :(


public class Controller {
	public static Controller player;

	public final World world;
	public final PlayerCamHandle cam;
	public boolean isFlying = false;
	public HistoryQueue q;

	public Controller(Camera cam, World world) {
		this.cam = new PlayerCamHandle(cam);
		this.world = world;
		player = this;
		q = new HistoryQueue(new BlockPos(0,0,0), blocks[world.getBlock(0, 0, 0)]);//initialize first node with random blck

		System.out.println(this.cam.pos.x + "\t" + this.cam.pos.y + "\t" + this.cam.pos.z);
	}

	public void update(RayInfo ray) {
		boolean breakB = false;
		boolean placeB = false;

		if (ray != null) {
			breakB = Gdx.input.isButtonJustPressed(Buttons.LEFT);
			placeB = Gdx.input.isButtonJustPressed(Buttons.RIGHT);

			if ((breakB&&!de.grnx.mapeditor.MapEditorMain.MENUFLAG)&&blocks[world.getBlock(ray.in.x, ray.in.y, ray.in.z)] != blocks[Blocks.BEDROCK]) {
//				System.out.println("before: " + blocks[world.getBlock(ray.in.x, ray.in.y, ray.in.z)]);
				
				q.pushCurrent(new BlockPos(ray.in), blocks[world.getBlock(ray.in.x, ray.in.y, ray.in.z)], blocks[Blocks.AIR]);
			world.editable.breakBlock(ray.in, blocks[Blocks.AIR]);
				
//				System.out.println("after: " +blocks[world.getBlock(ray.in.x, ray.in.y, ray.in.z)]+"\n\n\n");
				BlockTrace.ray(player);
//				q.tempSysout();

//			} if (placeB&&blocks[GUI.blockPick!=Integer.valueOf(blocks[Blocks.HELLROCK]])) {
			}
			if (placeB&& blocks[world.getBlock(ray.out.x, ray.out.y, ray.out.z)] != blocks[Blocks.BEDROCK] && !de.grnx.mapeditor.MapEditorMain.MENUFLAG) {//blocks[GUI.blockPick] != blocks[Blocks.BEDROCK]&&
//				System.out.println("before: " + blocks[world.getBlock(ray.out.x, ray.out.y, ray.out.z)]);

				q.pushCurrent(new BlockPos(ray.out), blocks[world.getBlock(ray.out.x, ray.out.y, ray.out.z)], blocks[Overlay.blockPick]);

				world.editable.placeBlock(ray.out, blocks[Overlay.blockPick]);
//				System.out.println("after: " +blocks[world.getBlock(ray.out.x, ray.out.y, ray.out.z)]+"\n\n\n");

				BlockTrace.ray(player);
//				q.tempSysout();

			}
		}
	}
	
	public void undo() {
		TstampNode prev = q.undo();
		world.editable.placeBlock(prev.pos, prev.oldBlock);
//		q.tempSysout();
	}
	
	public void redo() {
		TstampNode next = q.redo();
		world.editable.placeBlock(next.pos, next.newBlock);
		//Options.sysout(next.pos, next.oldBlock.name +"\n"));
//		q.tempSysout();

	}
}
