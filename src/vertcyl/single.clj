(ns vertcyl.single
  (:require [scad-clj.model :refer [cube cylinder difference rotate translate
                                    union scale hull *fn* sphere]]
            [scad-clj.scad :refer [write-scad]]))

(def width 21)
(def height 21)
(def radius 110)
(def plate-thickness 3)
(def wall-thickness 3)
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
  (union
    (difference
      shell-block
      plate-cutter
      switch-cutter)
    (->> (cylinder 0.5 15)
         (translate [0 (+ (/ plate-thickness 2) radius) 0])
         (rotate (/ Math/PI 2) [0 1 0])
         )))

(def single-thumb-plate
  (difference
    (scale [3/2 1 1] shell-block)
    plate-cutter
    switch-cutter))

(defn place-switch
  [row column block]
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

(def shell
  ;(union 
    
    
  (difference
    (union
      (difference 
        (->> (difference
               (sphere (+ (* radius 2) plate-thickness ))
               (sphere (* radius 2)))
             (scale [1 1 0.75]))
        (->> (sphere (+ (* radius 2) plate-thickness))
             (scale [1 1 0.75])
             (rotate (/ Math/PI 3) [0 0 -1])
             (rotate (/ Math/PI 6) [0 1 0])
             (translate [-240 150 115])))
      (difference
        (->> (difference
               (sphere (+ (* radius 2) plate-thickness))
               (sphere (* radius 2)))
             (scale [1 1 0.75])
             (rotate (/ Math/PI 3) [0 0 -1])
             (rotate (/ Math/PI 6) [0 1 0])
             (translate [-240 150 115]))
        (->> (sphere (+ (* radius 2)))
             (scale [1 1 0.75])))

    (->> (cube shell-thickness 150 200)
         (translate [-79 240 0]))
    (->> (cube 150 shell-thickness 200)
         (translate [-20 204 0]))
    )
    (->> (sphere (* radius 2))
         (scale [1 1 0.75])
         (rotate (/ Math/PI 3) [0 0 -1])
         (rotate (/ Math/PI 6) [0 1 0])
         (translate [-240 150 115]))

    (->> (sphere (* radius 2))
         (scale [1 1 0.75]))

    (->> (cube (* radius 4) (* radius 6) (* radius 6))
         (translate [-300 150 115]))
    (->> (cube (* radius 6) (* radius 6) (* radius 6))
         (translate [0 -127 115]))
    (->> (cube (* radius 6) (* radius 6) (* radius 6))
         (translate [293 0 384]))
    (->> (cube (* radius 6) (* radius 6) (* radius 6))
         (translate [374 0 0]))
    (->> (cube (* radius 6) (* radius 6) (* radius 3))
         (translate [0 0 -219]))
    (->> (cube (* radius 6) (* radius 6) (* radius 6))
         (translate [0 616 0]))
    (->> (cube (* radius 4) (* radius 4.35) (* radius 6))
         (rotate (/ Math/PI 3) [0 0 -1])
         (rotate (/ Math/PI 6) [0 1 0])
         (translate [0 0 419]))
    (->> (cube (* radius 6) (* radius 6) (* radius 6))
         (rotate (/ Math/PI 3) [0 0 -1])
         (rotate (/ Math/PI 6) [0 1 0])
         (translate [0 723 0]))
         )
  )
;)

(def shell-cutter
  (union plate-cutter switch-cutter))

(def finger-cutter
  (union
    (for [row (range (- (/ finger-rows 2)) (/ finger-rows 2))
          column (range (- (/ finger-columns 2)) (/ finger-columns 2))]
      (place-switch row column shell-cutter))))

(def thumb-cutter
  (union
    (for [row (range (- (/ thumb-vert-rows 2)) (/ thumb-vert-rows 2))]
      (place-switch row 1.50 shell-cutter))
      (place-switch -1 0.5 shell-cutter)
      (place-switch 0 0.5 shell-cutter)))

(def hand
  ;(union
    ;(->> fingers-plate
         ;;(rotate (/ Math/PI 64) [0 -1 0]))
         ;)
    ;(->> thumb-plate
         ;(rotate (/ Math/PI 3) [0 0 -1])
         ;(rotate (/ Math/PI 6) [0 1 0])
         ;(translate [-240 150 115]))
    (difference
      shell
    finger-cutter
    (->> thumb-cutter
         (rotate (/ Math/PI 3) [0 0 -1])
         (rotate (/ Math/PI 6) [0 1 0])
         (translate [-240 150 115]))


    )
   )
  ;)
;)


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
