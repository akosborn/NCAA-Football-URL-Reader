package osborn.andrew.NCAAFootballURLGRabber;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.Map;

public class Database
{
    public static void insertToMySQL(Map<String, Date> hashMap)
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection
                    ("jdbc:mysql://localhost:3306/NCAAFootball?autoReconnect=true&useSSL=false", "root", "Osbo_789");

            String query = "INSERT INTO schedule (date, URL) VALUES " +
                    "(?, ?)";

            for (Map.Entry<String, Date> entry : hashMap.entrySet())
            {
                String gameURL = entry.getKey();
                Date javaDate = entry.getValue();
                java.sql.Date gameDate = new java.sql.Date(javaDate.getTime());

                PreparedStatement statement = conn.prepareStatement(query);
                statement.setDate(1, gameDate);
                statement.setString(2, gameURL);
                statement.execute();
            }
            conn.close();
        }
        catch (Exception ex) { ex.printStackTrace(); }
    }
}
