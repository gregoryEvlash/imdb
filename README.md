# IMDB Search

Test assignment

Written using Akka streams for parsing and uploading the data, Slick for as ORM and Circe for json.
We should understand that its something between POC and MVP.

### Running app
Application uses mysql as database.
At first you need to upload the data. For this application i used 4 data files
title.ratings.tsv, title.principals.tsv, title.basics.tsv, name.basics.tsv

Parsing and uploading is very expensive operation. You need to provide a lot of memory and cores, besides it might take for 2 hours to init all data.
So if you want to upload you need to pass as argument flag "upload" at first.
Also path name of row data and path to the file:

titleRating     /home/user/data/title.ratings.tsv
titlePrincipal  /home/user/data/title.principals.tsv
titleBasic      /home/user/data/title.basics.tsv
nameBasic       /home/user/data/name.basics.tsv

So the full run command will be like:

"sbt -J-Xmx4G -J-Xms2G run upload titleRating /home/user/data/title.ratings.tsv titlePrincipal /home/user/data/title.principals.tsv titleBasic /home/user/data/title.basics.tsv nameBasic /home/user/data/name.basics.tsv"

### In order to run project:

clone it from git (hint! there you can see source code and it evolution)

make sbt run with arguments above

application starts localhost:8085 

environment variables HOST and PORT allows you override host and port

Also added assembly plugin that allows you to assembly project into jar file

sbt assembly

run it with java -jar target/scala-2.12/imdb_2.12-0.0.1.jar ${arguments above}

## Routes
### Titles
GET http://localhost:8085/imdb/videos/title/{SEARCH_TITLE}?limit=10&offset=0

Search films by Main or original title. Sort it by rating( TODO should be improved asc desc) and respond 200 ok within limit offset amount. 
By default limit offset is limit = 10 offset = 0

response example :
{
	"films": [{
		"id": "tt0068646",
		"title": "The Godfather",
		"originalTitle": "The Godfather",
		"year": "1972",
		"rating": 9.2,
		"cast": [{
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

### By genres
GET http://localhost:8085/imdb/videos/genre?limit=10&offset=0&genre=comedy&genre=drama

Search films by genres provided in query, allows you to search across several genres.
Respond you 200 ok within provided limit.
Here i avoid info of participants for make search faster.
{
// todo pretty parser
	"films": [{
            "filmTconst": "tt0792332",
            "filmPrimaryTitle": "Stayin' Alive",
            "filmOriginalTitle": "Stayin' Alive",
            "filmStartYear": "1979",
            "averageRating": 10.0
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
There is no final routes or generic search mechanism. For that we need to analyze all data relations, split to much more tables with indexes. Put much more effort than one man/week.

### Db storage
I used classical DB - MySql. Through config you need to provide connection url, name, pass, load factor etc.

### Services
There is 3 services in my project:
1 Search service is responsible for plain search.
2 Dataloader parse tsv files, convert rows to entities, and streaming it into DB through Slick 
3 KevinBacon search shortest pass from Kevin bacon to necessary person. Using BFS approach. Not implemented for now.

## Validation
There is no validation for now. It might be used for validate whether actor in kevin bacon request exists, but it to expensive write a lot of boilerplate code instead of just check it in DB inside service and return NOT FOUND  