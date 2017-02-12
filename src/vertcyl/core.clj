(ns vertcyl.core
  (:require [scad-clj.model :refer [cube]]
            [scad-clj.scad :refer [write-scad]])
  (:gen-class))

(def complete
  (cube 10 10 10))

(defn render!
  []
  (spit "out/main.scad"
        (write-scad complete)))

;(render!)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (render!))
