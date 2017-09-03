(ns database-parser)

(require '[clojure.string :as str])
(require '[input-validator])

(defn- remove-trailing-char
  [s]
  (subs s 0 (- (.length s) 1))
  )

(defn- remove-first-two-chars
  [s]
  (subs s 2 (.length s))
  )

(defn parse-database-string
  "Parses a given database string, returning a list of facts or nil if database is corrupt"
  [database]
  (let [splitted-elements
        (map remove-first-two-chars
             (remove str/blank? (str/split database #"\n")))]
    (if (not-every? #(input-validator/valid-input? % ".") splitted-elements)
      nil
      (map remove-trailing-char splitted-elements)))
  )

