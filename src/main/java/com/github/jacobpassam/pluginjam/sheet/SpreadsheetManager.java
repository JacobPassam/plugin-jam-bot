package com.github.jacobpassam.pluginjam.sheet;

import com.github.jacobpassam.pluginjam.jam.JamEntry;
import com.github.jacobpassam.pluginjam.jam.vote.VoteCategory;
import com.github.jacobpassam.pluginjam.jam.vote.VoteTotals;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

public class SpreadsheetManager {

    public static final String APPLICATION_NAME = "Plugin Jam";
    public static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static final String spreadsheetId = "1UJcOFNIXkJyKwrAeM_AN025h0em1uz5dCAdCUjCAvLA";

    private final SpreadsheetAuthorisation authorisation;

    private Sheets sheetsService;

    public SpreadsheetManager() {
        this.authorisation = new SpreadsheetAuthorisation();
    }

    @SneakyThrows
    public void load() {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        this.sheetsService = new Sheets.Builder(HTTP_TRANSPORT, SpreadsheetManager.JSON_FACTORY, authorisation.getCredentials(HTTP_TRANSPORT))
                .setApplicationName(SpreadsheetManager.APPLICATION_NAME)
                .build();
    }

    @SneakyThrows
    public List<JamEntry> getEntries(JDA jda) {
        final String RANGE = "Sheet1!C11:J55";

        ValueRange response = sheetsService.spreadsheets().values()
                .get(spreadsheetId, RANGE)
                .execute();

        List<JamEntry> entryList = new ArrayList<>();

        List<List<Object>> values = response.getValues();
        for (int i = 0; i < values.size(); i++) {
            if (i % 3 != 0) continue;

            List<Object> s = values.get(i);
            for (int j = 0; j < s.size(); j++) {
                if (!s.remove("")) break;
            }

            long devId = Long.parseLong((String) s.get(1));
            String title = (String) s.get(2);

            entryList.add(new JamEntry(devId, title));
        }

        return entryList;
    }

    public void addVotes(int entryIndex, Map<VoteCategory, VoteTotals> totals) {
        // Begin at index 0
        int row = 11 + (3 * entryIndex);

        VoteTotals creativity = totals.get(VoteCategory.CREATIVITY);
        VoteTotals functionality = totals.get(VoteCategory.FUNCTIONALITY);
        VoteTotals aesthetics = totals.get(VoteCategory.AESTHETICS);

        ForkJoinPool.commonPool().execute(() -> {
            try {
                sheetsService.spreadsheets().values().update(spreadsheetId, "L" + row + ":Q" + row,
                        new ValueRange()
                                .setValues(Arrays.asList(Arrays.asList(creativity.getTotalAverage(), creativity.getSpecialistAverage(),
                                        functionality.getTotalAverage(), functionality.getSpecialistAverage(),
                                        aesthetics.getTotalAverage(), aesthetics.getSpecialistAverage())))).setValueInputOption("USER_ENTERED").execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

}
