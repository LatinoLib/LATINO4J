package org.latinolib.stopwords;

import org.latinolib.Language;

import java.io.*;
import java.util.HashSet;

/**
 * Author mIHA
 */
public class DefaultStopWords implements StopWords
{
    private boolean caseSensitive;
    private HashSet<String> stopWords
        = new HashSet<String>();

    public DefaultStopWords(Language language, boolean caseSensitive) throws IOException {
        this.caseSensitive = caseSensitive;
        loadStopWords(language);
    }

    @Override
    public Boolean isStopWord(String word) {
        return stopWords.contains(caseSensitive ? word : word.toLowerCase());
    }

    private void loadStopWords(Language language) throws IOException {
        InputStream ip = getClass().getResourceAsStream("/latino-" + language.toString().toLowerCase() + ".sw");
        BufferedReader br = new BufferedReader(new InputStreamReader(ip));
        String line;
        while ((line = br.readLine()) != null) {
            if (line.contains("#")) {
                line = line.substring(0, line.indexOf('#'));
            }
            line = line.trim();
            if (line.equals("")) { continue; }
            stopWords.add(caseSensitive ? line : line.toLowerCase());
        }
        br.close();
    }
}
