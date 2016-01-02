(ns mdss.methods.mindistance
  (:require [loom.graph :as g]
            [mdss.core :as c])

  (:use [clojure.core.matrix]
        [clojure.math.combinatorics]))

(defn- range-index
  [pm choices]
  (map #(.indexOf pm %) choices))

(defn- get-inner-permutations
  [pm]
  (let [
    vecs (filter vector? pm)
    vecs-positions (map #(.indexOf pm %) vecs)
    vecs-perms (map permutations vecs)
    vecs-produced (apply cartesian-product vecs-perms)]
    (vec
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
        vecs-produced))))

(defn- alternatives-matrix-for-row
  [pm]
  (let [
    pm-vec (vec pm)
    pm-dim (count pm-vec)
    pm-range (range
      (reduce min pm-vec)
      (inc (reduce max pm-vec)))]
    (matrix
      (partition
        pm-dim
        (for [
          x pm-range
          y pm-range]
          (compare
            (.indexOf pm-vec y)
            (.indexOf pm-vec x)))))))

(defn alternatives-matrix
  [pmx]
  (let [
    pms (get-inner-permutations pmx)
    matrices (map alternatives-matrix-for-row pms)
    (emap
      #(/ % (count pms))
      (apply emap + matrices))))

(deftype MinDistanceMethod []
  c/PDecisionSystem

  (init [this] {:step 0})

  (solve [this input]
    (let [
      step (:step input)
      choices (:choices input)
      pm (permutations (range 1 (inc (count choices))))
      ri (range-index pm choices)]

      (case step
        0 (assoc input
            :pm pm
            :ri ri)

        1 input)))

  (solved? [this input] (>= (get input :step) 1))

  (graph [this input]
    (g/weighted-graph)))
