package io.github.tsudico.disenchanting.client;

import io.github.prospector.modmenu.api.ModMenuApi;
import io.github.tsudico.disenchanting.Disenchanting;
import io.github.tsudico.disenchanting.common.config.DisenchantingConfig;
import me.shedaniel.cloth.api.ConfigScreenBuilder;
import me.shedaniel.cloth.gui.ClothConfigScreen;
import me.shedaniel.cloth.gui.entries.FloatListEntry;
import me.shedaniel.cloth.gui.entries.TextListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
                DisenchantingConfig.save();
            });
            builder.addCategories("Disenchant Table");
            ConfigScreenBuilder.CategoryBuilder disenchantTable = builder.getCategory("Disenchant Table");
            disenchantTable.addOption(new TextListEntry("Cost Formula", "Cost = (Level * LevelMultiplier) * (Offset - (Rarity * RarityMultiplier)"));
            disenchantTable.addOption(new TextListEntry("Multiplier Description", "If Level Multiplier is set to 0, Cost will always be 0."));
            disenchantTable.addOption(
                    new FloatListEntry(
                            "Level Multiplier",
                            DisenchantingConfig.LEVEL_MULTIPLIER,
                            "text.cloth.reset_value",
                            () -> DisenchantingConfig.DEFAULT_LEVEL_MULTIPLIER,
                            (levelMultiplier) -> DisenchantingConfig.LEVEL_MULTIPLIER = levelMultiplier
                    ).setMinimum(0.0F).setMaximum(10.0F));
            disenchantTable.addOption(
                    new FloatListEntry(
                            "Rarity Multiplier",
                            DisenchantingConfig.RARITY_MULTIPLIER,
                            "text.cloth.reset_value",
                            () -> DisenchantingConfig.DEFAULT_RARITY_MULTIPLIER,
                            (rarityMultiplier) -> DisenchantingConfig.RARITY_MULTIPLIER = rarityMultiplier
                    ).setMinimum(0.0F).setMaximum(10.0F));
            disenchantTable.addOption(
                    new FloatListEntry(
                            "Offset",
                            DisenchantingConfig.OFFSET,
                            "text.cloth.reset_value",
                            () -> DisenchantingConfig.DEFAULT_OFFSET,
                            (offset) -> DisenchantingConfig.OFFSET = offset
                    ).setMinimum(0.0F).setMaximum(10.0F));
            builder.doesConfirmSave();
            return builder.build();
        };
    }
}
