(ns vertcyl.core
  (:require [scad-clj.model :refer [cube rotate translate union]]
            [scad-clj.scad :refer [write-scad]])
  (:gen-class))

(def switch-hole-mx-inverse
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

(defn render!
  []
  (spit "switch-hole.scad"
        (write-scad switch-hole-mx-inverse)))

(render!)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (render!))
