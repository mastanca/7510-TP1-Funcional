(ns logical-interpreter)

(require '[database-parser])

(defn evaluate-query
  "Returns true if the rules and facts in database imply query, false if not. If
  either input can't be parsed, returns nil"
  [database query]
  (def parsed-database (database-parser/parse-database-string database))
  (if #(nil? parsed-database)
    nil
    (if (some #(= query %) parsed-database) true false))
  )
