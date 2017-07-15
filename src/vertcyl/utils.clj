(ns vertcyl.utils
  (:require [scad-clj.scad :refer [write-scad]]))

(defn render!
  [filename part]
  (spit (str "out/" filename ".scad")
    (write-scad part)))
