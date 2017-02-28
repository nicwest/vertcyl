(ns vertcyl.single
  (:require [scad-clj.model :refer [cube cylinder difference rotate translate
                                    union]]
            [scad-clj.scad :refer [write-scad]]))



(def width 22)
(def height 22)

(def switch-cutter
  (let [extra (cube 3.5 15.6 15.6)
        angle (/ Math/PI 2)]
    (union
        (cube 14 14 14)
        (translate [4.0 0 0] extra)
        (translate [-4.0 0 0] extra)
        (->> extra
             (rotate angle [0 0 1])
             (translate [0 4 0]))
        (->> extra
             (rotate angle [0 0 1])
             (translate [0 -4 0])))))


(def radius 120)
(def diameter (* radius 2))
(def rows 4)
(def columns 5)
(def thickness 5)


(def cirum (* Math/PI 2 radius))
(def step (/ (* Math/PI 2) (/ cirum width)))
(def offset (/ step 2))


(def switch-plate
  (union
    (->> (cylinder 2/3 15)
         (rotate (/ Math/PI 2) [0 1 0]))
    (difference
      (cube (+ width 2) (+ height 1) thickness)
      switch-cutter)))

(def shell-plate
  (->> (cube (+ width 7) (+ height 7) (* thickness 6))
       (translate [0 0 (- (* thickness 3))])))

(defn on-sphere
  [n]
  (let [a (+ (* step n) offset)
        x (* (Math/sin a) radius)
        y (* (Math/cos a) radius)]
    [a x y]))

(defn row-generator
  [block]
  (apply union 
         (for [column (range (- (/ columns 2)) (/ columns 2))]
           (let [[a x y] (on-sphere column)]
             (->> block
                  (rotate (/ Math/PI 2) [1 0 0])
                  (rotate a [0 0 -1])
                  (translate [x y 0]))))))


(def switch-row
  (row-generator switch-plate))

(def shell-row
  (row-generator shell-plate))


(defn set-generator
  [block]
  (apply union
         (for [row (range (- (/ rows 2)) (/ rows 2))]
           (let [[a z _] (on-sphere row)]
             (->> block
                  (rotate (/ Math/PI 2) [0 1 0])
                  (rotate a [0 0 -1]))))))

(def switch-set
  (set-generator switch-row))

(def shell-set
  (set-generator shell-row))

(def trimmed-shell
  (difference
    shell-set
    (->> (cube (* radius 2) (* radius 2) (* radius 2))
         (translate [0 radius (* radius 34/23)]))
    (->> (cube (* radius 2) (* radius 2) (* radius 2))
         (translate [0 radius (- (* radius 34/23))]))
    (->> (cube (* radius 2) (* radius 2) (* radius 2))
         (translate [(* radius 15/11) radius 0]))
    (->> (cube (* radius 2) (* radius 2) (* radius 2))
         (translate [(- (* radius 15/11)) radius 0]))
    (->> (cube (* radius 2) (* radius 2) (* radius 2))
         (translate [0 (+ (* radius 2) thickness) 0]))
    ))


(def walled-shell
  (let [shell-width (/ (+ (* radius 34/23) (- (* 2 thickness))) 2)
        shell-height (* radius 11/12)]
    (difference
      trimmed-shell
      (->> (cube shell-width radius shell-height)
           (translate [0 radius 0])))))


(def single-complete
  (union
    switch-set
    walled-shell))

(defn render-part!
  [[filename part]]
  (spit (str "out/" filename ".scad")
        (write-scad part)))

(defn render!
  []
  (dorun 
    (map render-part!
         {"switch-cutter" switch-cutter
          "switch-plate" switch-plate
          "switch-row" switch-row
          "switch-set" switch-set
          "single-complete" single-complete
          "shell-plate" shell-plate
          "shell-row" (union switch-row shell-row)
          "shell-set" (union switch-set shell-set)
          "trimmed-shell" trimmed-shell
          "walled-shell" walled-shell})))


(render!)
