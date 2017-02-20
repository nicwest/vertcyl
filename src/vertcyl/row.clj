(ns vertcyl.row
  (:require [scad-clj.model :refer [cube cylinder difference rotate translate
                                    union]]
            [scad-clj.scad :refer [write-scad]]
            [vertcyl.switches :refer [cutter padding]]))

(def thickness 3)

(defn single-hole
  [radius step offset switch width height n]
  (let [a (+ offset (* step n))
        x (* (Math/sin a) (- radius thickness))
        y (* (Math/cos a) (- radius thickness))
        inside-width (- width (* thickness 2))
        inside-height (+ height (* thickness 2))
        inside-x (* (Math/sin a) (+ radius (/ inside-height 2) (- (* 2 thickness))))
        inside-y (* (Math/cos a) (+ radius (/ inside-height 2) (- (* 2 thickness))))
        ]
    (union
      (difference 
        (->> (cube height thickness width)
             (rotate a [0 0 -1])
             (translate [x y 0]))
        (->> (cutter switch)
             (rotate (/ Math/PI 2) [1 0 0])
             (rotate a [0 0 -1])
             (translate [x y 0])))
      (->> (difference
        (cube height width width)
        (cube inside-width inside-width inside-height))
             (rotate (/ Math/PI 2) [1 0 0])
             (rotate a [0 0 -1])
             (translate [inside-x inside-y 0]))
      
      )))

(defn get-step
  [radius switch]
  (let [[_ height] (padding switch)
        circum (* Math/PI 2 radius)
        n (/ circum height) ]
    (/ (* Math/PI 2) n)))


(defn row-with-switches
 [radius switch columns]
  (let [[width height] (padding switch)
        step (get-step radius switch)
        offset (/ step 2)]
    (apply union (map #(single-hole radius step offset switch width height %) 
                      (range columns)))))

(defn render!
  []
  (let [radius 120
        switch :mx
        [width height] (padding switch)
        step (get-step radius switch)
        offset (/ step 2)
        columns 5 
        n 4 ]
    (spit "out/row-with-switches.scad"
          (write-scad (row-with-switches radius switch columns)))
    (spit "out/row-single-hole.scad"
          (write-scad (single-hole radius step offset switch width height n)))))

(render!)
