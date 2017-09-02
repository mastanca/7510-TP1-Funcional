(ns database-parser)

(require '[clojure.string :as str])

(defn- line-valid?
  [s]
  (= (subs s (- (.length s) 1) (.length s)) ".")
  )

(defn- remove-trailing-char
  [s]
  (subs s 0 (- (.length s) 1))
  )

(defn parse-database-string
  "Parses a given database string, returning a list of facts or nil if database is corrupt"
  [database]
  (def splitted-elements
    (filter (fn [coll] (not= coll ""))
            (str/split (str/join "" (str/split database #"\n")) #" ")))
  (if (not-every? line-valid? splitted-elements) nil (map remove-trailing-char splitted-elements))
  )

