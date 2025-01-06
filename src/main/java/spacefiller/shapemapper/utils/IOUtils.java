package spacefiller.shapemapper.utils;

import spacefiller.shapemapper.ShapeMapper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Stream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class IOUtils {
  public static String[] getFileContents(String filename) {
    try {
      InputStream resourceAsStream = ShapeMapper.class.getResourceAsStream("/model.frag.glsl");
      BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream));
      Stream<String> lineStream = reader.lines();
      List<String> lines = lineStream.toList();
      return lines.toArray(new String[] {});
    } catch (Exception e) {
      e.printStackTrace();
      return new String[] {};
    }
  }

  private static String getFileExtension(String name) {
    int lastIndexOf = name.lastIndexOf(".");
    if (lastIndexOf == -1) {
      return ""; // empty extension
    }
    return name.substring(lastIndexOf);
  }

  public static String extractResourceToFile(String resourcePath) {
    try {
      InputStream resourceAsStream = ShapeMapper.class.getResourceAsStream(resourcePath);
      if (resourceAsStream == null) {
        throw new IllegalArgumentException("Resource not found: " + resourcePath);
      }
      String extension = getFileExtension(resourcePath);
      File tempFile = Files.createTempFile(null, extension).toFile();
      tempFile.deleteOnExit();

      try (FileOutputStream out = new FileOutputStream(tempFile)) {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = resourceAsStream.read(buffer)) != -1) {
          out.write(buffer, 0, bytesRead);
        }
      }

      return tempFile.getAbsolutePath();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}
