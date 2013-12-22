package st.redline.classloader;

import java.io.*;

public class FileSourceReader implements SourceReader {

    private final File file;

    public FileSourceReader(File file) {
        this.file = file;
    }

    public String contents(LineTransformer lineTransformer) {
        FileInputStream fis = null;
        String newLine = System.getProperty("line.separator");
        StringBuilder contents = new StringBuilder();
        try {
            fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            line = lineTransformer.begin();
            if (line != null)
                contents.append(line);
            while ((line = reader.readLine()) != null) {
                line = lineTransformer.transform(line);
                contents.append(line).append(newLine);
            }
            line = lineTransformer.end();
            if (line != null)
                contents.append(line);
            return contents.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            close(fis);
        }
    }

    private void close(InputStream inputStream) {
        if (inputStream != null)
            try {
                inputStream.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
    }
}
