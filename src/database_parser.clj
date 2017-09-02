(ns database-parser)

(require '[clojure.string :as str])

(defn parse-database-string
  "Parses a given database string, returning a list of facts or nil if database is corrupt"
  [database]
  (def splitted-string
    (filter
      (fn [coll] (not= coll ""))
      (filter (fn [coll] (not= coll "\n"))
              (str/split (str/join "" (str/split database #"\.")) #" "))))
  (map str/trim-newline splitted-string)
  )