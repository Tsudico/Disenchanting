package io.github.tsudico.disenchanting.common.config;


import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileConfig;
import io.github.tsudico.disenchanting.Disenchanting;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.IOException;

import static io.github.tsudico.disenchanting.Disenchanting.*;


public class DisenchantingConfig {
    private static ConfigSpec.CorrectionListener listener = (action, path, incorrectValue, correctedValue) -> {
        String pathString = String.join(",", path);
        MODLOG(Level.WARN, "Configuration error: Corrected " + pathString + ": was " + incorrectValue + ", is now " + correctedValue);
    };
    private static File configFile = new File("config/Disenchanting.toml");
    private static ConfigSpec spec = new ConfigSpec();

    public static void init() {
        // Define configuration
        spec.defineInRange("DisenchantCost.LevelMultiplier", DEFAULT_LEVEL_MULTIPLIER, MIN_FLOAT_VALUE, MAX_FLOAT_VALUE);
        spec.defineInRange("DisenchantCost.RarityMultiplier", DEFAULT_RARITY_MULTIPLIER, MIN_FLOAT_VALUE, MAX_FLOAT_VALUE);
        spec.defineInRange("DisenchantCost.Offset", DEFAULT_OFFSET, MIN_FLOAT_VALUE, MAX_FLOAT_VALUE);

        if (configFile.exists()) {
            load();
        } else {
            save();
        }
    }

    public static void load() {
        if (configFile.exists()) {

            FileConfig config = FileConfig.of(configFile);
            config.load();
            if (!spec.isCorrect(config)) {
                spec.correct(config, listener);
            }

            Disenchanting.LEVEL_MULTIPLIER = config.get("DisenchantCost.LevelMultiplier");
            Disenchanting.RARITY_MULTIPLIER = config.get("DisenchantCost.RarityMultiplier");
            Disenchanting.OFFSET = config.get("DisenchantCost.Offset");

            config.close();
        }
    }

    public static void save() {
        File configDir = new File("config/");
        try {
            if (!configDir.exists()) configDir.mkdir();
            if (!configFile.exists()) configFile.createNewFile();
        } catch (IOException e) {
            Disenchanting.MODLOG(Level.ERROR, e.toString());
        }

        CommentedFileConfig config = CommentedFileConfig.of(configFile);

        config.setComment("DisenchantCost",
                " To set any option to its default, just remove the option from the config file.\n\n"
                        + " Disenchant Cost = (EnchantmentLevel * LevelMultiplier) * (Offset - (EnchantmentRarity * RarityMultiplier))"
                        + "\n     All values must be in the range of 0.00 to 10.00, values outside of range will be set to default"
        );
        config.setComment("DisenchantCost.LevelMultiplier", " If LevelMultiplier is set to 0.00, cost will always be 0\"");
        config.set("DisenchantCost.LevelMultiplier", LEVEL_MULTIPLIER);
        config.set("DisenchantCost.RarityMultiplier", RARITY_MULTIPLIER);
        config.set("DisenchantCost.Offset", OFFSET);
        if (!spec.isCorrect(config)) {
            spec.correct(config, listener);
        }

        config.save();
        config.close();
    }
}
