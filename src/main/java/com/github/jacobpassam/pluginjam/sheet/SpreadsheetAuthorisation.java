package com.github.jacobpassam.pluginjam.sheet;

import com.github.jacobpassam.pluginjam.file.JsonConfigurationFile;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import lombok.SneakyThrows;

import java.io.*;
import java.util.Collections;
import java.util.List;

public class SpreadsheetAuthorisation {

    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);

    private static final String CREDENTIALS_FILE_NAME = "credentials.json";

    public SpreadsheetAuthorisation() {
        // Ensure file is there
        JsonConfigurationFile credentials = new JsonConfigurationFile("credentials.json");
        credentials.load();
    }

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     */
    @SneakyThrows
    public Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) {
        // Load client secrets.
        InputStream in = new FileInputStream(new File(JsonConfigurationFile.FILE_DIRECTORY, CREDENTIALS_FILE_NAME));

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(SpreadsheetManager.JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, SpreadsheetManager.JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

}
