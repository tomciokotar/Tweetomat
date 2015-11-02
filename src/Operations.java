import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterException;
import twitter4j.Status;
import twitter4j.conf.ConfigurationBuilder;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class Operations
{
	private static int min(int a, int b)
	{
		if (a <= b)
			return a;
		return b;
	}
	
	public static Twitter login()
	{
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("s94xKTRD7kVLMHTSXbOg")
		  .setOAuthConsumerSecret("QFxNB2RF25ACqhxewQoe0CzJZeDeNeISQOPU1IGR0k")
		  .setOAuthAccessToken("2361764406-ehLCE28zzMIcQmzmJ08o4cfmE06gALhrCAKbsg6")
		  .setOAuthAccessTokenSecret("BvAV0g6CNQzkSEcrxm97N7i8M0XpFhQKwnvp5mtmhf7fo");
		
		TwitterFactory tf = new TwitterFactory(cb.build());
		return tf.getInstance();
	}
	
	public static void archiveTweets(Twitter twitter, Connection connection, String author)
		throws TwitterException, SQLException
	{
		if (author != null && !isInDatabase(connection, author)) {
			System.out.print("Given author not found.\n");
			return;
		}
		
		Statement statement = connection.createStatement();
		ArrayList<String> authors = getAuthors(connection);
		
		for (String currentAuthor: authors) {
			if (author != null && !currentAuthor.equals(author))
				continue;
			
			System.out.print("Downloading for " + currentAuthor + "... ");
				
				List<Status> tweets = null;
				
				try {
					tweets = twitter.getUserTimeline(currentAuthor);
				} catch (Exception e) {
					System.out.println("tweets couldn't have been downloaded, an author's name might be incorrect.");
					return;
				}
				
				for (Status tweet: tweets) {
					int efficiency = 3 * tweet.getRetweetCount() + 3 * tweet.getFavoriteCount()
						+ tweet.getUser().getFollowersCount();
					
					statement.executeUpdate("insert into tweets values(" + tweet.getId() + ", '"
						+ tweet.getUser().getScreenName() + "', " + efficiency + ")");
				}
				System.out.print("OK\n");
		}
		
		statement.close();
		System.out.print("Tweets have been downloaded.\n");
	}
	
	public static Status giveTheBest(Twitter twitter, Connection connection, String author)
		throws SQLException, TwitterException
	{
		Statement statement = connection.createStatement();
		ResultSet resultSet = null;
		
		if (author == null)
			resultSet = statement.executeQuery("select distinct * from tweets order by efficiency desc");
		else
			resultSet = statement.executeQuery("select distinct * from tweets where author = '" + author
				+ "' order by efficiency desc");
		
		while (resultSet.next()) {
			long id = resultSet.getLong("id");
			Status currentStatus = twitter.showStatus(id);
			Statement currentStatement = connection.createStatement();
			
			ResultSet used = currentStatement.executeQuery("select count(*) as number from retweeted where content like \"%"
				+ currentStatus.getText().substring(0, min(currentStatus.getText().length(), 100)) + "%\"");
			
			if (used.getInt("number") == 0) {
				used.close();
				currentStatement.close();
				resultSet.close();
				statement.close();
				return currentStatus;
			}
			
			used.close();
			currentStatement.close();
		}
		
		resultSet.close();
		statement.close();
		return null;
	}
	
	public static void addAuthor(Connection connection, String author) throws SQLException
	{
		if (isInDatabase(connection, author)) {
			System.out.print("This author has been added before.\n");
			return;
		}
		
		Statement statement = connection.createStatement();
		statement.executeUpdate("insert into authors values('" + author + "')");
		statement.close();
		
		System.out.print("Added author: " + author + "\n");
	}
	
	public static void retweet(Twitter twitter, Connection connection, Status tweet) throws SQLException, TwitterException
	{
		if (tweet == null) {
			System.out.print("There's nothing to retweet.\n");
			return;
		}
		
		Statement statement = connection.createStatement();
		
		twitter.retweetStatus(tweet.getId());
		statement.executeUpdate("insert into retweeted values(\"" + tweet.getText() + "\")");
		
		statement.close();
		System.out.print("Retweet sent.\n");
	}
	
	public static void removeTweets(Connection connection, String author) throws SQLException
	{
		if (author != null && !isInDatabase(connection, author)) {
			System.out.print("Given author not found.\n");
			return;
		}
		
		Statement statement = connection.createStatement();
		
		if (author == null)
			statement.executeUpdate("delete from tweets");
		else
			statement.executeUpdate("delete from tweets where author = '" + author + "'");
		
		statement.close();
		System.out.print("Tweets removed.\n");
	}
	
	public static void removeAuthor(Connection connection, String author) throws SQLException
	{
		if (!isInDatabase(connection, author)) {
			System.out.print("Given author not found.\n");
			return;
		}
		
		removeTweets(connection, author);
		
		Statement statement = connection.createStatement();
		statement.executeUpdate("delete from authors where name = '" + author + "'");
		statement.close();
		
		System.out.print("Removed author: " + author + "\n");
	}
	
	public static ArrayList<String> getAuthors(Connection connection) throws SQLException
	{
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("select * from authors");
		
		ArrayList<String> authors = new ArrayList<String>();
		
		while (resultSet.next())
			authors.add(resultSet.getString("name"));
		
		resultSet.close();
		statement.close();
		return authors;
	}
	
	public static void removeAuthors(Connection connection) throws SQLException
	{
		Statement statement = connection.createStatement();
		statement.executeUpdate("delete from tweets");
		statement.executeUpdate("delete from authors");
		statement.close();
		
		System.out.print("All authors has been removed.\n");
	}
	
	private static boolean isInDatabase(Connection connection, String author) throws SQLException
	{
		Statement statement = connection.createStatement();
		int count = statement.executeQuery("select count(*) as number from authors where name = '" + author + "'").getInt("number");
		statement.close();
		
		return count > 0;
	}
}
