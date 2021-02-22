package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class Configuration {

    private static String HTTPD_CONFIG_PATH;
    private static String MIME_TYPE_CONFIG_PATH;

    private static final Map<String, List<String>> httpd_config = new HashMap<String, List<String>>();
    private static final Map<String, List<String>> mime_types_config = new HashMap<String, List<String>>();

    private static final Map<String, String> config = new HashMap<String, String>();
    private static final Map<String, String> aliases = new HashMap<String, String>();
    private static final Map<String, String> mimeTypes = new HashMap<String, String>();

    public Configuration(String httpdConfigPath, String mimeTypesConfigPath) {
        Configuration.HTTPD_CONFIG_PATH = httpdConfigPath;
        Configuration.MIME_TYPE_CONFIG_PATH = mimeTypesConfigPath;
    }

    public void init() throws IOException {
        // Load both the httpd.conf and mime.types files.
        loadConfigFile(HTTPD_CONFIG_PATH, httpd_config);
        loadConfigFile(MIME_TYPE_CONFIG_PATH, mime_types_config);

        for (Entry<String, List<String>> entry : httpd_config.entrySet()) {
            // generate alias map
            if (entry.getKey().equals("Alias")) {
                for (String alias : entry.getValue()) {
                    // Aliases have the format of Alias </dir/> <./root/dir/>
                    // where '/dir/' points to './root/dir/'.
                    String[] aliasSplit = alias.split(" ");
                    aliases.put(aliasSplit[0], aliasSplit[1]);
                }
            } else { // generate the config map
                for (String c : entry.getValue()) {
                    config.put(entry.getKey(), c);
                }
            }
        }

        // generate mime types map
        for (Entry<String, List<String>> entry : mime_types_config.entrySet()) {
            if (entry.getValue().size() > 0) {
                String[] exts = entry.getValue().get(0).toString().split("\\s+");
                for (String extension : exts) {
                    mimeTypes.put(extension, entry.getKey());
                }
            }
        }
    }

    /**
     * Returns the config property of the given key.
     * 
     * @param key - property key to look for in config
     * @return value or null if property doesn't exist
     */
    public static final String getConfigProperty(String key) {
        return config.get(key);
    }

    /**
     * Returns the mime type of an extension. Returns "text/html" if extension is
     * not found in mime.types
     * 
     * @param extension - file extension
     * @return corresponding mime type
     */
    public static final String getMimeType(String extension) {
        if (mimeTypes.get(extension) == null) {
            return "text/html";
        }

        return mimeTypes.get(extension);
    }

    /**
     * Returns a map of all aliases loaded by the config.
     */
    public static final Map<String, String> getAliases() {
        return aliases;
    }

    /**
     * This loads the contents of a configuration file into a map object.
     * 
     * @param filePath  - for the config file
     * @param configMap - initialized config map
     * @throws IOException
     */
    private static final void loadConfigFile(String filePath, Map<String, List<String>> configMap) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;

        while ((line = reader.readLine()) != null) {
            if (line.length() > 0) {
                switch (line.charAt(0)) {
                    // things we should ignore
                    case '#':
                    case ' ':
                        continue;
                    default: {
                        // NOTE: This assumes config has format of '<KEY> <VALUE>'
                        // where value can be a list of values separated by spaces.
                        String[] args = line.split("\\s+", 2);
                        configMap.putIfAbsent(args[0], new ArrayList<String>());
                        for (int i = 1; i < args.length; i++) {
                            configMap.get(args[0]).add(args[i]);
                        }
                    }
                }
            }
        }

        reader.close();
    }
}