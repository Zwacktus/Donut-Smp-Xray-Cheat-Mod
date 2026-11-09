package net.zwacktus.searcher;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class DonutSearcherClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		var category = KeyMapping.Category.register(DonutSearcher.id("searcher"));
		KeyMapping openConfig = KeyBindingHelper.registerKeyBinding(new KeyMapping(
			"key.donut-searcher.open_config", GLFW.GLFW_KEY_H, category));
		SearchIndex index = new SearchIndex();
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			index.clear();
			if (!isConfiguredSeed(client)) {
				DonutSearcher.LOGGER.warn("Donut Searcher is disabled: this world has a different seed.");
			}
		});
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> index.clear());
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (openConfig.consumeClick()) client.setScreen(new SearcherScreen(index.scannedChunkCount(), index.positions().size()));
			if (client.level == null || client.player == null || !isConfiguredSeed(client)) return;
			index.update(client.level, client.player.blockPosition(), DonutSearcher.CONFIG.radius(), DonutSearcher.selectedBlock());
		});
		WorldRenderEvents.BEFORE_DEBUG_RENDER.register(context -> {
			var client = net.minecraft.client.Minecraft.getInstance();
			if (client.level == null || client.player == null || !isConfiguredSeed(client)) return;
			SearchRenderer.render(client.player, index.positions());
		});
	}

	private static boolean isConfiguredSeed(net.minecraft.client.Minecraft client) {
		// The server does not send its seed to multiplayer clients. Verify it when it is locally available.
		var server = client.getSingleplayerServer();
		return server == null || server.getWorldData().worldGenOptions().seed() == DonutSearcher.CONFIG.seed();
	}
}
