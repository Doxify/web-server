package utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {

    private Properties httpd;
    private Properties mime;

    public Configuration() throws IOException {
        httpd = loadConfiguration("./conf/httpd.conf");
        mime = loadConfiguration("./conf/mime.types");
    }

    private Properties loadConfiguration(String filePath) throws IOException {
        Properties properties = new Properties();
        BufferedInputStream stream = new BufferedInputStream(new FileInputStream(filePath));
        
        properties.load(stream);
        stream.close();
        
        return properties;
    }

    public Properties getHttpd() {
        return httpd;
    }

    public Properties getMime() {
        return mime;
    }
}
