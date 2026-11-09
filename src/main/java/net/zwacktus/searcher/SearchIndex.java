package net.zwacktus.searcher;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

import java.util.HashSet;
import java.util.Set;

/** Finds target blocks only in chunks already received from the server. */
public final class SearchIndex {
	private final Set<BlockPos> positions = new HashSet<>();
	private int lastChunkX = Integer.MIN_VALUE, lastChunkZ = Integer.MIN_VALUE;
	private int lastRadius = -1;
	private Block lastBlock;
	private long lastScanTick;
	private int scannedChunkCount;

	public void update(Level level, BlockPos player, int radius, Block block) {
		int cx = player.getX() >> 4, cz = player.getZ() >> 4;
		long tick = level.getGameTime();
		if (cx == lastChunkX && cz == lastChunkZ && radius == lastRadius && block == lastBlock && tick - lastScanTick < 20) return;
		lastChunkX = cx; lastChunkZ = cz; lastRadius = radius; lastBlock = block; lastScanTick = tick;
		positions.clear();
		scannedChunkCount = 0;
		int chunkRadius = (radius + 15) >> 4;
		long radiusSq = (long) radius * radius;
		for (int x = cx - chunkRadius; x <= cx + chunkRadius; x++) {
			for (int z = cz - chunkRadius; z <= cz + chunkRadius; z++) {
				int minX = x * 16, minZ = z * 16;
				int nearestX = Math.max(minX, Math.min(minX + 15, player.getX()));
				int nearestZ = Math.max(minZ, Math.min(minZ + 15, player.getZ()));
				long dx = nearestX - player.getX(), dz = nearestZ - player.getZ();
				if (dx * dx + dz * dz > radiusSq || !level.hasChunk(x, z)) continue;
				LevelChunk chunk = level.getChunkSource().getChunkNow(x, z);
				if (chunk == null) continue;
				scannedChunkCount++;
				LevelChunkSection[] sections = chunk.getSections();
				for (int sectionIndex = 0; sectionIndex < sections.length; sectionIndex++) {
					LevelChunkSection section = sections[sectionIndex];
					if (!section.maybeHas(state -> state.is(block))) continue;
					int baseY = (level.getMinSectionY() + sectionIndex) << 4;
					for (int ly = 0; ly < 16; ly++) for (int lx = 0; lx < 16; lx++) for (int lz = 0; lz < 16; lz++) {
						if (!section.getBlockState(lx, ly, lz).is(block)) continue;
						BlockPos pos = new BlockPos(minX + lx, baseY + ly, minZ + lz);
						if (pos.distSqr(player) <= radiusSq) positions.add(pos);
					}
				}
			}
		}
	}

	public Set<BlockPos> positions() { return positions; }
	public int scannedChunkCount() { return scannedChunkCount; }
	public void clear() { positions.clear(); scannedChunkCount = 0; lastChunkX = Integer.MIN_VALUE; lastChunkZ = Integer.MIN_VALUE; }
}
