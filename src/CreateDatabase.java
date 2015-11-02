import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.Status;
import java.sql.*;
import java.util.List;

public class CreateDatabase
{
	public static void newDatabase() throws SQLException, TwitterException, ClassNotFoundException
	{
		System.out.print("Creating new database... ");
		Class.forName("org.sqlite.JDBC");
		
		Connection connection = DriverManager.getConnection("jdbc:sqlite:database.db");
		Statement statement = connection.createStatement();
		
		Twitter twitter = Operations.login();
		
		statement.executeUpdate("drop table if exists authors");
		statement.executeUpdate("drop table if exists tweets");
		statement.executeUpdate("drop table if exists retweeted");
		
		statement.executeUpdate("create table authors(name varchar(100))");
		statement.executeUpdate("create table tweets(id long, author varchar(100), efficiency integer)");
		statement.executeUpdate("create table retweeted(content varchar(300))");
		statement.executeUpdate("insert into authors values('stoch_kamil')");
		statement.executeUpdate("insert into authors values('HolowczycBlog')");
		statement.executeUpdate("insert into authors values('lewy_official')");
		statement.executeUpdate("insert into authors values('SchumiOfficial')");
		statement.executeUpdate("insert into authors values('MorgensternT')");
		statement.executeUpdate("insert into authors values('Cristiano')");
		
		List<Status> tweets = twitter.getHomeTimeline();
		for (Status tweet : tweets)
			statement.executeUpdate("insert into retweeted values(\"" + tweet.getText() + "\")");
		
		connection.close();
		statement.close();
		
		System.out.print("OK\n");
	}
}
