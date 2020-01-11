package com.lunatech.imdb.models

package object imdb {

  type TCONST = String
  type NCONST = String

// todo perhaps redundant
  //  title.akas.tsv.gz - Contains the following information for titles:
  case class TitleAka(
                       titleId: TCONST // (string) - a tconst, an alphanumeric unique identifier of the title
                       , ordering: Int // (integer) – a number to uniquely identify rows for a given titleId
                       , title: String // (string) – the localized title
                       , region: String // (string) - the region for this version of the title
                       , language: String // (string) - the language of the title
                       , types: List[String] // (array) - Enumerated set of attributes for this alternative title. One or more of the following: "alternative", "dvd", "festival", "tv", "video", "working", "original", "imdbDisplay". New values may be added in the future without warning
                       , attributes: List[String] // (array) - Additional terms to describe this alternative title, not enumerated
                       , isOriginalTitle: Int // (boolean) – 0: not original title; 1: original title // todo to bool cause of optimization
                     )


  //    title.basics.tsv.gz - Contains the following information for titles:
  case class TitleBasic(
                         tconst: TCONST // alphanumeric unique identifier of the title
                         , titleType: String // the type/format of the title (e.g. movie, short, tvseries, tvepisode, video, etc)
                         , primaryTitle : String // the more popular title / the title used by the filmmakers on promotional materials at the point of release
                         , originalTitle: String // original title, in the original language
                         , isAdult: Int // - 0: non-adult title; 1: adult title // todo to bool cause of optimization
                         , startYear: String // (YYYY) – represents the release year of a title. In the case of TV Series, it is the series start year
                         , endYear: String // (YYYY) – TV Series end year. ‘\N’ for all other title types
                         , runtimeMinutes: Double //– primary runtime of the title, in minutes
                         , genres: List[String] // (string array) – includes up to three genres associated with the title
                       )

// todo perhaps redundant
  // Contains the director and writer information for all the titles in IMDb. Fields include:
  case class TitleCrew(
                        tconst: TCONST // alphanumeric unique identifier of the title
                        , directors: List[NCONST] //  director(s) of the given title
                        , writers: List[NCONST] // writer(s) of the given title
                      )

  // todo redundant
  // Contains the tv episode information. Fields include:
  case class TitleEpisode(
                           tconst: TCONST // alphanumeric identifier of episode
                           , parentTconst: TCONST // alphanumeric identifier of the parent TV Series
                           , seasonNumber: Int // season number the episode belongs to
                           , episodeNumber: Int // episode number of the tconst in the TV series
                         )

  // Contains the principal cast/crew for titles
  case class TitlePrincipal(
                             tconst: TCONST // alphanumeric unique identifier of the title
                             , ordering: Int // a number to uniquely identify rows for a given titleId
                             , nconst: NCONST // alphanumeric unique identifier of the name/person
                             , category: String // the category of job that person was in
                             , job: Option[String] // - the specific job title if applicable, else '\N'
                             , characters: Option[String] //  the name of the character played if applicable, else '\N'
                           )

  // title.ratings.tsv.gz – Contains the IMDb rating and votes information for titles
  case class TitleRating(
                          tconst: TCONST // alphanumeric unique identifier of the title
                          , averageRating: Double
                          , numVotes: Long
                        )

  // name.basics.tsv.gz – Contains the following information for names:
  case class NameBasic(
                        nconst: NCONST // id alphanumeric unique identifier of the name/person
                        , primaryName: String // name by which the person is most often credited
                        , birthYear: String // YYYY
                        , deathYear: Option[String] // YYYY format if applicable, else '\N'
                        , primaryProfession: List[String] // the top-3 professions of the person
                        , knownForTitles: List[TCONST] // the top-3 professions of the person
                      )

}
