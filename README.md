# Tweetomat
Simple database application used for finding and retweeting the most popular tweets from the given authors.

At the first run you need to use ```newdatabase``` and ```download``` to set up everything.

An account connected with the application: twitter.com/panretliter

# How to run
```$ java -jar Tweetomat.jar <parameter>```

# Possible parameters
- ```add <author>``` - adds the user ```<author>``` to authors table
- ```remove <author>``` - removes the user and his tweets from the database
- ```removetweets [author]``` - removes all the archived tweets or all tweets from the given author (if given)
- ```propose [author]``` - displays the content of the most popular tweet from the database or the most popular tweet from the given author (if given)
- ```retweet [author]``` - instead of displaying, retweets the most popular tweet
- ```download [author]``` - downloads and saves the last 20 tweets from the given author to the database (or 20 tweets from everyone)
- ```authors``` - displays the names of all authors stored in the database
- ```newdatabase``` - removes the old one and creates the new, default database with default authors (a few famous sportsmen)
- ```removeauthors``` - removes all authors with their tweets from the database

<b>Key:</b>
- ```<author>``` - an author's name that exists in the database must be given
- ```[author]``` - an author's name is optional - if it's not given, the command will be applied to all authors in the database

<b>Note:</b>
After setting up the new database (using ```newdatabase```), the table with tweets is empty. You need to use ```download``` first to fill it in. Then you're able to display and retweet tweets.
