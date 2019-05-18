package io.github.tsudico.disenchanting;

import io.github.tsudico.disenchanting.common.block.DisenchantTableBlock;
import io.github.tsudico.disenchanting.common.block.DisenchantTableBlockEntity;
import io.github.tsudico.disenchanting.common.config.DisenchantingConfig;
import io.github.tsudico.disenchanting.common.container.DisenchantTableContainer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Disenchanting implements ModInitializer {
    public static double LEVEL_MULTIPLIER = 5.0F;
    public static double OFFSET = 1.6F;
    public static double RARITY_MULTIPLIER = 0.03F;
    public static double DEFAULT_LEVEL_MULTIPLIER = 5.0F;
    public static double DEFAULT_OFFSET = 1.6F;
    public static double DEFAULT_RARITY_MULTIPLIER = 0.03F;
    public static double MIN_FLOAT_VALUE = 0.0F;
    public static double MAX_FLOAT_VALUE = 10.0F;

    public static final String MODID = "disenchanting";
    // An instance of our new block
    public static final Block DISENCHANT_TABLE = new DisenchantTableBlock(
            FabricBlockSettings.of(Material.STONE).materialColor(MaterialColor.QUARTZ).strength(5.0F, 1200.0F).build()
    );
    public static final BlockEntityType<DisenchantTableBlockEntity> DISENCHANT_TABLE_ENTITY =
            BlockEntityType.Builder.create(DisenchantTableBlockEntity::new, DISENCHANT_TABLE).build(null);
    // Container for the block
    public static final Identifier DISENCHANT_CONTAINER = new Identifier(MODID, "disenchant_container");

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitialize() {
        MODLOG("Initializing Disenchanting");

        // Block registration
        Registry.register(Registry.BLOCK, new Identifier(MODID, "disenchant_table"), DISENCHANT_TABLE);
        Registry.register(Registry.BLOCK_ENTITY, new Identifier(MODID, "disenchant_table"), DISENCHANT_TABLE_ENTITY);
        // Item registration
        Registry.register(Registry.ITEM, new Identifier(MODID, "disenchant_table"), new BlockItem(DISENCHANT_TABLE, new Item.Settings().itemGroup(ItemGroup.MISC)));
        // Container registration
        ContainerProviderRegistry.INSTANCE.registerFactory(Disenchanting.DISENCHANT_CONTAINER, (syncId, identifier, player, buf) -> {
            return new DisenchantTableContainer(syncId, player);
        });

        if (FabricLoader.getInstance().isModLoaded("nightconfig4fabric")) {
            DisenchantingConfig.init();
        }
    }

    public static void MODLOG(String string) {
        MODLOG(Level.INFO, string);
    }

    public static void MODLOG(Level level, String string) {
        LOGGER.log(level, "[Disenchanting] " + string);
    }
}