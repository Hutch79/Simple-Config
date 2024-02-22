package ch.hutch79;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class SimpleConfig {
    private String pluginPath;
    private ObjectMapper writeMapper;
    private ObjectMapper readMapper;
    private HashMap<Class<?>, Object> configCache = new HashMap<>();

    public SimpleConfig(File _pluginPath) {
        pluginPath = _pluginPath.toString();

        writeMapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
        writeMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        writeMapper.findAndRegisterModules();

        readMapper = new ObjectMapper(new YAMLFactory());
        readMapper.findAndRegisterModules();
    }


    /**
     * @param configClass Class which shoud be serialized to YAML
     * @param localPath Local Pluginfolder path where the file should be safed ("config.yml" => "plugins/yourPlugin/config.yml")
     */
    public void writeConfig (Object configClass, String localPath) {
        try {
            writeMapper.writeValue(new File(pluginPath + "/" + localPath), configClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param configClass Class which will be used to serialize the YAML file to
     * @param localPath Local Pluginfolder path where the file is saved ("config.yml" => "plugins/yourPlugin/config.yml")
     * @return this
     */
    public <T> SimpleConfig loadConfig(Class<T> configClass, String localPath) {

        try {
            T config = readMapper.readValue(new File(pluginPath + File.separatorChar + localPath), configClass);
            configCache.put(configClass, config);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return this;
    }

    /**
     * @param configClass Config Class you want to get
     * @return Config Class
     */
    public <T> T getConfig(Class<?> configClass) {
        return (T) configCache.get(configClass);
    }
}