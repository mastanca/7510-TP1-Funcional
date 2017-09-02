(ns database-parser-test
  (:require [clojure.test :refer :all]
            [database-parser :refer :all]))

(def valid-database "
  varon(juan).
  mujer(carla).
  padre(mario,juan).
")

(deftest valid-database-parsing-test
  testing "Parsing should return list of facts"
  is (= (parse-database-string valid-database)
        (list ["varon(juan)" "mujer(carla)" "padre(mario,juan)"])))
