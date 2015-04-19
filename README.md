# Tweetomat
Prosta aplikacja bazodanowa wspomagająca kreowanie osobowości twitterowej poprzez retweetowanie popularnych tweetów.

Na początku otrzymujemy gotową bazę danych z kontami kilku znanych sportowców oraz ich tweetami.

Konto skojarzone z aplikacją: twitter.com/panretliter

# Uruchamianie
```$ java -jar tweetomat.jar <parametry>```

# Parametry
- ```dodaj <autor>``` - dodaje użytkownika ```<autor>``` do tabeli przechowującej konta
- ```usun <autor>``` - usuwa użytkownika i jego tweety z bazy
- ```wyszysctweety [autor]``` - usuwa wszystkie zarchiwizowane tweety, bądź wszystkie tweety danego autora
- ```zaproponuj [autor]``` - wyświetla treść najbardziej popularnego tweeta z całej bazy lub spośród tweetów danego autora
- ```zretweetuj [autor]``` - zamiast proponować, retweetuje najpopularniejszego tweeta
- ```pobierz [autor]``` - archiwizuje ostatnie 20 tweetów danego autora (ew. wszystkich autorów z bazy)
- ```autorzy``` - wyświetla listę użytkowników trzymaną w bazie
- ```nowabaza``` - usuwa starą i tworzy nową, domyślną bazę ze sportowcami
- ```wyszyscautorow``` - usuwa wszystkie konta z bazy oraz tweety z tych kont

<b>Legenda:</b>
- ```<autor>``` - w tym miejscu musi znaleźć się nazwa użytkownika istniejącego w bazie
- ```[autor]``` - nazwa użytkownika może się pojawić - jeśli tak się nie stanie, instrukcja zostanie uogólniona na wszystkie konta z bazy

<b>Uwaga:</b>
Warto pamiętać, że po wczytaniu nowej bazy (po użyciu ```nowabaza```) tabela z tweetami jest pusta. Należy użyć parametru ```pobierz``` do ściągnięcia tweetów. Dopiero wtedy będziemy mogli otrzymywać sugestie i wysyłać retweety.
