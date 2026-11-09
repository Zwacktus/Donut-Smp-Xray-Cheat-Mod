package net.zwacktus.searcher;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DonutSearcher implements ModInitializer {
	public static final String MOD_ID = "donut-searcher";
	public static final long DEFAULT_WORLD_SEED = 6608149111735331168L;
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final SearchConfig CONFIG = SearchConfig.load();

	@Override
	public void onInitialize() {
		// Client-only registrations are isolated in the client entrypoint below.
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	public static Block selectedBlock() {
		return BuiltInRegistries.BLOCK.getValue(Identifier.parse(CONFIG.blockId()));
	}
}
