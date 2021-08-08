package com.github.jacobpassam.pluginjam.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.*;

public class JsonConfigurationFile {

    public static final String FILE_DIRECTORY = "data";

    public static final Gson GSON = new GsonBuilder().create();

    private final String name;

    @Getter
    private JsonObject data;

    public JsonConfigurationFile(String name) {
        this.name = name;
    }

    @SneakyThrows
    public void load() {
        File dir = new File(FILE_DIRECTORY);
        if (!dir.exists()) dir.mkdirs();

        File f = new File(FILE_DIRECTORY, name);
        if (!f.exists()) {
            InputStream stream = getClass().getResourceAsStream("/" + name);

            FileUtils.copyInputStreamToFile(stream, f);
        }

        this.data = JsonParser.parseReader(new FileReader(f)).getAsJsonObject();
    }

    @SneakyThrows
    public void save() {
        if (data == null) {
            return;
        }

        File f = new File(FILE_DIRECTORY, name);

        try (FileWriter fw = new FileWriter(f)) {
            fw.write(GSON.toJson(data));
        }
    }
}
