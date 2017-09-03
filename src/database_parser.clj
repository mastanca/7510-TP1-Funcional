(ns database-parser)

(require '[clojure.string :as str])
(require '[input-validator])
(require '[rules-and-facts])

(defn- remove-trailing-char
  [s]
  (subs s 0 (- (.length s) 1))
  )

(defn- remove-first-two-chars
  [s]
  (subs s 2 (.length s))
  )

(defn- create-rules-and-facts
  "Creates a RulesAndFacts record from the given parsed db"
  [parsed-db]
  (let [rulesAndFacts (rules-and-facts/->RulesAndFacts
                        (filter #(str/includes? % ":-") parsed-db)
                        (filter #(not (str/includes? % ":-")) parsed-db))]
    rulesAndFacts
    )
  )

(defn parse-database-string
  "Parses a given database string, returning a RulesAndFacts record or nil if database is corrupt"
  [database]
  (let [splitted-elements
        (map remove-first-two-chars
             (remove str/blank? (str/split database #"\n")))]
    (if (not-every? #(input-validator/valid-input? % ".") splitted-elements)
      nil
      (create-rules-and-facts (map remove-trailing-char splitted-elements))))
  )

