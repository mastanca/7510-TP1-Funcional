(ns input-validator)

(defn valid-input?
  [input char]
  (if (empty? input)
    false
    (= (subs input (- (.length input) 1) (.length input)) char))
  )
