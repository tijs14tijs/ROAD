package road.movementparser.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Authentication
{
    private Properties props;

    public Authentication(String authFilePath)
    {
        props = new Properties();
        URL url = this.getClass().getResource(authFilePath);
        System.out.println("url: " + url.getPath());

        try
        {
            props.load(new FileInputStream(new File(url.getFile())));
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(Authentication.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(Authentication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean checkApiKey(String key)
    {
        String real_api_key = props.getProperty("api_key");
        return key != null && (key.equals(real_api_key));
    }
}
