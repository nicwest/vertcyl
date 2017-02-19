(ns vertcyl.row
  (:require [scad-clj.model :refer [cube cylinder difference rotate translate
                                    union]]
            [scad-clj.scad :refer [write-scad]]
            [vertcyl.switches :refer [cutter padding]]))

(def thickness 3)

(defn material
  [radius switch]
  (let [[width _] (padding switch)]

    (difference
      (cylinder radius width)
      (cylinder (- radius thickness) (+ width thickness)))))

(defn get-step
  [radius switch]
  (let [[_ height] (padding switch)
        circum (* Math/PI 2 radius)
        n (/ circum height) ]
    (/ (* Math/PI 2) n)))

(defn cutters
  [radius switch columns]
  (let [step (get-step radius switch)
        offset (/ step 2)
        placer (fn [n]
                 (let [a (+ offset (* step n))]
                   (->> (cutter switch)
                        (rotate (/ Math/PI 2) [1 0 0])
                        (rotate a [0 0 -1])
                        (translate [(* (Math/sin a) radius)
                                    (* (Math/cos a) radius)
                                    0]))))]
    (map placer (range columns))))

(defn add-switch-holes
  [radius switch columns block]
    (apply difference block (cutters radius switch columns)))

(defn trimmer-block
  [radius switch n]
  (let [[width height] (padding switch)
        step (get-step radius switch)
        offset (/ step 2)
        a (+ offset (* step n))]
    (->> (cube width (+ radius thickness) (+ height thickness))
         (translate  [0 (/ radius 2) 0])
         (rotate a [0 0 -1]))))

(defn trimmer 
  [radius switch columns]
  (let [[width _] (padding switch)]
  (difference
    (cylinder (+ thickness radius) (+ thickness width))
    (apply union
           (map #(trimmer-block radius switch %) (range columns))))))

(defn trim
  [radius switch columns block]
  (difference
    block
    (trimmer radius switch columns)))


(defn row-with-switches
 [radius switch columns]
 (->> (material radius switch)
      (add-switch-holes radius switch columns)
      (trim radius switch columns)))

(defn render!
  []
  (spit "out/row-material.scad"
        (write-scad (material 90 :mx)))
  (spit "out/row-cutters.scad"
        (write-scad (cutters 90 :mx 5)))
  (spit "out/row-with-switches.scad"
        (write-scad (row-with-switches 90 :mx 2))))

(render!)
