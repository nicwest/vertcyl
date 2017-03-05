(ns vertcyl.single
  (:require [scad-clj.model :refer [cube cylinder difference rotate translate
                                    union]]
            [scad-clj.scad :refer [write-scad]]))

(def width 22)
(def height 22)
(def radius 140)
(def plate-thickness 3)
(def wall-thickness 5.5)
(def wall-height 10)
(def shell-width (+ width wall-thickness))
(def shell-height (+ height wall-thickness))
(def shell-thickness 30)
(def diameter (* radius 2))
(def rows 4)
(def columns 5)
(def cirum (* Math/PI 2 radius))
(def step (/ (* Math/PI 2) (/ cirum width)))
(def offset (/ step 2))

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
         (translate [0 0 (+ (/ radius 2) (* 2 plate-thickness))])
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

(def shell-cutter
  (->> 
    (union
      (cube width height shell-thickness)
      (cube (- width wall-height)
            (+ height wall-height)
            shell-thickness)
      (cube (+ width wall-height)
            (- height wall-height)
            shell-thickness))
    (translate [0 0 (+ (/ shell-thickness 2)
                       radius
                       plate-thickness)])
    (rotate (/ Math/PI 2) [-1 0 0])))

(def single-switch
  (difference
    shell-block
    shell-cutter
    plate-cutter
    switch-cutter))

(defn place-switch
  [row column block]
  (let [a (+ offset (* column step))
        b (+ (/ offset 2) (* row offset))
        x (* (Math/sin a) radius)
        y (* (Math/cos a) radius)]
    (->> block
         (translate [x y 0])
         (rotate a [0 0 1])
         (rotate (/ Math/PI 2) [0 1 0])
         (rotate b [0 0 1])
         )))

(def single-uncut
  (union
    (for [row (range (- (/ rows 2)) (/ rows 2))
          column (range (- (/ columns 2)) (/ columns 2))]
      (place-switch row column single-switch))))

(def single-cut
  (difference
    single-uncut
    (->> 
      (cube radius radius radius)
      (translate [0 (+ (* radius 5/2)
                       plate-thickness)]))))

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
          "shell-cutter" shell-cutter
          "single-switch" single-switch
          "single-uncut" single-uncut
          "single-cut" single-cut
          })))


(render!)
