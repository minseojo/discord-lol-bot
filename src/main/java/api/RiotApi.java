package api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class RiotApi {


    public JSONObject getRiotApiJsonObject(URL url) throws IOException {
        // 라이엇 api 링크에 연결하기
        ApiConnection apiConnection = new ApiConnection(url);
        JSONObject obj = null;

        try {
            // 응답코드 200이 아닐 경우 리턴
            if(apiConnection.getResponseCode() != 200) return null;

            // 서버로부터 데이터 읽어오기
            StringBuilder sb = apiConnection.moveToDataBuffer();

            obj = new JSONObject(sb.toString()); // json으로 변경 (역직렬화)
            return obj;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }


    public JSONArray getRiotApiJsonArray(URL url) throws IOException {
        ApiConnection apiConnection = new ApiConnection(url);
        JSONArray obj = null;
        try {
            // 응답코드 200이 아닐 경우 리턴
            if(apiConnection.getResponseCode() != 200) return null;

            // 서버로부터 데이터 읽어오기
            StringBuilder sb = apiConnection.moveToDataBuffer();

            obj = new JSONArray(sb.toString()); // json으로 변경 (역직렬화)
            return obj;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

}
