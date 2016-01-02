(ns mdss.methods.mindistance
  (:require [loom.graph :as g]
            [mdss.core :as c]
            [clojure.math.combinatorics :as combo])

  (:use [clojure.core.matrix]))

(defn- range-index
  [pm choices]
  (map #(.indexOf pm %) choices))

(defn- alternatives-matrix
  [pm]
  (let [
    pm-dim (count pm)
    pm-range (range
      (reduce min pm)
      (inc (reduce max pm)))]
    (matrix
      (partition
        pm-dim
        (for [
          x pm-range
          y pm-range]
          (compare
            (.indexOf pm y)
            (.indexOf pm x)))))))

(defn- get-inner-permutations
  [pm]
  (let [
    vecs (filter vector? pm)
    vecs-counts (map count vecs)
    vecs-positions (map #(.indexOf pm %) vecs)
    vecs-perms (map permutations vecs)
    vecs-produced (apply cartesian-product vecs-perms)]
    (map
      (fn [vec-produced]
        (flatten
          (map-indexed
            (fn [current-index current-value]
              (if (vector? current-value)
                (nth
                  vec-produced
                  (.indexOf vecs-positions current-index))
                current-value))
            pm)))
      vecs-produced)))

(deftype MinDistanceMethod []
  c/PDecisionSystem

  (init [this] {:step 0})

  (solve [this input]
    (let [
      step (:step input)
      choices (:choices input)
      pm (combo/permutations (range 1 (inc (count choices))))
      ri (range-index pm choices)]

      (case step
        0 (assoc input
            :pm pm
            :ri ri)

        1 input)))

  (solved? [this input] (>= (get input :step) 1))

  (graph [this input]
    (g/weighted-graph)))
