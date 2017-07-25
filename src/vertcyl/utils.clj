(ns vertcyl.utils
  (:require [scad-clj.scad :refer [write-scad]]))

(defn two-way-range
  [n]
  (let [h (/ n 2)]
    (map #(+ (- % h) 0.5) (range n))))

(defn render!
  [filename part]
  (spit (str "out/" filename ".scad")
    (write-scad part)))
