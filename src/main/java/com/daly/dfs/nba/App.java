package com.daly.dfs.nba;

import com.daly.dfs.nba.request.Request;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class App
{
    private JFrame mainFrame;
    private JLabel headerLabel;
    private JLabel statusLabel;
    private JPanel controlPanel;
    Request request = new Request();
    final static App app = new App();


    public App() {
        prepareGUI();
    }
    public static void main( String[] args ) {

        app.displayTable(new Date());

    }

    private void displayTable(Date date) {
        headerLabel.setText("Enter a date to find all players' stats for the day.");

        UtilDateModel model = new UtilDateModel();
        model.setValue(new Date());
        final JDatePanelImpl datePanel = new JDatePanelImpl(model);
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel);

        JButton okButton = new JButton("Submit");

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText("");
                getBoxScores((Date)datePanel.getModel().getValue());
                statusLabel.setText("Spreadsheet Generated.");
            }
        });

        controlPanel.add(datePicker);
        controlPanel.add(okButton);

        mainFrame.setVisible(true);
        System.out.println("App started");

    }

    /*
    private ArrayList<JSONArray> getSeasonStats() {
        String url = "http://stats.nba.com/stats/leaguedashplayerstats?College=&Conference=&Country=&DateFrom=&DateTo=&Division=&DraftPick=&DraftYear=&GameScope=&GameSegment=&Height=&LastNGames=0&LeagueID=00&Location=&MeasureType=Base&Month=0&OpponentTeamID=0&Outcome=&PORound=0&PaceAdjust=N&PerMode=PerGame&Period=0&PlayerExperience=&PlayerPosition=&PlusMinus=N&Rank=N&Season=2015-16&SeasonSegment=&SeasonType=Regular+Season&ShotClockRange=&StarterBench=&TeamID=0&VsConference=&VsDivision=&Weight=";
        String data = null;
        try {
            data = request.sendGet(url);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JSONObject obj = new JSONObject(data);
        JSONArray headers = obj.getJSONArray("resultSets").getJSONObject(0).getJSONArray("headers");
        JSONArray gameLog = obj.getJSONArray("resultSets").getJSONObject(0).getJSONArray("rowSet");
        ArrayList<JSONArray> data2 = new ArrayList<JSONArray>();
        data2.add(headers);
        data2.add(gameLog);
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("NBASeasonStats.xls", "UTF-8");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (int x=0; x<headers.length(); x++){
            writer.print(headers.get(x).toString() + '\t');
        }
        writer.print('\n');
        for (int x=0; x<gameLog.length(); x++){
            for (int y=0; y<gameLog.getJSONArray(x).length(); y++){
                writer.print(gameLog.getJSONArray(x).get(y).toString() + '\t');
            }
            writer.print('\n');
        }
        writer.close();
        return data2;
    }
    */

    private void getBoxScores(Date date) {
        System.out.println("Getting box scores...");
        ArrayList gameIds = getGames(date);
        SimpleDateFormat sdf = new SimpleDateFormat("M/dd/yyyy");
        String date2 = sdf.format(date).replaceAll("/","%2F");
        ArrayList<JSONArray> players = new ArrayList<JSONArray>();
        String[] headers = {"Name","Team","Min","FG","3P","FT","PTS","RB","AST","STL","BLK","TO","PF","FD","DK"};
        for (int x=0; x<gameIds.size(); x++) {
            String url = "http://stats.nba.com/stats/boxscoretraditionalv2?EndPeriod=10&EndRange=28800&GameID="+gameIds.get(x)+"&RangeType=0&Season=2017-18&SeasonType=Regular+Season&StartPeriod=1&StartRange=0";
            String data = null;
            try {
                data = request.sendGet(url, date2);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            JSONObject obj = new JSONObject(data);
            JSONArray gamePlayers = obj.getJSONArray("resultSets").getJSONObject(0).getJSONArray("rowSet");
            for (int y=0; y<gamePlayers.length(); y++){
                if (String.valueOf(gamePlayers.getJSONArray(y).get(8)) != "null") {
                    players.add(gamePlayers.getJSONArray(y));
                }
            }
        }
        String[][] playersSelect = new String[players.size()][15];
        for (int x=0; x<players.size(); x++){
            JSONArray plyr = players.get(x);
            playersSelect[x][0] = String.valueOf(plyr.get(5));
            playersSelect[x][1] = String.valueOf(plyr.get(2));
            playersSelect[x][2] = getMinutes(String.valueOf(plyr.get(8)));
            playersSelect[x][3] = String.valueOf(plyr.get(9))+" for "+String.valueOf(plyr.get(10));
            playersSelect[x][4] = String.valueOf(plyr.get(12))+" for "+String.valueOf(plyr.get(13));
            playersSelect[x][5] = String.valueOf(plyr.get(15))+" for "+String.valueOf(plyr.get(16));
            playersSelect[x][6] = String.valueOf(plyr.get(26));
            playersSelect[x][7] = String.valueOf(plyr.get(20));
            playersSelect[x][8] = String.valueOf(plyr.get(21));
            playersSelect[x][9] = String.valueOf(plyr.get(22));
            playersSelect[x][10] = String.valueOf(plyr.get(23));
            playersSelect[x][11] = String.valueOf(plyr.get(24));
            playersSelect[x][12] = String.valueOf(plyr.get(25));
            playersSelect[x][13] = getFDPoints(plyr).toString();
            playersSelect[x][14] = getDKPoints(plyr).toString();
        }
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("NBABoxScores_"+date2+".xls", "UTF-8");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (int x=0; x<headers.length; x++){
            writer.print(headers[x].toString() + '\t');
        }
        writer.print('\n');
        for (int x=0; x<playersSelect.length; x++){
            for (int y=0; y<playersSelect[x].length; y++){
                writer.print(playersSelect[x][y] + '\t');
            }
            writer.print('\n');
        }
        writer.close();
    }

    private ArrayList getGames(Date date2) {
        System.out.println("Getting games...");
        SimpleDateFormat sdf = new SimpleDateFormat("M/dd/yyyy");
        String date = sdf.format(date2).replaceAll("/","%2F");
        String url = "http://stats.nba.com/stats/scoreboardV2?DayOffset=0&LeagueID=00&gameDate="+date;
        String data = null;
        try {
            data = request.sendGet(url, date);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JSONObject obj = new JSONObject(data);
        JSONArray games = obj.getJSONArray("resultSets").getJSONObject(0).getJSONArray("rowSet");
        ArrayList gameIds = new ArrayList();
        for (int x=0; x<games.length(); x++) {
            gameIds.add(games.getJSONArray(x).get(2));
        }
        return gameIds;
    }

    private Double getFDPoints(JSONArray plyr) {
        Double pts = plyr.optDouble(26);
        Double ast = plyr.optDouble(21);
        Double reb = plyr.optDouble(20);
        Double stl = plyr.optDouble(22);
        Double blk = plyr.optDouble(23);
        Double to = plyr.optDouble(24);
        Double fp = pts + (ast*1.5) + (reb*1.2) + (stl*3) + (blk*3) - to;
        return fp;
    }

    private Double getDKPoints(JSONArray plyr) {
        int d = 0;
        Double pts = plyr.optDouble(26);
        if (pts>=10) d++;
        Double tpm = plyr.optDouble(12);
        Double ast = plyr.optDouble(21);
        if (ast>=10) d++;
        Double reb = plyr.optDouble(20);
        if (reb>=10) d++;
        Double stl = plyr.optDouble(22);
        if (stl>=10) d++;
        Double blk = plyr.optDouble(23);
        if (blk>=10) d++;
        Double to = plyr.optDouble(24);
        Double dk = pts + (ast*1.5) + (tpm*.5) + (reb*1.25) + (stl*2) + (blk*2) - (to*.5);
        if (d>=2) dk+=1.5;
        if (d>=3) dk+=3;

        return dk;
    }

    private String getMinutes(String minutes) {
        String[] mins = minutes.split(":");
        if (mins.length > 1 && Integer.parseInt(mins[1]) >= 30) {
            return Integer.toString(Integer.parseInt(mins[0]) + 1);
        }
        else {
            return mins[0];
        }
    }

    private void prepareGUI(){
        mainFrame = new JFrame("Fantasy Points");
        mainFrame.setSize(400,400);
        mainFrame.setLayout(new GridLayout(3, 1));
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                System.exit(0);
            }
        });
        headerLabel = new JLabel("", JLabel.CENTER);
        statusLabel = new JLabel("",JLabel.CENTER);

        statusLabel.setSize(350,100);

        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        mainFrame.add(headerLabel);
        mainFrame.add(controlPanel);
        mainFrame.add(statusLabel);
        mainFrame.setVisible(true);
    }
}
