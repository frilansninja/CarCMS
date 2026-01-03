package se.meltastudio.cms.integration.carinfo.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JsoupHtmlFetcher {

    /**
     * Fetches HTML document from the given URL.
     * Uses realistic Chrome browser headers to avoid being blocked.
     *
     * @param url the URL to fetch
     * @param timeoutMillis timeout in milliseconds
     * @param userAgent user agent string (typically Chrome UA)
     * @return parsed HTML document
     * @throws IOException if fetch fails
     */
    public Document fetch(String url, int timeoutMillis, String userAgent) throws IOException {
        return Jsoup.connect(url)
                .userAgent(userAgent)
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "sv-SE,sv;q=0.9,en-US;q=0.8,en;q=0.7")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Connection", "keep-alive")
                .header("Upgrade-Insecure-Requests", "1")
                .header("Sec-Fetch-Dest", "document")
                .header("Sec-Fetch-Mode", "navigate")
                .header("Sec-Fetch-Site", "none")
                .header("Sec-Fetch-User", "?1")
                .timeout(timeoutMillis)
                .followRedirects(true)
                .get();
    }
}
