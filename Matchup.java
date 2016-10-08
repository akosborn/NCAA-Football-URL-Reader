package osborn.andrew.NCAAFootballURLGRabber;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Matchup
{
    private String gameURL;
    Map<String, Date> hashMap = new LinkedHashMap<>();

    public Matchup()
    {
        /**
         * URL-reading: http://www.oracle.com/technetwork/articles/java/json-1973242.html
         * Chaining: http://howtodoinjava.com/core-java/io/how-to-read-data-from-inputstream-into-string-in-java/
         * Parsing: https://examples.javacodegeeks.com/core-java/json/java-json-parser-example/
         */

        try
        {
            URL url = new URL("http://data.ncaa.com/sites/default/files/data/" +
                    "scoreboard/football/fbs/2016/06/scoreboard.json");
            InputStream in = url.openStream();
            InputStreamReader isReader = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isReader);
            JsonParser jsonParser = new JsonParser();

            JsonObject json = jsonParser.parse(br).getAsJsonObject();

            int numGameDays = json.getAsJsonObject().getAsJsonArray("scoreboard")
                    .size();
            for (int i = 0; i < numGameDays; i++)
            {
                JsonObject game = (JsonObject) json.getAsJsonObject().getAsJsonArray("scoreboard").get(i);

                int numGames = game.getAsJsonObject().getAsJsonArray("games").size();
                String rawDate = json.getAsJsonObject().getAsJsonArray("scoreboard")
                        .get(i).getAsJsonObject().get("day").getAsString();

                /** Parse and format date from "dayOfWeek, month dayOfMonth, year" to
                 * "yyyy-MM-dd"
                 */
                String[] dateTokens = rawDate.split(", ");
                String monthAndDay = dateTokens[1];

                String [] monthTokens = monthAndDay.split("   ");
                String monthString = monthTokens[0];
                String monthAbbr = org.apache.commons.lang3.StringUtils.
                        substring(monthString, 0, 3);
                DateTimeFormatter formatMonth = DateTimeFormat.forPattern("MMM");
                DateTime dateTime = formatMonth.parseDateTime(monthAbbr);
                DecimalFormat twoDigits = new DecimalFormat("00");
                String month = twoDigits.format(dateTime.getMonthOfYear());
                String dayOfMonth = monthTokens [1];
                String year = dateTokens[2];

                String dateString = year + "-" + month + "-" + dayOfMonth;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = simpleDateFormat.parse(dateString);

                for (int j = 0; j < numGames; j++)
                {
                    gameURL = "data.ncaa.com" + game.getAsJsonArray("games").get(j).getAsString();

                    hashMap.put(gameURL, date);
                }
            }

            Database.insertToMySQL(hashMap);
        }
        catch (Exception ex)
            {
                ex.printStackTrace();
            }

            // System.out.print(teamHome + " : " + scoreTeamH);
    }
}
