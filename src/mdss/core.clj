(ns mdss.core
  (:require [gorilla-repl.core :as g]
            [clojure.string :as s]
            [clojure.java.browse :as b]
            [clojure.java.io :as io])
  (:gen-class))

(defprotocol PDecisionSystem
  (init [this])
  (solve [this input])
  (solved? [this input])

  (graph [this input]))

(def mdss-dir
  (doto
    (io/file
      (if (re-find #"win" (-> "os.name" System/getProperty s/lower-case))
        (System/getenv "APPDATA")
        (System/getProperty "user.home"))
      ".mdss")
    (.mkdir)))

(defn- mdss-file
  [filename]
  (str (io/file mdss-dir filename)))

(defn -main
  [& args]
  (let [
    arguments {
      :ip "127.0.0.1"
      :port 8889
      :nrepl-port-file (mdss-file ".nrepl-port")
      :gorilla-port-file (mdss-file ".gorilla-port")
      :project "mdss"}]
    (g/run-gorilla-server arguments)
    (b/browse-url
      (str
        "http://"
        (:ip arguments)
        ":"
        (:port arguments)
        "/worksheet.html?filename=ws/mindistance-method.cljw"))))
