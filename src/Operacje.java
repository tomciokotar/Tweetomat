import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterException;
import twitter4j.Status;
import twitter4j.conf.ConfigurationBuilder;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class Operacje
{
	private static int min(int a, int b)
	{
		if (a <= b)
			return a;
		return b;
	}
	
	public static Twitter zaloguj()
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
	
	public static void archiwizujTweety(Twitter twitter, Connection con, String autor)
		throws TwitterException, SQLException
	{
		if (autor != null && !czyWBazie(con, autor)) {
			System.out.print("Brak danego użytkownika w bazie.\n");
			return;
		}
		
		Statement st = con.createStatement();
		ArrayList<String> osoby = autorzy(con);
		
		for (String osoba : osoby) {
			if (autor != null && !osoba.equals(autor))
				continue;
			
			System.out.print("Pobieranie dla " + osoba + "... ");
				
				List<Status> tweety = null;
				
				try {
					tweety = twitter.getUserTimeline(osoba);
				} catch (Exception e) {
					System.out.println("nie można pobrać tweetów, być może nazwa konta jest niepoprawna.");
					return;
				}
				
				for (Status tw : tweety) {
					int skutecznosc = 3*tw.getRetweetCount() + 3*tw.getFavoriteCount()
						+ tw.getUser().getFollowersCount();
					
					st.executeUpdate("insert into tweety values(" + tw.getId() + ", '"
						+ tw.getUser().getScreenName() + "', " + skutecznosc + ")");
				}
				System.out.print("OK\n");
		}
		
		st.close();
		System.out.print("Pobrano tweety.\n");
	}
	
	public static Status dajNajlepszy(Twitter twitter, Connection con, String autor)
		throws SQLException, TwitterException
	{
		Statement st = con.createStatement();
		ResultSet rs = null;
		
		if (autor == null)
			rs = st.executeQuery("select distinct * from tweety order by skutecznosc desc");
		else
			rs = st.executeQuery("select distinct * from tweety where autor = '" + autor
				+ "' order by skutecznosc desc");
		
		while (rs.next()) {
			long id = rs.getLong("id");
			Status akt = twitter.showStatus(id);
			Statement stat = con.createStatement();
			
			ResultSet uzyto = stat.executeQuery("select count(*) as ile from zretweetowane where tresc like \"%"
				+ akt.getText().substring(0, min(akt.getText().length(), 100)) + "%\"");
			
			if (uzyto.getInt("ile") == 0) {
				uzyto.close();
				stat.close();
				rs.close();
				st.close();
				return akt;
			}
			
			uzyto.close();
			stat.close();
		}
		
		rs.close();
		st.close();
		return null;
	}
	
	public static void dodajAutora(Connection con, String autor) throws SQLException
	{
		if (czyWBazie(con, autor)) {
			System.out.print("Dane konto już zostało dodane do bazy.\n");
			return;
		}
		
		Statement st = con.createStatement();
		st.executeUpdate("insert into osobistosci values('" + autor + "')");
		st.close();
		
		System.out.print("Dodano konto: " + autor + "\n");
	}
	
	public static void zretweetuj(Twitter twitter, Connection con, Status tweecik) throws SQLException, TwitterException
	{
		if (tweecik == null) {
			System.out.print("Nie ma czego zretweetować.\n");
			return;
		}
		
		Statement st = con.createStatement();
		
		twitter.retweetStatus(tweecik.getId());
		st.executeUpdate("insert into zretweetowane values(\"" + tweecik.getText() + "\")");
		
		st.close();
		System.out.print("Poszedł retweet.\n");
	}
	
	public static void wyczyscTweety(Connection con, String autor) throws SQLException
	{
		if (autor != null && !czyWBazie(con, autor)) {
			System.out.print("Danego autora nie ma w bazie.\n");
			return;
		}
		
		Statement st = con.createStatement();
		
		if (autor == null)
			st.executeUpdate("delete from tweety");
		else
			st.executeUpdate("delete from tweety where autor = '" + autor + "'");
		
		st.close();
		System.out.print("Wyczyszczono.\n");
	}
	
	public static void usunAutora(Connection con, String autor) throws SQLException
	{
		if (!czyWBazie(con, autor)) {
			System.out.print("Danego autora nie ma w bazie.\n");
			return;
		}
		
		wyczyscTweety(con, autor);
		
		Statement st = con.createStatement();
		st.executeUpdate("delete from osobistosci where nazwa = '" + autor + "'");
		st.close();
		
		System.out.print("Usunięto konto: " + autor + "\n");
	}
	
	public static ArrayList<String> autorzy(Connection con) throws SQLException
	{
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery("select * from osobistosci");
		
		ArrayList<String> osoby = new ArrayList<String>();
		
		while (rs.next())
			osoby.add(rs.getString("nazwa"));
		
		rs.close();
		st.close();
		return osoby;
	}
	
	public static void wyczyscAutorow(Connection con) throws SQLException
	{
		Statement st = con.createStatement();
		st.executeUpdate("delete from tweety");
		st.executeUpdate("delete from osobistosci");
		st.close();
		
		System.out.print("Wyczyszczono.\n");
	}
	
	private static boolean czyWBazie(Connection con, String autor) throws SQLException
	{
		Statement st = con.createStatement();
		int ile = st.executeQuery("select count(*) as ile from osobistosci where nazwa = '" + autor + "'").getInt("ile");
		st.close();
		
		return ile > 0;
	}
}
