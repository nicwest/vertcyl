(ns vertcyl.core
  (:require [scad-clj.model :refer [union rotate translate sphere cube 
                                    difference]]
            [scad-clj.scad :refer [write-scad]]
            [vertcyl.row :refer [row-with-switches]]
            )
  (:gen-class))

(defn normalise-row
  [radius block]
  (->> block
       (rotate (/ Math/PI 2) [1 0 0])
       (rotate (/ Math/PI 5/2) [0 1 0])
       (translate [(- radius) 0 (/ radius 2)])
       (rotate Math/PI [0 0 1])))

(def complete
  (let [radius 120
        diameter (* 2 radius)
        switch :mx
        columns 5
        rows 4]
    (union
      (->> (row-with-switches radius switch columns)
           (normalise-row radius)
           (rotate (/ Math/PI 12) [0 0 -1])
           (translate [105 89 0]))
      (->> (row-with-switches radius switch columns)
           (normalise-row radius)
           (translate [101 59 0]))
      (->> (row-with-switches radius switch columns)
           (normalise-row radius)
           (rotate (/ Math/PI 6) [0 0 1])
           (translate [110 25 0]))
      (->> (row-with-switches radius switch columns)
           (normalise-row radius)
           (rotate (/ Math/PI 7/2) [0 0 1])
           (translate [131 0 0]))
      )))


(defn render!
  []
  (spit "out/complete.scad"
        (write-scad complete)))

(render!)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (render!))
