(ns logical-interpreter)

(require '[database-parser])
(require '[input-validator])
(require '[clojure.string :as str])

(def args-regex #"\(([^\)]+)\)")
(def rule-name-regex #"^[^\(]+")
(def rule-name-and-args-regex #"^[^:-]+")
(def after-fact-separator-regex #"[:-].*$")

(declare parse-matching-rules)
(declare some-fact-matches)

(defn evaluate-query
  "Returns true if the rules and facts in database imply query, false if not. If
  either input can't be parsed, returns nil"
  [database query]
  (let [parsed-db (database-parser/parse-database-string database)]
    (if (input-validator/valid-input? query ")")
      (if (nil? parsed-db)
        nil
        (if (some-fact-matches query (:facts parsed-db))
          true
          (let [facts-from-rule (parse-matching-rules query (:rules parsed-db))
                ]
            (let [facts-from-rule (str/replace facts-from-rule #"\)," ");")
                  facts-from-rule (map #(str/split % #";\s*") (list facts-from-rule))
                  facts-from-rule (first facts-from-rule)
                  ]
              (every? #(some-fact-matches % (:facts parsed-db)) facts-from-rule)
              )
            )
          )
        )

      nil)))

(defn- some-fact-matches
  [fact facts]
  (some #(= fact %) facts)
  )

(defn- get-args
  [rules]
  (map #(str/split % #",") (filter #(not (str/includes? % "(")) (some #(re-find args-regex %) rules))))

(defn- get-rule-number-of-args
  [rules]
  (first (map #(count %) (get-args rules))))

(defn- replace-one-arg
  [letter letter-map facts]
  (str/join (map #(str/replace % (name letter) (get letter-map letter)) facts))
  )

(defn- replace-at
  [counter letter-map facts]
  (let [letter (nth (keys letter-map) counter)
        facts-replaced (replace-one-arg letter letter-map facts)]
    facts-replaced
    )
  )

(defn- replace-args
  "REFACTOR! Replaces args for their values (Recursive maybe? with inc to keys)"
  [rule-args facts]
  (let [clean-args (map #(str/trim %) (first rule-args))
        letter-map (zipmap [:X :Y :Z] clean-args)
        keys-size (count (keys letter-map))
        counter 0
        ]
    (def letter (nth (keys letter-map) counter))
    (def facts-replaced (replace-one-arg letter letter-map facts))
    (let [counter (+ counter 1)]
      (if (<= counter keys-size)
        (
          (def letter (nth (keys letter-map) counter))
          (def facts-replaced (replace-one-arg letter letter-map facts-replaced))
          (let [counter (+ counter 1)]
            (if (< counter keys-size)
              (
                (def letter (nth (keys letter-map) counter))
                (def facts-replaced (replace-one-arg letter letter-map facts-replaced))
                )
              )
            )
          )
        )
      )
    facts-replaced
    )
  )

(defn- get-rule-facts
  "Get the facts of a given rule"
  [rule]
  (let [facts (subs (re-find after-fact-separator-regex rule) 3)]
    facts)
  )

(defn- parse-matching-rules
  "Parses the rules that match the given query"
  [query-rule rules]
  (let [query-rule-name (re-find rule-name-regex query-rule)
        matching-rules (filter #(str/includes? (re-find rule-name-and-args-regex %) query-rule-name) rules)
        number-of-args-query (get-rule-number-of-args (list query-rule))
        rules-matching-both-name-and-args (filter #(= number-of-args-query (get-rule-number-of-args (list %))) matching-rules)
        query-rule-args (get-args (list query-rule))]
    (if (empty? matching-rules)
      false
      (replace-args query-rule-args (get-rule-facts (first rules-matching-both-name-and-args)))
      )
    )
  )

