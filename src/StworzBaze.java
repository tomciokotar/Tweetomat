import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.Status;
import java.sql.*;
import java.util.List;

public class StworzBaze
{
	public static void nowaBaza() throws SQLException, TwitterException, ClassNotFoundException
	{
		System.out.print("Tworzenie bazy... ");
		Class.forName("org.sqlite.JDBC");
		
		Connection con = DriverManager.getConnection("jdbc:sqlite:baza.db");
		Statement st = con.createStatement();
		
		Twitter twitter = Operacje.zaloguj();
		
		st.executeUpdate("drop table if exists osobistosci");
		st.executeUpdate("drop table if exists tweety");
		st.executeUpdate("drop table if exists zretweetowane");
		
		st.executeUpdate("create table osobistosci(nazwa varchar(100))");
		st.executeUpdate("create table tweety(id long, autor varchar(100), skutecznosc integer)");
		st.executeUpdate("create table zretweetowane(tresc varchar(300))");
		st.executeUpdate("insert into osobistosci values('stoch_kamil')");
		st.executeUpdate("insert into osobistosci values('HolowczycBlog')");
		st.executeUpdate("insert into osobistosci values('Lewandowski_BVB')");
		st.executeUpdate("insert into osobistosci values('SchumiOfficial')");
		st.executeUpdate("insert into osobistosci values('MorgensternT')");
		st.executeUpdate("insert into osobistosci values('Cristiano')");
		
		List<Status> wpisy = twitter.getHomeTimeline();
		for (Status wpis : wpisy)
			st.executeUpdate("insert into zretweetowane values(\"" + wpis.getText() + "\")");
		
		con.close();
		st.close();
		
		System.out.print("OK\n");
	}
}
