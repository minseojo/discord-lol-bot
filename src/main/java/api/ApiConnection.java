package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiConnection {
    HttpURLConnection conn = null;
    ApiConnection(URL url) throws IOException {
        this.conn = requestConnection(url);
    }

    public HttpURLConnection requestConnection(URL url) throws IOException {
        this.conn = (HttpURLConnection) url.openConnection();
        this.conn.setRequestMethod("GET");
        this.conn.setRequestProperty("Content-Type", "application/json");
        this.conn.setRequestProperty("Transfer-Encoding", "chunked");
        this.conn.setRequestProperty("Connection", "keep-alive");
        this.conn.setDoOutput(true);

        return this.conn;
    }

    public int getResponseCode() throws IOException {
        return this.conn.getResponseCode();
    }

    public StringBuilder moveToDataBuffer() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(this.conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line = null;

        while((line = br.readLine()) != null) { // 읽을 수 있을 때 까지 반복
            sb.append(line);
        }

        return sb;
    }
}
