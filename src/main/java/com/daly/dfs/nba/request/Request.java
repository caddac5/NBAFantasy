package com.daly.dfs.nba.request;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Request {
    public String sendGet(String url, String date) throws Exception {
        System.out.println("Sending get request...");
        URL obj = new URL(url);
        try{
            // test
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            System.out.println("Connection opened...");
            System.out.println("Sending 'GET' request to URL : " + url);
            // optional default is GET
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            con.setReadTimeout(30000);
            con.setRequestProperty("Referer", "http://stats.nba.com/scores/"+date);
            System.out.println("Referer is http://stats.nba.com/scores/"+date);

            //System.out.println("Response Code: " + con.getResponseCode());

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        }
        catch (java.net.SocketTimeoutException e) {
            e.printStackTrace();
            throw new RuntimeException("Connection Timed Out");
        }
        catch (java.io.IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }


        return "ERROR";
    }
}
