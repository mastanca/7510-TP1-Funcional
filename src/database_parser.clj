(ns database-parser)

(require '[clojure.string :as str])

(defn parse-database-string
    "Parses a given database string, returning a list of facts or nil if database is corrupt"
    [database]
    str/split database #".")