(ns logical-interpreter)

(require '[database-parser])
(require '[input-validator])
(require '[clojure.string :as str])

(def args-regex #"\(([^\)]+)\)")
(def rule-name-regex #"^[^\(]+")
(def rule-name-and-args-regex #"^[^:-]+")
(def after-fact-separator-regex #"[:-].*$")

(declare parse-matching-rules)

(defn evaluate-query
  "Returns true if the rules and facts in database imply query, false if not. If
  either input can't be parsed, returns nil"
  [database query]
  (let [parsed-db (database-parser/parse-database-string database)]
    (if (input-validator/valid-input? query ")")
      (if (nil? parsed-db)
        nil
        (if (boolean (some #(= query %) (:facts parsed-db)))
          true
          (boolean (some #(= (parse-matching-rules query (:rules parsed-db)) %) (:facts parsed-db))))
        )

      nil)))

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

(defn- replace-args
  "REFACTOR! Replaces args for their values (Recursive maybe? with inc to keys)"
  [rule-args facts]
  (def clean-args (map #(str/trim %) (first rule-args)))
  ; Assume max is 3 args, in case of more just add more letters here
  (def letter-map (zipmap [:X :Y :Z] clean-args))
  (def letter (first (keys letter-map)))
  (def facts-replaced (replace-one-arg letter letter-map facts))
  (def letter (second (keys letter-map)))
  (def facts-replaced (replace-one-arg letter letter-map facts-replaced))
  (def letter (nth (keys letter-map) 2))
  (def facts-replaced (replace-one-arg letter letter-map facts-replaced))
  facts-replaced
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
  ;(def query-rule-name (re-find rule-name-regex query-rule))
  ;(def matching-rules (filter #(str/includes? (re-find rule-name-and-args-regex %) query-rule-name) rules)) ;"subtract(X, Y, Z) :- add(Y, Z, X)"
  ;(def number-of-args-query (get-rule-number-of-args (list query-rule)))
  ;(def rules-matching-both-name-and-args (filter #(= number-of-args-query (get-rule-number-of-args (list %))) matching-rules))
  ; Up to here we have the matching rules with both name and number of args
  ;(def query-rule-args (get-args (list query-rule)))
  ;(def replaced-facts (replace-args query-rule-args (get-rule-facts (first rules-matching-both-name-and-args))))
  )

