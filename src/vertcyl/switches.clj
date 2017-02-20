(ns vertcyl.switches
  (:require [scad-clj.model :refer [cube rotate translate union]]
            [scad-clj.scad :refer [write-scad]]))

(def mx-cutter
  (let [extra (cube 3.5 15.6 15.6)
        angle (/ Math/PI 2)]
    (union
        (cube 14 14 14)
        (translate [4.0 0 0] extra)
        (translate [-4.0 0 0] extra)
        (->> extra
             (rotate angle [0 0 1])
             (translate [0 4 0]))
        (->> extra
             (rotate angle [0 0 1])
             (translate [0 -4 0])))))

(def matias-cutter
  (cube  12.8 15.5 15.6))

(defn cutter
  [switch]
  (cond
    (= switch :mx) mx-cutter
    (= switch :matias) matias-cutter))

(defn padding
  [switch]
  (cond
    (= switch :mx) [22 22]
    (= switch :matias) [20 20]))

(defn render!
  []
  (spit "out/mx-cutter.scad"
        (write-scad mx-cutter))
  (spit "out/matias-cutter.scad"
        (write-scad matias-cutter)))

(render!)
