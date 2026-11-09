package net.zwacktus.searcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.Identifier;

import java.nio.file.Files;
import java.nio.file.Path;

public final class SearchConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private final Path file;
	private int radius = 250;
	private String block = "minecraft:deepslate_coal_ore";
	private long seed = DonutSearcher.DEFAULT_WORLD_SEED;
	private static final class StoredConfig {
		int radius = 250;
		String block = "minecraft:deepslate_coal_ore";
		long seed = DonutSearcher.DEFAULT_WORLD_SEED;
	}

	private SearchConfig(Path file) { this.file = file; }

	public static SearchConfig load() {
		Path path = Path.of("config", "donut-searcher.json");
		SearchConfig result = new SearchConfig(path);
		try {
			if (Files.exists(path)) {
				StoredConfig saved = GSON.fromJson(Files.readString(path), StoredConfig.class);
				if (saved != null) {
					result.radius = Math.max(16, Math.min(512, saved.radius));
					if (validBlockId(saved.block)) result.block = saved.block;
					result.seed = saved.seed;
				}
			}
		} catch (Exception e) { DonutSearcher.LOGGER.warn("Could not load searcher config", e); }
		result.save();
		return result;
	}

	public int radius() { return radius; }
	public String blockId() { return block; }
	public long seed() { return seed; }
	public void setRadius(int value) { radius = Math.max(16, Math.min(512, value)); save(); }
	public void setBlock(String value) { if (validBlockId(value)) { block = value; save(); } }
	public void setSeed(long value) { seed = value; save(); }
	private static boolean validBlockId(String value) { return value != null && Identifier.tryParse(value) != null; }
	private void save() {
		try {
			Files.createDirectories(file.getParent());
			StoredConfig saved = new StoredConfig();
			saved.radius = radius;
			saved.block = block;
			saved.seed = seed;
			Files.writeString(file, GSON.toJson(saved));
		}
		catch (Exception e) { DonutSearcher.LOGGER.warn("Could not save searcher config", e); }
	}
}
