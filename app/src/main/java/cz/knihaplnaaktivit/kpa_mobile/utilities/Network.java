package cz.knihaplnaaktivit.kpa_mobile.utilities;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

public class Network {

    private static final String sBaseUrl = "http://knihaplnaaktivit.cz/api/";

    private static final int TIMEOUT = 1000 * 60 * 5; // 5 min

    public static String doGet(String context, Map<String, String> params) throws IOException {
        String paramsBuilded = buildParameters(params);
        if(!TextUtils.isEmpty(paramsBuilded)) {
            paramsBuilded = "?" + paramsBuilded;
        }
        URL obj = new URL(sBaseUrl + context + paramsBuilded);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setReadTimeout(TIMEOUT);

        // int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    /**
     *
     */
    public static String doPost(String context, Map<String, String>  params) throws IOException {
        URL obj = new URL(sBaseUrl + context);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setReadTimeout(TIMEOUT);

        String urlParameters = buildParameters(params);

        // Send post request
        con.setDoOutput(true);
        OutputStream cos = con.getOutputStream();
        DataOutputStream wr = new DataOutputStream(cos);
        wr.writeBytes(urlParameters);
        int totalBytes = wr.size();
        wr.flush();
        wr.close();
        cos.close();



        int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    /**
     * Creates p1=val1&p2=val2 like string
     */
    private static String buildParameters(Map<String, String>  params) {
        StringBuilder sb = new StringBuilder();
        if(params != null) {
            Set<String> keys = params.keySet();
            boolean first = true;
            for (String key : keys) {
                try {
                    if (!first) {
                        sb.append("&");
                    }
                    sb.append(key).append("=").append(URLEncoder.encode(params.get(key), "UTF-8"));
                    first = false;
                } catch (UnsupportedEncodingException e) {}
            }
        }
        return sb.toString();
    }
}
