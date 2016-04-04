package net.blay09.mods.excompressum.client.render.item;

import net.blay09.mods.excompressum.tile.TileEntityAutoHammer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class ItemRenderAutoHammer implements IItemRenderer {

    private static final TileEntityAutoHammer tileEntity = new TileEntityAutoHammer();

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return type != ItemRenderType.FIRST_PERSON_MAP;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return type != ItemRenderType.FIRST_PERSON_MAP;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glTranslatef(0, -0.1f, 0);
        TileEntityRendererDispatcher.instance.renderTileEntityAt(tileEntity, 0, 0, 0, 0);
    }

}
