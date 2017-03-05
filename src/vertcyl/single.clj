(ns vertcyl.single
  (:require [scad-clj.model :refer [cube cylinder difference rotate translate
                                    union scale hull *fn*]]
            [scad-clj.scad :refer [write-scad]]))

(def width 22)
(def height 22)
(def radius 140)
(def plate-thickness 3)
(def wall-thickness 5.5)
(def wall-height 10)
(def shell-width (+ width wall-thickness))
(def shell-height (+ height wall-thickness))
(def shell-thickness 3)
(def diameter (* radius 2))
(def finger-rows 4)
(def finger-columns 5)
(def thumb-vert-rows 3)
(def thumb-hori-rows 2)
(def cirum (* Math/PI 2 radius))
(def ^:dynamic *step* (/ (* Math/PI 2) (/ cirum width)))
(def ^:dynamic *offset* (/ *step* 2))
(def body-thickness 40)
(def body-angle (/ Math/PI 9))

(def switch-cutter
  (let [extra (cube 3.5 15.6 radius)
        angle (/ Math/PI 2)]
    (->> (union
           (cube 14 14 radius)
           (translate [4.0 0 0] extra)
           (translate [-4.0 0 0] extra)
           (->> extra
                (rotate angle [0 0 1])
                (translate [0 4 0]))
           (->> extra
                (rotate angle [0 0 1])
                (translate [0 -4 0])))
         (translate [0 0 (+ (/ radius 2) (* 2 shell-thickness))])
         (rotate angle [-1 0 0]))))

(def plate-cutter
  (->> (cube width height radius)
       (translate [0 0 (/ radius 2)])
       (rotate (/ Math/PI 2) [-1 0 0])))

(def shell-block
  (->> (cube shell-width shell-height shell-thickness)
       (translate [0 0 (+ (/ shell-thickness 2)
                          radius)])
       (rotate (/ Math/PI 2) [-1 0 0])))


(def single-finger-plate
  (difference
    shell-block
    plate-cutter
    switch-cutter))

(def single-thumb-plate
  (difference
    (scale [3/2 1 1] shell-block)
    plate-cutter
    switch-cutter))

(defn place-switch
  [row column block]
  (println *step*)
  (let [a (+ *offset* (* column *step*))
        b (+ (/ *offset* 2) (* row *offset*))
        x (* (Math/sin a) radius)
        y (* (Math/cos a) radius)]
    (->> block
         (translate [x y 0])
         (rotate a [0 0 1])
         (rotate (/ Math/PI 2) [0 1 0])
         (rotate b [0 0 1])
         )))

(def fingers-plate
  (union
    (for [row (range (- (/ finger-rows 2)) (/ finger-rows 2))
          column (range (- (/ finger-columns 2)) (/ finger-columns 2))]
      (place-switch row column single-finger-plate))))

(def thumb-plate
  (union
    (for [row (range (- (/ thumb-vert-rows 2)) (/ thumb-vert-rows 2))]
      (place-switch row 1.50 single-finger-plate))
      (place-switch -1 0.5 single-finger-plate)
      (place-switch 0 0.5 single-finger-plate)))

(def hand
  (union
    (->> fingers-plate
         (rotate (/ Math/PI 64) [0 -1 0]))
    (->> thumb-plate
         (rotate (/ Math/PI 3) [0 0 -1])
         (rotate (/ Math/PI 6) [0 1 0])
         (translate [-280 160 155])
         )))


(defn render-part!
  [[filename part]]
  (spit (str "out/" filename ".scad")
        (write-scad part)))

(defn render!
  []
  (dorun 
    (map render-part!
         {"switch-cutter" switch-cutter
          "plate-cutter" plate-cutter
          "shell-block" shell-block
          "single-finger-plate" single-finger-plate
          "single-thumb-plate" single-thumb-plate
          "fingers-plate" fingers-plate
          "thumb-plate" thumb-plate
          "hand" hand
          })))


(render!)
