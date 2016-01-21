(ns mdss.methods.mindistance-test
  (:require [clojure.test :refer :all]
            [mdss.methods.mindistance :refer :all]))

(deftest mindistance-range-index-test
  (testing "Range index test."
    (are [pm choices result]
      (= result (range-index pm choices))
      [[1 2]] [[1 2]] '(0)
      [[1 2]] [[2 1]] '(-1)
      [[1 2] [2 1]] [[1 2]] '(0)
      [[1 2] [2 1]] [] '()
      [[1 2] [2 1]] [[2 1]] '(1)
      [[1 2] [2 1]] [[1 2] [2 1]] '(0 1)
      [[1 2] [2 1]] [[2 1] [1 2]] '(1 0))))

(deftest get-inner-permutations-test
  (testing "Inner permutations test."
    (are [pm result]
      (= result (get-inner-permutations pm))
      [1 2 3] ['(1 2 3)]
      [] ['()]
      [1 [2 3]] ['(1 2 3) '(1 3 2)]
      [[1 2 3]] ['(1 2 3) '(1 3 2) '(2 1 3) '(2 3 1) '(3 1 2) '(3 2 1)])))

(deftest permutation-range-test
  (testing "Permutation range test."
    (are [pm-vec result]
      (= result (permutation-range pm-vec))
      [] '()
      [1 2 3] '(1 2 3)
      [3 2 1] '(1 2 3)
      [1] '(1))))

(deftest matrices-distance-test
  (testing "Matrices distances."
    (are [mx1 mx2 result]
      (= result (matrices-distance mx1 mx2))
      [] [] 0
      [[1]] [[0]] 1.0
      [[-1]] [[1]] 2.0
      [[0 1] [-1 0]] [[0 0] [0 0]] 2.0)))
