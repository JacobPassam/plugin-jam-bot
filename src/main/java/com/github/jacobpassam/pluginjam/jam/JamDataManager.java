package com.github.jacobpassam.pluginjam.jam;

import com.github.jacobpassam.pluginjam.file.JsonConfigurationFile;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

public class JamDataManager {

    private final JsonConfigurationFile entries;

    public JamDataManager() {
        this.entries = new JsonConfigurationFile("entries.json");
    }

    public void load() {
        entries.load();
    }

    public List<JamEntry> getEntries() {
        JsonArray entries = this.entries.getData().getAsJsonArray("entries");

        List<JamEntry> entryList = new ArrayList<>();

        for (JsonElement entry : entries) {
            entryList.add(JsonConfigurationFile.GSON.fromJson(entry, JamEntry.class));
        }

        return entryList;
    }

    public void addEntry(JamEntry entry) {
        entries.getData().getAsJsonArray("entries").add(JsonConfigurationFile.GSON.toJsonTree(entry));
        entries.save();
    }
}
