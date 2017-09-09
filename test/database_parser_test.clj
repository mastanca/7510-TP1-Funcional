(ns database-parser-test
  (:require [clojure.test :refer :all]
            [database-parser :refer :all]
            [rules-and-facts :refer :all]))

(def valid-database "
  varon(juan).
  mujer(carla).
  padre(mario, juan).
  fact(uno, dos, tres).
")

(def invalid-database "
  varon(carlos).
  mujer
")

(def valid-with-rules-database "
  varon(juan).
  mujer(carla).
  padre(mario, juan).
  fact(uno, dos, tres).
  hijo(X, Y) :- varon(X), padre(Y, X).
")

(deftest valid-database-parsing-test
  (testing "Parsing should return list of facts"
    (let [rulesAndFacts (->RulesAndFacts (list "varon(juan)" "mujer(carla)" "padre(mario, juan)" "fact(uno, dos, tres)") '())]
      (is (= (:facts (parse-database-string valid-database))
             (list "varon(juan)" "mujer(carla)" "padre(mario, juan)" "fact(uno, dos, tres)")))
      )
    (is (= (:rules (parse-database-string valid-database))
           '()))
    )
  )

(deftest invalid-database-parsing-test
  (testing "Parsing invalid database should return nil"
    (is (= (parse-database-string invalid-database)
           nil))))

(deftest valid-database-with-rules-parsing-test
  (testing "Parsing should return record with facts and rules"
    (let [rulesAndFacts (->RulesAndFacts (list "varon(juan)" "mujer(carla)" "padre(mario, juan)" "fact(uno, dos, tres)") '())]
      (is (= (:facts (parse-database-string valid-with-rules-database))
             (list "varon(juan)" "mujer(carla)" "padre(mario, juan)" "fact(uno, dos, tres)")))
      )
    (is (= (:rules (parse-database-string valid-with-rules-database))
           '("hijo(X, Y) :- varon(X), padre(Y, X)")))
    )
  )