package de.alewu.wpbc.files;

import java.io.File;
import java.io.IOException;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class FileHandle {

    private static final ConfigurationProvider PROVIDER = ConfigurationProvider.getProvider(YamlConfiguration.class);
    private final File file;
    final Configuration cfg;

    public FileHandle(File file) {
        this.file = file;
        if (!file.getParentFile().exists()) {
            boolean createdFolder = file.mkdir();
            if (!createdFolder) {
                throw new FileHandleException("Could not create folder " + file.getParent());
            }
        }
        if (!file.exists()) {
            try {
                boolean createdFile = file.createNewFile();
                if (!createdFile) {
                    throw new FileHandleException("Could not create file " + file.getName());
                }
            } catch (IOException e) {
                throw new FileHandleException("Error while creating file " + file.getName(), e);
            }
        }
        try {
            this.cfg = PROVIDER.load(file);
        } catch (IOException e) {
            throw new FileHandleException("Error while loading configuration of file " + file.getName(), e);
        }
    }

    public final Configuration cfg() {
        return cfg;
    }

    public final void save() {
        try {
            PROVIDER.save(cfg, file);
        } catch (IOException e) {
            throw new FileHandleException("Error while saving file " + file.getName());
        }
    }

}
