package net.anvilcraft.anvillib.api.serialize;

import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

@FunctionalInterface
public interface ISerializedRead {
    
    default Reader read() throws IOException {
        CharArrayWriter writer = new CharArrayWriter();
        write(writer);
        return new CharArrayReader(writer.toCharArray());
    }

    void write(Writer writer) throws IOException;

    public static ISerializedRead fromReader(Reader reader) {
        return new ISerializedRead() {

            @Override
            public Reader read() throws IOException {
                return reader;
            }

            @Override
            public void write(Writer writer) throws IOException {
                char[] buf = new char[32];
                while (reader.read(buf) >= 0) {
                    writer.write(buf);
                }
                reader.close();
                writer.close();
            }
            
        };
    }

}
