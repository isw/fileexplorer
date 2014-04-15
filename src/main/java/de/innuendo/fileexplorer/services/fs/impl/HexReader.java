package de.innuendo.fileexplorer.services.fs.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class HexReader implements AutoCloseable, ILineReader {
  private static final int LEN = 64; // Zeilenlaenge
  
  private Path input;
  private InputStream stream;
  private byte[] buffer;
  
  public HexReader (Path fl) throws IOException {
    this.input = fl;
    this.buffer = new byte[LEN];
    this.open ();
  }
  
  public void open () throws IOException {
    this.stream = new BufferedInputStream(Files.newInputStream(this.input, StandardOpenOption.READ),LEN*1000);
  }
    
  public String readLine () throws IOException {
    int read = this.stream.read(this.buffer);
    if (read < 0) return null;
    StringBuilder bld = new StringBuilder ();
    StringBuilder str = new StringBuilder ();
    for (int i=0; i<read; i++) {
      byte b = this.buffer[i];
      bld.append(String.format("%02x", b));
      if (i%2 != 0) bld.append(" ");
      //if (Character.isLetterOrDigit(b))
      if (Character.isDefined(b) && !Character.isWhitespace(b) && !Character.isISOControl(b))
        str.append(String.format("%c", b));
      else
        str.append("\u25aa");
    }
    if (read < LEN) {
      int fehlend = (LEN>>1)*5 - bld.length();
      
      for (int i=0; i<fehlend; i++) {
        bld.append (" ");
      }
    }
    
    bld.append(" | ").append(str);
    return bld.toString();
  }

  public void close () throws IOException {
    this.stream.close();
  }
}
