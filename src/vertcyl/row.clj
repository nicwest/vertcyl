(ns vertcyl.row
  (:require [scad-clj.model :refer [cube cylinder difference rotate translate
                                    union]]
            [scad-clj.scad :refer [write-scad]]
            [vertcyl.switches :refer [cutter padding]]))

(def thickness 3)

(defn single-hole
  [width height switch a x y]
  (difference 
    (->> (cube height thickness (+ width 1))
         (rotate a [0 0 -1])
         (translate [x y 0]))
    (->> (cutter switch)
         (rotate (/ Math/PI 2) [1 0 0])
         (rotate a [0 0 -1])
         (translate [x y 0]))))

(defn place-hole
  [f radius step switch width height n ]
  (let [a (* step n)
        x (* (Math/sin a) (- radius thickness))
        y (* (Math/cos a) (- radius thickness))]
    (f width height switch a x y)))

(defn get-column-step
  [radius switch]
  (let [[_ height] (padding switch)
        circum (* Math/PI 2 radius)
        n (/ circum height) ]
    (/ (* Math/PI 2) n)))

(defn normalise-row
  [radius columns step width height block]
  (let [n (if (even? columns) columns (dec columns))
        n (/ n 2)
        a (* step n)
        x (* (Math/sin a) (- radius thickness))
        y (* (Math/cos a) (- radius thickness))]
    (->> block
         (translate [(- x) (- y) 0])
         (rotate (/ Math/PI 2) [1 0 0])
         (rotate a [0 -1 0])
         (rotate (/ Math/PI 2) [0 1 0])
         (rotate Math/PI [0 0 1])
         )))

(defn row-with-switches
 [radius switch columns]
  (let [[width height] (padding switch)
        step (get-column-step radius switch)]
    (->> 
      (apply union (map #(place-hole single-hole radius step switch width height %) 
                        (range columns)))
      (normalise-row radius columns step width height))))

(defn get-row-step
  [radius switch]
  (let [[width _] (padding switch)
        circum (* Math/PI 2 radius)
        n (/ circum width)]
    (/ (* Math/PI 2) n)))

(defn place-row
  [radius switch n block]
  (let [step (get-row-step radius switch)
        a (* step n)
        x (* (Math/sin a) radius)
        y (* (Math/cos a) radius)]
    (->> block
         (rotate (+ (/ Math/PI 2) a) [0 0 -1])
         (translate [x y 0])
         )))

(defn render!
  []
  (let [radius 120
        switch :mx
        [width height] (padding switch)
        step (get-column-step radius switch)
        columns 5
        depth 15
        n 4 ]
    (spit "out/row-with-switches.scad"
          (write-scad (row-with-switches radius switch columns)))
    (spit "out/row-single-hole.scad"
          (write-scad (single-hole radius step switch width height n)))))

(render!)
