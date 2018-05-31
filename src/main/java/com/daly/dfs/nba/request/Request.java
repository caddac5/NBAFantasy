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
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            System.out.println("Connection opened...");
            System.out.println("Sending 'GET' request to URL : " + url);
            // optional default is GET
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            con.setReadTimeout(30000);
            con.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36");
            con.setRequestProperty("Referer", "http://stats.nba.com/scores/");
            con.setRequestProperty("Accept-Language", "en");

            System.out.println("Response Code: " + con.getResponseCode());

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
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
