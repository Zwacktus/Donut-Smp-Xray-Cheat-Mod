package net.zwacktus.searcher;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.world.entity.player.Player;

import java.util.Set;

public final class SearchRenderer {
	private SearchRenderer() {}
	public static void render(Player player, Set<BlockPos> positions) {
		var levelRenderer = Minecraft.getInstance().levelRenderer;
		try (var ignored = levelRenderer.collectPerFrameGizmos()) {
			for (BlockPos pos : positions) {
				if (pos.distSqr(player.blockPosition()) <= 512L * 512L) {
					Gizmos.cuboid(pos, GizmoStyle.stroke(0xDD33FF55)).setAlwaysOnTop();
				}
			}
		}
	}
}
