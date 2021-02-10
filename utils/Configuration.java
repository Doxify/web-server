package utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Properties;
import java.util.Map.Entry;

public class Configuration {

    public static final DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");

    private static Properties httpd;
    private static Properties mime;
    private static HashMap<String, String> mimeTypes;

    public Configuration() throws IOException {
        // Load both the httpd.conf and mime.types files.
        httpd = loadConfiguration("./conf/httpd.conf");
        mime = loadConfiguration("./conf/mime.types");

        // Generate the mime types map.
        mimeTypes = new HashMap<String, String>();
        for (Entry<Object, Object> entry : mime.entrySet()) {
            for (String extension: entry.getValue().toString().split(" "))
                mimeTypes.put(extension, entry.getKey().toString());
        }
    }

    private Properties loadConfiguration(String filePath) throws IOException {
        Properties properties = new Properties();
        BufferedInputStream stream = new BufferedInputStream(new FileInputStream(filePath));
        
        properties.load(stream);
        stream.close();
        
        return properties;
    }

    public static Properties getHttpd() {
        return httpd;
    }

    public static Properties getMime() {
        return mime;
    }

    public static String getMimeType(String extension) {
        return mimeTypes.get(extension);
    }
}
