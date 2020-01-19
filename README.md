# IMDB Search

Test assignment

Written using Akka streams for parsing and uploading the data, Slick as ORM and Circe for json.
We should understand that its something between POC and MVP.

### Running app
Application uses H2 as database (possible to store data in file or memory), its good for test concepts.
At first you need to upload the data. For this application i used 4 data files
title.ratings.tsv, title.principals.tsv, title.basics.tsv, name.basics.tsv

Parsing and uploading is very expensive operation. You need to provide a lot of memory and cores, besides it might take for 1 hour to init all data. On disk H2 data file take about 60 GB. 
So if you want to upload you need to pass as argument flag "upload" at first.
Also path name of row data and path to the file:

titleRating     /home/user/data/title.ratings.tsv
titlePrincipal  /home/user/data/title.principals.tsv
titleBasic      /home/user/data/title.basics.tsv
nameBasic       /home/user/data/name.basics.tsv

So the full run command will be like:

The more memory the better. In this case it takes about 25 minutes
sbt -J-Xmx9G -J-Xms7G "run upload titleRating /home/user/data/title.ratings.tsv titlePrincipal /home/user/data/title.principals.tsv titleBasic /home/user/data/title.basics.tsv nameBasic /home/user/data/name.basics.tsv;"

### In order to run project:

clone it from git 

make sbt run with arguments above

application starts localhost:8085 

environment variables HOST and PORT allows you override host and port

Also added assembly plugin that allows you to assembly project into jar file

sbt assembly

run it with java -jar target/scala-2.12/imdb_2.12-0.0.1.jar ${arguments above}

## Routes
### Titles & genres
GET http://localhost:8085/imdb/videos?title=The Godfather&limit=10&offset=0&genre=Criminal

Search films by passed params like title, genre. Sort it by rating( TODO should be improved asc desc) and respond 200 ok within limit offset amount. 
By default limit offset is limit = 10 offset = 0

response example :
{
	"films": [{
		"id": "tt0068646",
		"title": "The Godfather",
		"originalTitle": "The Godfather",
		"year": "1972",
		"rating": 9.2,
		"genre": "Criminal,Thriller"
		"castsCrews": [{
				"name": "Al Pacino",
				"category": "actor",
				"birth": "1940"
			},
			{
				"name": "Francis Ford Coppola",
				"category": "director",
				"birth": "1939"
			}
		]
	}]
}

### Errors
There possible several errors responses: 
Six degrees actor not found or custom error.

## About architecture:

Here i used prototype approach. I wanted to play around with new technologies for me such as (Slick 3. version and Akka streaming)
Because it was first usage of Slick i tried to resolve search and keep it type safely. No raw sql allowed. At all. But now i have experience in current version of slick api.

In search i rely on DB evaluation as much as possible.

Some of the responses data might be not full in your view. Cause its a thing for improvement.
There is generic search mechanism. Typesafe give us ability to combine queries based on params.

Everything is wrapped in tagless final, allow us to provide necessary multithreading impl (Cats.IO, Monix.Task etc.)

### Db storage
I used H2 as db, cause it allows to experiment with data like for different DB. Through config you need to provide connection url as env variable DB_URL, by default it "jdbc:h2:file:~/imdb"

### Services
There is 4 services in my project:
####1 Search service is responsible for plain search.
####2 Dataloader parse tsv files, convert rows to entities, and streaming it into DB through Slick 
####3 QueryBuilder build combine typesafe sql queries
####4 KevinBacon search shortest pass from Kevin bacon to necessary person. Using BFS approach. I didnt want to add spark graphx, neo4j or other graph DB. Cause at first it would be cheating, the second - i was influenced with idea BFS on SQL. So the main bottleneck was thousands queries to DB were very slow. So i bit overcomplicate code. I made a batch load from db for 100 colleagues and before adding them to queue i also check them. Besides i fetch only 'not visited' colleagues. Main query time for degree 3 is .... Find actor by name query, even with indexes

## Validation
There is only validation when parsing tsv files, like checking on \N or parsing entities. In order to fall tolerance i just log errors and skip entity 