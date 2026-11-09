package net.zwacktus.searcher;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.BuiltInRegistries;

public class SearcherScreen extends Screen {
	private EditBox blockField;
	private EditBox seedField;
	private int radius;
	private final int scannedChunks;
	private final int foundBlocks;
	private String message = "";
	public SearcherScreen(int scannedChunks, int foundBlocks) {
		super(Component.literal("Donut Searcher"));
		radius = DonutSearcher.CONFIG.radius();
		this.scannedChunks = scannedChunks;
		this.foundBlocks = foundBlocks;
	}
	@Override protected void init() {
		int center = width / 2;
		blockField = new EditBox(font, center - 100, height / 2 - 70, 200, 20, Component.literal("Block ID"));
		blockField.setValue(DonutSearcher.CONFIG.blockId()); addRenderableWidget(blockField);
		seedField = new EditBox(font, center - 100, height / 2 - 28, 200, 20, Component.literal("World seed"));
		seedField.setValue(Long.toString(DonutSearcher.CONFIG.seed())); addRenderableWidget(seedField);
		addRenderableWidget(Button.builder(Component.literal("−"), b -> { radius = Math.max(16, radius - 16); }).bounds(center - 100, height / 2 + 8, 40, 20).build());
		addRenderableWidget(Button.builder(Component.literal("+"), b -> { radius = Math.min(512, radius + 16); }).bounds(center + 60, height / 2 + 8, 40, 20).build());
		addRenderableWidget(Button.builder(Component.literal("Save"), b -> save()).bounds(center - 100, height / 2 + 45, 95, 20).build());
		addRenderableWidget(Button.builder(Component.literal("Done"), b -> onClose()).bounds(center + 5, height / 2 + 45, 95, 20).build());
	}
	private void save() {
		String id = blockField.getValue().trim();
		Identifier identifier = Identifier.tryParse(id);
		if (identifier == null || !BuiltInRegistries.BLOCK.containsKey(identifier)) { message = "Unknown block ID"; return; }
		long seed;
		try { seed = Long.parseLong(seedField.getValue().trim()); }
		catch (NumberFormatException e) { message = "Seed must be a whole 64-bit number"; return; }
		DonutSearcher.CONFIG.setBlock(id);
		DonutSearcher.CONFIG.setRadius(radius);
		DonutSearcher.CONFIG.setSeed(seed);
		message = "Saved";
	}
	@Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		graphics.fill(0, 0, width, height, 0x99000000);
		super.render(graphics, mouseX, mouseY, delta);
		graphics.drawCenteredString(font, "Donut Searcher", width / 2, height / 2 - 105, 0xFFFFFF);
		graphics.drawString(font, "Block registry ID", width / 2 - 100, height / 2 - 82, 0xAAAAAA);
		graphics.drawString(font, "World seed", width / 2 - 100, height / 2 - 40, 0xAAAAAA);
		graphics.drawCenteredString(font, "Search radius: " + radius + " blocks (16–512)", width / 2, height / 2 - 2, 0xFFFFFF);
		graphics.drawCenteredString(font, scannedChunks + " chunks", width / 2, height / 2 + 14, 0xFFFFFF);
		graphics.drawCenteredString(font, "Matches: " + foundBlocks, width / 2, height / 2 + 31, 0xAAAAAA);
		graphics.drawCenteredString(font, message, width / 2, height / 2 + 71, 0xFFFF55);
	}
	@Override public boolean isPauseScreen() { return false; }
}
