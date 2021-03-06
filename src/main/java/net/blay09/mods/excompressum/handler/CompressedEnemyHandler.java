package net.blay09.mods.excompressum.handler;

import net.blay09.mods.excompressum.ExCompressum;
import net.blay09.mods.excompressum.config.ModConfig;
import net.blay09.mods.excompressum.utils.StupidUtils;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import org.apache.commons.lang3.ArrayUtils;

@SuppressWarnings("unused")
public class CompressedEnemyHandler {

    private static final String COMPRESSED = "Compressed";
    private static final String NOCOMPRESS = "NoCompress";

    @SubscribeEvent
    public void onSpawnEntity(EntityJoinWorldEvent event) {
        if (!event.getWorld().isRemote && (event.getEntity() instanceof EntityCreature || event.getEntity() instanceof EntityGhast)) {
            EntityEntry entry = EntityRegistry.getEntry(event.getEntity().getClass());
            ResourceLocation registryName = entry != null ? entry.getRegistryName() : null;
            String entityName = EntityList.getEntityString(event.getEntity());
            if (registryName != null && ArrayUtils.contains(ModConfig.compressedMobs.allowedMobs, registryName.toString())) {
                NBTTagCompound baseTag = event.getEntity().getEntityData().getCompoundTag(ExCompressum.MOD_ID);
                if (baseTag.hasKey(NOCOMPRESS) || baseTag.hasKey(COMPRESSED)) {
                    return;
                }

                if (event.getEntity().world.rand.nextFloat() <= ModConfig.compressedMobs.chance) {
                    event.getEntity().setAlwaysRenderNameTag(true);
                    event.getEntity().setCustomNameTag("Compressed " + event.getEntity().getName());
                    NBTTagCompound tagCompound = new NBTTagCompound();
                    tagCompound.setBoolean(COMPRESSED, true);
                    event.getEntity().getEntityData().setTag(ExCompressum.MOD_ID, tagCompound);
                } else {
                    NBTTagCompound tagCompound = new NBTTagCompound();
                    tagCompound.setBoolean(NOCOMPRESS, true);
                    event.getEntity().getEntityData().setTag(ExCompressum.MOD_ID, tagCompound);
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        if (!event.getEntity().world.isRemote && event.getEntity().getEntityData().getCompoundTag(ExCompressum.MOD_ID).hasKey(COMPRESSED)) {
            if (event.getEntity() instanceof EntityCreature || event.getEntity() instanceof EntityGhast) {
                if (event.getSource().getTrueSource() instanceof EntityPlayer && !(event.getSource().getTrueSource() instanceof FakePlayer)) {
                    if (StupidUtils.hasSilkTouchModifier((EntityLivingBase) event.getSource().getTrueSource())) {
                        return;
                    }

                    ResourceLocation resourceLocation = EntityList.getKey(event.getEntity());
                    if (resourceLocation == null) {
                        return;
                    }

                    for (int i = 0; i < ModConfig.compressedMobs.size; i++) {
                        EntityLivingBase entity = (EntityLivingBase) EntityList.createEntityByIDFromName(resourceLocation, event.getEntity().world);
                        if (entity == null) {
                            return;
                        }

                        if (((EntityLivingBase) event.getEntity()).isChild()) {
                            if (entity instanceof EntityZombie) {
                                ((EntityZombie) entity).setChild(true);
                            } else if (entity instanceof EntityAgeable && event.getEntity() instanceof EntityAgeable) {
                                ((EntityAgeable) entity).setGrowingAge(((EntityAgeable) event.getEntity()).getGrowingAge());
                            }
                        }
                        if (entity instanceof EntityPigZombie) {
                            entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
                        } else if (entity instanceof EntitySkeleton) {
                            entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
                        } else if (entity instanceof EntityWitherSkeleton) {
                            entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
                        }
                        NBTTagCompound tagCompound = new NBTTagCompound();
                        tagCompound.setBoolean(NOCOMPRESS, true);
                        entity.getEntityData().setTag(ExCompressum.MOD_ID, tagCompound);
                        entity.setLocationAndAngles(event.getEntity().posX, event.getEntity().posY + 1, event.getEntity().posZ, (float) Math.random(), (float) Math.random());
                        double motion = 0.01;
                        entity.motionX = (event.getEntity().world.rand.nextGaussian() - 0.5) * motion;
                        entity.motionY = 0;
                        entity.motionZ = (event.getEntity().world.rand.nextGaussian() - 0.5) * motion;
                        event.getEntity().world.spawnEntity(entity);
                    }
                }
            }
        }
    }

}
