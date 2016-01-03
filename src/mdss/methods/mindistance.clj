(ns mdss.methods.mindistance
  (:require [loom.graph :as g]
            [clojure.string :as s]
            [mdss.core :as c])

  (:use [clojure.core.matrix]
        [clojure.math.combinatorics]))

(defn- range-index
  [pm choices]
  (map #(.indexOf pm %) choices))

(defn get-permutations-repr
  [pm]
  (reduce
    #(if
      (vector? %2)
      (apply
        conj %1
        (map (fn [x] (s/join "~" %2)) %2))
        (conj %1 %2)) [] pm))

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

(defn permutation-range
  [pm-vec]
  (range
    (reduce min pm-vec)
    (inc (reduce max pm-vec))))

(defn- alternatives-matrix-for-row
  [pm]
  (let [
    pm-vec (vec pm)
    pm-dim (count pm-vec)
    pm-range (permutation-range pm-vec)]
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
    matrices (map alternatives-matrix-for-row pms)]
    (emap
      #(/ % (count pms))
      (apply emap + matrices))))

(defn- matrices-distance
  [mx1 mx2]
  (esum
    (emap
      abs
      (emap - mx1 mx2))))

(deftype MinDistanceMethod []
  c/PDecisionSystem

  (init [this] {:step 0})

  (solve [this input]
    (let [
      step (:step input)
      choices (:choices input)
      choices-count (column-count choices)
      ams (get input :ams)
      new-step (inc step)]

      (case step
        0 (assoc input
            :ams (map alternatives-matrix choices)
            :step new-step)

        1 (let [
            pmx (permutations
                (permutation-range
                  [1 choices-count]))
            matrices (map alternatives-matrix-for-row pmx)
            distances (map
              (fn [am choice]
                (map
                  (fn [pm mx]
                    [choice pm (matrices-distance am mx)])
                  pmx
                  matrices))
              ams
              choices)
            distance-sums (transpose
              (map
                vec
                (map-indexed
                  (fn [expert-index expert]
                    (map
                      #(nth % 2)
                      (nth distances expert-index)))
                  distances)))
            distance-sums-and-their-sum (map
              #(join %1 %2 (apply + %2))
              pmx
              distance-sums)
            only-sums (map last distance-sums-and-their-sum)
            pm-index (.indexOf
              only-sums
              (reduce min only-sums))
            minimal-pm (nth pmx pm-index)]
            (assoc input
              :pmx pmx
              :matrices matrices
              :distances distances
              :distance-sums distance-sums-and-their-sum
              :minimal-pm minimal-pm
              :step new-step)))))

  (solved? [this input] (>= (get input :step) 2))

  (graph [this input]
    nil))
