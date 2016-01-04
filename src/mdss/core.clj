(ns mdss.core
  (:require [gorilla-repl.core :as g]
            [clojure.java.browse :as b])
  (:gen-class))

(defprotocol PDecisionSystem
  (init [this])
  (solve [this input])
  (solved? [this input])

  (graph [this input]))

(defn -main
  [& args]
  (def arguments {
    :ip "127.0.0.1"
    :port 8889
    :project "mdss"
    })
  (g/run-gorilla-server arguments)
  (b/browse-url
    (str
      "http://"
      (:ip arguments)
      ":"
      (:port arguments)
      "/worksheet.html?filename=ws/mindistance-method.cljw")))
