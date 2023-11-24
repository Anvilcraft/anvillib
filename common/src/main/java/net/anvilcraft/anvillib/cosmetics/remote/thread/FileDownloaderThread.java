package net.anvilcraft.anvillib.cosmetics.remote.thread;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import net.anvilcraft.anvillib.AnvilLib;

public abstract class FileDownloaderThread implements Runnable {
    protected Gson gson = new GsonBuilder().create();
    protected HttpClient client = HttpClient.newBuilder().build();
    protected final String version;

    public FileDownloaderThread(String version) {
        this.version = version;
    }

    protected HttpRequest buildRequest(URI url) {
        return HttpRequest.newBuilder()
            .GET()
            .uri(url)
            .header("User-Agent", System.getProperty("java.version"))
            .header("X-AnvilLib-Version", this.version)
            .header("X-Minecraft-Version", "1.18.2")
            .build();
    }

    public InputStream getStreamForURL(URI url) {
        HttpRequest req = this.buildRequest(url);
        InputStream is = null;
        try {
            HttpResponse<InputStream> res
                = client.send(req, HttpResponse.BodyHandlers.ofInputStream());
            if (res.statusCode() == 200) {
                is = res.body();
            } else if (res.statusCode() != 404) {
                AnvilLib.LOGGER.error("Unexpected status code: {}", res.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            AnvilLib.LOGGER.error(e);
        }
        return is;
    }

    public String getStringForURL(URI url) {
        HttpRequest req = this.buildRequest(url);
        String is = null;
        try {
            HttpResponse<String> res
                = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() == 200) {
                is = res.body();
            } else if (res.statusCode() != 404) {
                AnvilLib.LOGGER.error("Unexpected status code: {}", res.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            AnvilLib.LOGGER.error(e);
        }
        return is;
    }

    public <T> T loadJson(URI url, Class<T> type) throws IOException {
        InputStream stream = this.getStreamForURL(url);
        if (stream == null)
            return null;
        try {
            T json = this.gson.fromJson(new InputStreamReader(stream), type);
            return json;
        } catch (JsonSyntaxException | JsonIOException e) {
            throw new IOException(e);
        } finally {
            stream.close();
        }
    }
}
