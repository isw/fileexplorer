package de.innuendo.fileexplorer.services.fs.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class LineReader implements AutoCloseable, ILineReader {
  
  private Path input;
  private PushbackInputStream stream;
  
  public LineReader (Path fl) throws IOException {
    this.input = fl;
    this.open ();
  }
  
  public void open () throws IOException {
    this.stream = new PushbackInputStream(
        new BufferedInputStream(Files.newInputStream(this.input, StandardOpenOption.READ)));
  }
    
  public String readLine () throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    int read = this.stream.read();
    if (read == -1) return null;
    while (read >= 0) {
      if (read == '\n')
        break;
      else if (read == '\r') {        
        int next = this.stream.read();
        if (next == '\n') {
          break;
        }
        if (next != -1) this.stream.unread(next);
      }
      else {
        bos.write(read);
      }
      read = this.stream.read();
    }
    return new String(bos.toByteArray(), "utf-8");
  }

  public void close () throws IOException {
    this.stream.close();
  }
}
