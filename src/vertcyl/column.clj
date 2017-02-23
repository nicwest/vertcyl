(ns vertcyl.column
  (:require [scad-clj.model :refer [cube rotate translate]]
            [scad-clj.scad :refer [write-scad]]
            [vertcyl.switches :refer [padding]]))

(defn get-step
  [radius switch]
  (let [gap 5
        [width _] (padding switch)
        circum (* Math/PI 2 radius)
        n (/ circum (+ gap width))]
    (/ (* Math/PI 2) n)))

(defn place-row
  [radius switch n block]
  (let [step (get-step radius switch)
        a (* step n)
        x (* (Math/sin a) radius)
        y (* (Math/cos a) radius)]
    (->> block
         (rotate a [0 0 -1])
         (translate [x y 0])
         )))

(defn render!
  []
  (let [radius 120
        switch :mx
        columns 4 ]
  (spit "out/column-test.scad"
        (write-scad (map #(place-row radius switch % (cube 20 20 100)) (range columns))))))

(render!)



