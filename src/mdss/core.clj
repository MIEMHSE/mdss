(ns mdss.core)

(defprotocol PDecisionSystem
  (init [this])
  (solve [this input])
  (solved? [this input])

  (graph [this input]))
