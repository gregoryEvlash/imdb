package com.imdb.models

package object imdb {

  sealed trait DomainEntity

  type TCONST = String
  type NCONST = String

//    title.basics.tsv.gz - Contains the following information for titles:
  case class TitleBasic(
                         tconst: TCONST // alphanumeric unique identifier of the title
                         , titleType: String // the type/format of the title (e.g. movie, short, tvseries, tvepisode, video, etc)
                         , primaryTitle : String // the more popular title / the title used by the filmmakers on promotional materials at the point of release
                         , originalTitle: String // original title, in the original language
                         , isAdult: Int // - 0: non-adult title; 1: adult title
                         , startYear: String // (YYYY) – represents the release year of a title. In the case of TV Series, it is the series start year
                         , endYear: String // (YYYY) – TV Series end year. ‘\N’ for all other title types
                         , runtimeMinutes: Double //– primary runtime of the title, in minutes
                         , genres: String // (string array) – includes up to three genres associated with the title
                       ) extends DomainEntity

// Contains the principal cast/crew for titles
  case class TitlePrincipal(
                             tconst: TCONST // alphanumeric unique identifier of the title
                             , ordering: Int // a number to uniquely identify rows for a given titleId
                             , nconst: NCONST // alphanumeric unique identifier of the name/person
                             , category: String // the category of job that person was in
                             , job: Option[String] // - the specific job title if applicable, else '\N'
                             , characters: Option[String] //  the name of the character played if applicable, else '\N'
                           ) extends DomainEntity

// title.ratings.tsv.gz – Contains the IMDb rating and votes information for titles
  case class TitleRating(
                          tconst: TCONST // alphanumeric unique identifier of the title
                          , averageRating: Double
                          , numVotes: Long
                        ) extends DomainEntity

  // name.basics.tsv.gz – Contains the following information for names:
  case class NameBasic(
                        nconst: NCONST // id alphanumeric unique identifier of the name/person
                        , primaryName: String // name by which the person is most often credited
                        , birthYear: String // YYYY
                        , deathYear: Option[String] // YYYY format if applicable, else '\N'
                        , primaryProfession: String // the top-3 professions of the person
                        , knownForTitles: String //  titles the person is known for
                      ) extends DomainEntity

  case class NameTitle(nconst: NCONST, tconst: TCONST) extends DomainEntity
}
