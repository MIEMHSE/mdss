(ns mdss.methods.test
  (:require [mdss.core :as c]))

(deftype TestMethod [initial]
  c/PDecisionSystem

  (init [this] initial)
  (solve [this input] (+ input initial))
  (solved? [this input] true)

  (graph [this input] nil))
