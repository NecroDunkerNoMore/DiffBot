# DiffBot
A bot that periodically diffs a set of webpages and posts to Reddit when a change is detected.

# How to Use
This bot has several configuration files that are stubbed out, so to run this bot, will at minimum need to:

1. Copy `org/ndnm/diffbot/security.properties-TEMPLATE` to be `org/ndnm/diffbot/security.properties` (the latter has an entry in gitignore)

1. Edit `org/ndnm/diffbot/security.properties` so that any `CHANGEME` values have in fact been changed to valid values

1. Change `sql/diff_url_t.sql` to reflect which URLs are to be diff'd

1. Ensure a DB exists for which the above can use

1. Ensure a Reddit account exists for the above (must be registered as application developer to get API token)

1. Ensure specified subreddit exists to which the Reddit account has permissions to post to

1. Execute `ddl/bootstrap.sql` and all scripts under  `sql/*` against DB from earlier steps

1. Build executable jar with: `$ mvn -DskipTests=true pacakge`, which creates the jar `$DiffBot/target/diffbot.jar`

1. Execute the jar with `java -jar diffbot.jar &> diffbot.out` (a log file will additionally be created at $HOME/logs/diffbot.log)

1. Optional: Change `org/ndnm/diffbot/diffbot.properties` for time intervals of various functions; by default, the app will loop every ten seconds, authenticate w/ Reddit every 50 minutes, and poll the supplied URLs once every hour 
