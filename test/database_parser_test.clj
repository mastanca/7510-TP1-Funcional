(ns database-parser-test
  (:require [clojure.test :refer :all]
            [database-parser :refer :all]))

(def valid-database "
  varon(juan).
  mujer(carla).
  padre(mario,juan).
")

(def invalid-database "
  varon(carlos).
  mujer
")

(deftest valid-database-parsing-test
  (testing "Parsing should return list of facts"
    (is (= (parse-database-string valid-database)
           (list "varon(juan)" "mujer(carla)" "padre(mario,juan)")))))

(deftest invalid-database-parsing-test
  (testing "Parsing invalid database should return nil"
    (is (= (parse-database-string invalid-database)
           nil))))