(ns vertcyl.core
  (:require [scad-clj.model :refer [union rotate translate sphere cube 
                                    difference]]
            [scad-clj.scad :refer [write-scad]]
            [vertcyl.row :refer [row-with-switches place-row normalise-row
                                 row-cutter]]
            )
  (:gen-class))


(defn hand
  [radius switch columns rows]
      (map 
        #(->> (row-with-switches radius switch columns)
              (place-row radius switch %))
        (range rows)))

(defn shell
  [radius switch columns rows]
  (let [thickness 3
        depth 15
        d (* radius 2)
        dd (* (+ radius depth) 2)
        cutter (translate [(- 0 radius depth) 0 0] (cube dd dd dd))
        circum (* Math/PI 2 radius) 
        offset-one (/ Math/PI 0.5 (/ circum thickness))]

    (difference
      (sphere (+ radius depth))
      (sphere radius)
      (rotate offset-one [0 0 1] cutter)
      (map 
        #(->> (row-cutter radius depth switch columns)
              (place-row radius switch %))
        (range rows)))
      
      ))

(def complete
  (let [radius 120
        switch :mx
        columns 5
        rows 4]
  (union
    (hand radius switch columns rows)
    ;(shell radius switch columns rows)
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
