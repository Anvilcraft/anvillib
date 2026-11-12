package net.anvilcraft.anvillib;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import net.anvilcraft.anvillib.api.serialize.IObjectSerializer;
import net.anvilcraft.anvillib.api.serialize.ISerializedRead;
import net.anvilcraft.anvillib.earlyload.EarlyLoadEvent;
import net.anvilcraft.anvillib.earlyload.IEarlyLoadService;
import net.anvilcraft.anvillib.event.Bus;
import net.anvilcraft.anvillib.event.IEventHandler;
import net.anvilcraft.anvillib.impl.serialize.CombinedSerializer;
import net.anvilcraft.anvillib.impl.serialize.FileResponseSerializer;
import net.anvilcraft.anvillib.impl.serialize.GsonSerializer;

public class MixinPreloader implements IEarlyLoadService {

    public static final String FILE_HASH = "ad0ea4f92daf7bf7ec5c10e16258425e47929126954aea5d80df69ce26cd7318";

    @Override
    public void registerEventHandlers(Bus bus) {
        bus.register(EarlyLoadEvent.class, new EarlyLoadHandler());
    }

    static class EarlyLoadHandler implements IEventHandler<EarlyLoadEvent> {

        @Override
        public int order() {
            return Integer.MIN_VALUE;
        }

        @Override
        public void accept(EarlyLoadEvent ev) {
            if (!isMixinLoaded()) {
                System.out.println("Mixins are missing! Downloading Unimixins");
                try {
                    downloadMod(ev.gameDir.toPath().resolve("mods"), FILE_HASH);
                } catch (NoSuchAlgorithmException | IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Unimixins successfully installed! Please restart the game");
                ev.stopGame.run();
            }
        }

        public boolean isMixinLoaded() {
            return Thread.currentThread().getContextClassLoader().getResource("io/github/tox1cozz/mixinbooterlegacy/IEarlyMixinLoader.class") != null;
        }

        private Path downloadMod(Path modsDir, String fileHash) throws IOException, NoSuchAlgorithmException {
            URL url = this.getDownloadUrl(fileHash);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "curl");
            String path = url.getPath();
            String fileName = path.substring(path.lastIndexOf('/') + 1);
            Path targetFile = modsDir.resolve(fileName);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            try (InputStream in = connection.getInputStream();
                DigestInputStream dis = new DigestInputStream(in, digest)) {
                Files.copy(dis, targetFile, StandardCopyOption.REPLACE_EXISTING);
            } finally {
                connection.disconnect();
            }

            byte[] hashBytes = digest.digest();
            StringBuilder hexBuilder = new StringBuilder(hashBytes.length * 2);
            for (byte b : hashBytes) {
                hexBuilder.append(String.format("%02x", b));
            }
            String actualHash = hexBuilder.toString();

            if (!actualHash.toString().equals(fileHash)) {
                targetFile.toFile().delete();
                throw new IOException("File checksum mismatch, deleting downloaded file. Expected: " + fileHash + " Got: " + actualHash);
            }

            return targetFile;
        }

        public URL getDownloadUrl(String hash) throws IOException {
            URL requestUrl = new URL("https://api.tilera.xyz/anvillib/files/" + hash);
            HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestProperty("User-Agent", "curl");
            connection.setRequestProperty("X-Minecraft-Version", "1.7.10");
            connection.setRequestProperty("X-AnvilLib-Version", Constants.VERSION);
            if (connection.getResponseCode() == 200) {
                InputStream stream = connection.getInputStream();
                IObjectSerializer<ISerializedRead, List<URL>> serializer = new CombinedSerializer<>(GsonSerializer.RW, FileResponseSerializer.INSTANCE);
                return serializer.deserialize(ISerializedRead.fromReader(new InputStreamReader(stream)))
                    .map(l -> l.get(0))
                    .mapError(IOException::new)
                    .getOrThrow();
            }
            throw new IOException("Response code: "+ connection.getResponseCode());
        }

    }
    
}
