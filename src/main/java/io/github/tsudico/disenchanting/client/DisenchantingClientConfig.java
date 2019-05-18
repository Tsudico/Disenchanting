package io.github.tsudico.disenchanting.client;

import io.github.prospector.modmenu.api.ModMenuApi;
import io.github.tsudico.disenchanting.Disenchanting;
import io.github.tsudico.disenchanting.common.config.DisenchantingConfig;
import me.shedaniel.cloth.api.ConfigScreenBuilder;
import me.shedaniel.cloth.gui.ClothConfigScreen;
import me.shedaniel.cloth.gui.entries.DoubleListEntry;
import me.shedaniel.cloth.gui.entries.TextListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.Screen;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class DisenchantingClientConfig implements ModMenuApi {

    @Override
    public String getModId() {
        return Disenchanting.MODID;
    }

    public Function<Screen, ? extends Screen> getConfigScreenFactory() {
        return (screen) -> {
            ClothConfigScreen.Builder builder = new ClothConfigScreen.Builder(screen, "Disenchanting Config", (config) -> {
                if (FabricLoader.getInstance().isModLoaded("nightconfig4fabric")) {
                    DisenchantingConfig.save();
                }
            });
            builder.addCategories("Disenchant Table");
            ConfigScreenBuilder.CategoryBuilder disenchantTable = builder.getCategory("Disenchant Table");
            if (!FabricLoader.getInstance().isModLoaded("nightconfig4fabric")) {
                disenchantTable.addOption(new TextListEntry("Config Save", "If you want to adjust configuration, please add the mod:\n\n     Night Config 4 Fabric"));
            } else {
                disenchantTable.addOption(new TextListEntry("Cost Formula", "Cost = (Level * LevelMultiplier) * (Offset - (Rarity * RarityMultiplier)"));
                disenchantTable.addOption(new TextListEntry("Multiplier Description", "If Level Multiplier is set to 0, Cost will always be 0."));
                disenchantTable.addOption(
                        new DoubleListEntry(
                                "Level Multiplier",
                                Disenchanting.LEVEL_MULTIPLIER,
                                "text.cloth.reset_value",
                                () -> Disenchanting.DEFAULT_LEVEL_MULTIPLIER,
                                (levelMultiplier) -> Disenchanting.LEVEL_MULTIPLIER = levelMultiplier
                        ).setMinimum(Disenchanting.MIN_FLOAT_VALUE).setMaximum(Disenchanting.MAX_FLOAT_VALUE));
                disenchantTable.addOption(
                        new DoubleListEntry(
                                "Rarity Multiplier",
                                Disenchanting.RARITY_MULTIPLIER,
                                "text.cloth.reset_value",
                                () -> Disenchanting.DEFAULT_RARITY_MULTIPLIER,
                                (rarityMultiplier) -> Disenchanting.RARITY_MULTIPLIER = rarityMultiplier
                        ).setMinimum(Disenchanting.MIN_FLOAT_VALUE).setMaximum(Disenchanting.MAX_FLOAT_VALUE));
                disenchantTable.addOption(
                        new DoubleListEntry(
                                "Offset",
                                Disenchanting.OFFSET,
                                "text.cloth.reset_value",
                                () -> Disenchanting.DEFAULT_OFFSET,
                                (offset) -> Disenchanting.OFFSET = offset
                        ).setMinimum(Disenchanting.MIN_FLOAT_VALUE).setMaximum(Disenchanting.MAX_FLOAT_VALUE));
            }
            builder.doesConfirmSave();
            return builder.build();
        };
    }
}
