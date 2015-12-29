(ns mdss.methods.mindistance
  (:require [loom.graph :as g]
            [mdss.core :as c]
            [clojure.math.combinatorics :as combo])

  (:use [clojure.core.matrix]))

(defn- range-index
  [pm choices]
  (map #(.indexOf pm %) choices))

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
