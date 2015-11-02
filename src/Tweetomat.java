import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.Status;
import java.sql.*;
import java.util.ArrayList;

public class Tweetomat
{
	public static void main(String[] args) throws SQLException, TwitterException, ClassNotFoundException
	{
		Connection connection = null;
		Statement statement = null;
		Twitter twitter = null;
		
		if (args.length == 0) {
			System.out.print("No arguments, look at readme.\n");
			return;
		}
		
		System.out.print("Connecting to the database... ");
		
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:database.db");
			statement = connection.createStatement();
		} catch (Exception e) {
			System.out.print("ERROR\n");
			e.printStackTrace();
		}
		
		System.out.print("OK\nConnecting with Twitter... ");
		
		try {
			twitter = Operations.login();
		} catch (Exception e) {
			System.out.print("ERROR\n");
			e.printStackTrace();
		}
		
		System.out.print("OK\n");
		
		if (args[0].equals("add")) {
			if (args.length < 2) {
				System.out.print("Too little number of arguments.\n");
				return;
			}
			
			Operations.addAuthor(connection, args[1]);
		}
		else if (args[0].equals("remove")) {
			if (args.length < 2) {
				System.out.print("Too little number of arguments.\n");
				return;
			}
			
			Operations.removeAuthor(connection, args[1]);
		}
		else if (args[0].equals("removetweets")) {
			if (args.length < 2)
				Operations.removeTweets(connection, null);
			else
				Operations.removeTweets(connection, args[1]);
		}
		else if (args[0].equals("propose")) {
			Status proposal = null;
			
			if (args.length < 2)
				proposal = Operations.giveTheBest(twitter, connection, null);
			else
				proposal = Operations.giveTheBest(twitter, connection, args[1]);
			
			if (proposal == null)
				System.out.print("No proposals.\n");
			else
				System.out.print(proposal.getUser().getName() + ":\n" + proposal.getText() + "\n");
		}
		else if (args[0].equals("retweet")) {
			if (args.length < 2)
				Operations.retweet(twitter, connection, Operations.giveTheBest(twitter, connection, null));
			else
				Operations.retweet(twitter, connection, Operations.giveTheBest(twitter, connection, args[1]));
		}
		else if (args[0].equals("download")) {
			if (args.length < 2)
				Operations.archiveTweets(twitter, connection, null);
			else
				Operations.archiveTweets(twitter, connection, args[1]);
		}
		else if (args[0].equals("authors")) {
			ArrayList<String> authors = Operations.getAuthors(connection);
			
			System.out.print("Authors in the database:\n");
			for (String author: authors)
				System.out.println(author);
		}
		else if (args[0].equals("newdatabase"))
			CreateDatabase.newDatabase();
		else if (args[0].equals("removeauthors"))
			Operations.removeAuthors(connection);
		else
			System.out.print("Unknown parameter.\n");
		
		connection.close();
		statement.close();
	}
}
