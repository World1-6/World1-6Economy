package World16Economy.Managers;

import World16Economy.Main.Main;
import World16Economy.Utils.API;
import World16Economy.Utils.Translate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class CustomYmlManager {

    private Main plugin;

    // Files & File Configs Here.
    private FileConfiguration configcfg;
    private File configfile;

    //Strings
    private String nameoffile;
    //...

    public CustomYmlManager(Main plugin) {
        this.plugin = plugin;
    }

    // --------------------------------------------------------------------------------------------------------
    public void setup(String nameoffile) {
        this.nameoffile = nameoffile;

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        configfile = new File(plugin.getDataFolder(), this.nameoffile);

        if (!configfile.exists()) {
            try {
                configfile.createNewFile();
                Bukkit.getServer().getConsoleSender()
                        .sendMessage(Translate.chat(API.USELESS_TAG + " The {nameoffile} has been created.").replace("{nameoffile}", this.nameoffile));
            } catch (IOException e) {
                Bukkit.getServer().getConsoleSender()
                        .sendMessage(Translate
                                .chat(API.USELESS_TAG + " The {nameoffile} could not make for some reason.").replace("{nameoffile}", this.nameoffile));
            }
        }

        configcfg = YamlConfiguration.loadConfiguration(configfile);
    }

    public FileConfiguration getConfig() {
        return configcfg;
    }

    public void saveConfig() {
        try {
            configcfg.save(configfile);
            Bukkit.getServer().getConsoleSender()
                    .sendMessage(Translate.chat(API.USELESS_TAG + " &aThe {nameoffile} has been saved.").replace("{nameoffile}", this.nameoffile));
        } catch (IOException e) {
            Bukkit.getServer().getConsoleSender()
                    .sendMessage(Translate.chat(API.USELESS_TAG + " &cThe {nameoffile} has been NOT SAVED..").replace("{nameoffile}", this.nameoffile));
        }
    }

    public void saveConfigSilent() {
        try {
            configcfg.save(configfile);
        } catch (IOException e) {
            Bukkit.getServer().getConsoleSender()
                    .sendMessage(Translate.chat(API.USELESS_TAG + " &cThe {nameoffile} has been NOT SAVED..").replace("{nameoffile}", this.nameoffile));
        }
    }

    public void reloadConfig() {
        configcfg = YamlConfiguration.loadConfiguration(configfile);
        Bukkit.getServer().getConsoleSender()
                .sendMessage(Translate.chat(API.USELESS_TAG + " &6The {nameoffile} has been reloaded.").replace("{nameoffile}", this.nameoffile));
    }
}