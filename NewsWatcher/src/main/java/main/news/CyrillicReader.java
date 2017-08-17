package main.news;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by JustMe on 7/31/2017.
 */
public class CyrillicReader {

    public static final String ENCODING = "Windows-1251";

    public static  String read(String path){
        return read(new File(path).toPath());
    }
        public static  String read(Path path){
        Charset charset= Charset.availableCharsets().get(ENCODING);
        if (charset==null )
            charset= Charset.defaultCharset();
        try {
            String result = new String(Files.readAllBytes(path), charset);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }

            return null;
        }
}
