(defproject mdss "0.1.0-SNAPSHOT"
  :description "Decision Support System"
  :url "https://github.com/MIEMHSE/mdss"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/math.combinatorics "0.1.1"]
                 [loom-gorilla "0.1.0"]
                 [aysylu/loom "0.5.0"]
                 [gorilla-renderable "2.0.0"]
                 [lein-gorilla "0.3.5"]
                 [net.mikera/core.matrix "0.47.1"]]
  :main mdss.core
  :aot [mdss.core]
  :plugins [[lein-gorilla "0.3.5"]
            [lein-capsule "0.2.0"]]
  :jvm-opts ["-client"]
  :capsule {
   :types {
     :fat {}}})
