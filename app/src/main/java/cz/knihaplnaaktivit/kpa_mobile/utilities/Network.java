package cz.knihaplnaaktivit.kpa_mobile.utilities;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;

public class Network {

    private static final String sBaseUrl = "http://knihaplnaaktivit.cz/api/";

    public static String doGet(String context, Map<String, String> params) throws IOException {
        String paramsBuilded = buildParameters(params);
        if(!TextUtils.isEmpty(paramsBuilded)) {
            paramsBuilded = "?" + paramsBuilded;
        }
        URL obj = new URL(sBaseUrl + context + paramsBuilded);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");

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

    public static String doPost(String context, Map<String, String>  params) throws IOException {
        URL obj = new URL(sBaseUrl + context);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");

        String urlParameters = buildParameters(params);

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

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

    private static String buildParameters(Map<String, String>  params) {
        StringBuilder sb = new StringBuilder();
        if(params != null) {
            Set<String> keys = params.keySet();
            boolean first = true;
            for (String key : keys) {
                if (!first) {
                    sb.append("&");
                }
                sb.append(key).append("=").append(params.get(key));
                first = false;
            }
        }
        return sb.toString();
    }
}