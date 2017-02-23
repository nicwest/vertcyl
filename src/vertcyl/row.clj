(ns vertcyl.row
  (:require [scad-clj.model :refer [cube cylinder difference rotate translate
                                    union]]
            [scad-clj.scad :refer [write-scad]]
            [vertcyl.switches :refer [cutter padding]]))

(def thickness 3)

(defn single-hole
  [radius step switch width height n]
  (let [a (* step n)
        x (* (Math/sin a) (- radius thickness))
        y (* (Math/cos a) (- radius thickness))]
      (difference 
        (->> (cube height thickness (+ width thickness thickness))
             (rotate a [0 0 -1])
             (translate [x y 0]))
        (->> (cutter switch)
             (rotate (/ Math/PI 2) [1 0 0])
             (rotate a [0 0 -1])
             (translate [x y 0])))))

(defn single-cutter
  [radius depth step width height n]
  (let [ a (* step n)
        x (* (Math/sin a) radius)
        y (* (Math/cos a) radius)]
      (union 
        (->> (cube height (+ depth radius) width)
             (translate [0 (/ (+ depth radius) 2) 0])
             (rotate a [0 0 -1]))
        (->> (cube radius (+ depth thickness) (- width thickness))
             (translate [0 (/ (+ depth thickness) 2) 0])
             (rotate a [0 0 -1])
             (translate [x y 0]))
        (->> (cube (- height thickness) (+ depth thickness) radius)
             (translate [0 (/ (+ depth thickness) 2) 0])
             (rotate a [0 0 -1])
             (translate [x y 0]))
        )))

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
      (apply union (map #(single-hole radius step switch width height %) 
                        (range columns)))
      (normalise-row radius columns step width height))))

(defn row-cutter
 [radius depth switch columns]
  (let [[width height] (padding switch)
        step (get-column-step radius switch)]
    (->>
      (apply union (map #(single-cutter radius depth step width height %) 
                        (range columns)))
      (normalise-row radius columns step width height))))

(defn get-row-step
  [radius switch]
  (let [gap 5
        [width _] (padding switch)
        circum (* Math/PI 2 radius)
        n (/ circum (+ gap width))]
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
          (write-scad (single-hole radius step switch width height n)))
    (spit "out/row-single-cutter.scad"
          (write-scad (single-cutter radius depth step width height n)))
    (spit "out/row-cutter.scad"
          (write-scad (row-cutter radius depth switch columns)))))

(render!)
