(ns mdss.methods.ahp
  (:require [loom.graph :as g]
            [mdss.core :as c]
            [clojure.math.combinatorics :as combo])

  (:use [clojure.core.matrix]))

(defn- random-consistency-value
  [dim]
  (nth
    '(0 0 0.58 0.90 1.12 1.24 1.32 1.41 1.45 1.49)
    (dec dim)))

(defn- prices-and-weights
  [mx]
  (let [
    prices (map #(Math/pow (reduce * %) (/ 1 (count %))) mx)
    pricesum (esum prices)
    weights (map #(/ % pricesum) prices)]
    {:prices prices :pricesum pricesum :weights weights}))

(deftype AHPMethod [translate]
  c/PDecisionSystem

  (init [this] {:step 0})

  (solve [this input]
    (let [step (:step input)]
      (case step
        0 (let [
            criteria-ratio (:criteria-ratio input)
            criteria-dim (count criteria-ratio)
            criteria-prices-and-weights (prices-and-weights criteria-ratio)
            criteria-weights (:weights criteria-prices-and-weights)
            criteria-sums (map esum criteria-ratio)
            consistency-lambda (reduce + (map * criteria-weights criteria-sums))
            consistency-index (/
              (- consistency-lambda criteria-dim)
              (dec criteria-dim))
            consistency-ratio (/
              consistency-index
              (random-consistency-value criteria-dim))]

            (assoc input
              :step (inc step)
              :consistency {
                :index consistency-index
                :ratio consistency-ratio
                :lambda consistency-lambda
                :dim criteria-dim
                :consistent (< consistency-ratio 0.2)
              }
              :criteria-prices-and-weights criteria-prices-and-weights))

        1 (let [
            alternative-ratio (:alternative-ratio input)
            alternative-keys (keys alternative-ratio)]

            (assoc input
              :step (inc step)
              :alternative-prices-and-weights
              (zipmap
                alternative-keys
                (map
                  #(-> alternative-ratio % prices-and-weights)
                  alternative-keys))))

        2 (let [
            alternative-ratio (:alternative-ratio input)
            alternative-keys (keys alternative-ratio)
            criteria-prices-and-weights (:criteria-prices-and-weights input)
            alternatives (:alternatives input)
            alternative-prices-and-weights (:alternative-prices-and-weights input)]

            (assoc input
              :step (inc step)
              :alternative-value
              (zipmap
                (map translate alternatives)
                (map
                  #(reduce +
                    (map *
                      (-> alternative-prices-and-weights % :weights)
                      (:weights criteria-prices-and-weights)))
                  alternative-keys))))

        3 input)))

  (solved? [this input] (>= (get input :step) 3))

  (graph [this input]
    (let [
      goal (:goal input)
      criteria (get-column (:criteria-weights input) 0)
      alternatives (:alternatives input)]
      (apply g/digraph
        (concat
          (combo/cartesian-product
            [(translate goal)]
            (map translate criteria))
          (combo/cartesian-product
            (map translate criteria)
            (map translate alternatives)))))))
