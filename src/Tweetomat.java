import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.Status;
import java.sql.*;
import java.util.ArrayList;

public class Tweetomat
{
	public static void main(String[] args) throws SQLException, TwitterException, ClassNotFoundException
	{
		Connection con = null;
		Statement st = null;
		Twitter twitter = null;
		
		if (args.length == 0) {
			System.out.print("Brak argumentów, instrukcje w pliku readme.\n");
			return;
		}
		
		System.out.print("Łączenie z bazą danych... ");
		
		try {
			Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection("jdbc:sqlite:baza.db");
			st = con.createStatement();
		} catch (Exception e) {
			System.out.print("BŁĄD\n");
			e.printStackTrace();
		}
		
		System.out.print("OK\nŁączenie z Twitterem... ");
		
		try {
			twitter = Operacje.zaloguj();
		} catch (Exception e) {
			System.out.print("BŁĄD\n");
			e.printStackTrace();
		}
		
		System.out.print("OK\n");
		
		if (args[0].equals("dodaj")) {
			if (args.length < 2) {
				System.out.print("Za mało argumentów.\n");
				return;
			}
			
			Operacje.dodajAutora(con, args[1]);
		}
		else if (args[0].equals("usun")) {
			if (args.length < 2) {
				System.out.print("Za mało argumentów.\n");
				return;
			}
			
			Operacje.usunAutora(con, args[1]);
		}
		else if (args[0].equals("wyczysctweety")) {
			if (args.length < 2)
				Operacje.wyczyscTweety(con, null);
			else
				Operacje.wyczyscTweety(con, args[1]);
		}
		else if (args[0].equals("zaproponuj")) {
			Status propozycja = null;
			
			if (args.length < 2)
				propozycja = Operacje.dajNajlepszy(twitter, con, null);
			else
				propozycja = Operacje.dajNajlepszy(twitter, con, args[1]);
			
			if (propozycja == null)
				System.out.print("Brak propozycji.\n");
			else
				System.out.print(propozycja.getUser().getName() + ":\n" + propozycja.getText() + "\n");
		}
		else if (args[0].equals("zretweetuj")) {
			if (args.length < 2)
				Operacje.zretweetuj(twitter, con, Operacje.dajNajlepszy(twitter, con, null));
			else
				Operacje.zretweetuj(twitter, con, Operacje.dajNajlepszy(twitter, con, args[1]));
		}
		else if (args[0].equals("pobierz")) {
			if (args.length < 2)
				Operacje.archiwizujTweety(twitter, con, null);
			else
				Operacje.archiwizujTweety(twitter, con, args[1]);
		}
		else if (args[0].equals("autorzy")) {
			ArrayList<String> osoby = Operacje.autorzy(con);
			
			System.out.print("Konta w bazie:\n");
			for (String s : osoby)
				System.out.println(s);
		}
		else if (args[0].equals("nowabaza"))
			StworzBaze.nowaBaza();
		else if (args[0].equals("wyczyscautorow"))
			Operacje.wyczyscAutorow(con);
		else
			System.out.print("Nieznane polecenie.\n");
		
		con.close();
		st.close();
	}
}
