(ns vertcyl.fingers
  (:require [scad-clj.model :refer [difference rotate scale translate union
                                    hull]]
            [vertcyl.switch :refer [mx-cutter mx-plate plate-height
                                    plate-width]]
            [vertcyl.utils :refer [render! two-way-range]]))

(def rows 5)
(def columns 5)
(def y-offset 2)
(def z-offset 4)
(def x-offset (+ plate-width 1))
(def radius 90)
(def cirum (* Math/PI 2 radius))
(def step (/ (* Math/PI 2) (/ cirum plate-height)))


(defn place
  [row column block]
  (let [a (* row step)
        b (* (/ Math/PI 64) (- column))
        x 0
        y (* (Math/sin a) radius)
        z (- (* (Math/cos a) radius))
        x-offset (* x-offset column)
        y-offset (* y-offset column)
        y-offset (if (> column 0) (- y-offset) y-offset)
        z-offset (* z-offset column)
        z-offset (if (< column 0) (- z-offset) z-offset)
        z-offset (+ z-offset radius)
        ]

    (->> block
         (rotate b [0 1 0])
         (rotate a [1 0 0])
         (translate [(+ x x-offset) (+ y y-offset) (+ z z-offset)]))))

(defn grid-keys
  [block]
  (union
    (for [row (two-way-range rows)
          column (two-way-range columns)]
      (place row column block))))

(defn bind-keys
  [block]
  (union
  (let [cut-block (scale [1 2 2] block)
        this-block (difference block (translate [-1 0 0] cut-block))
        next-block (difference block (translate [1 0 0] cut-block))]
    (for [row (two-way-range rows)
          column (drop-last (two-way-range columns))]
      (hull
        (place row column this-block)
        (place row (+ column 1) next-block))))

  (let [cut-block (scale [2 1 2] block)
        this-block (difference block (translate [0 -1 0] cut-block))
        next-block (difference block (translate [0 1 0] cut-block))]
    (for [row (drop-last (two-way-range rows))
          column (two-way-range columns)]
      (hull
        (place row column this-block)
        (place (+ row 1) column next-block))))))

;(def switch-support
;  (union
;    (let [rod ])
;    ))

(def fingers-uncut
  (union
    (grid-keys mx-plate)
    (bind-keys mx-plate)
    ))

    
(def fingers
  (difference
    fingers-uncut
    (grid-keys mx-cutter)))

(defn render-fingers!
  []
  (render! "fingers-grid-keys" (grid-keys mx-plate))
  (render! "fingers-bind-keys" (bind-keys mx-plate))
  (render! "fingers-uncut" fingers-uncut)
  (render! "fingers" fingers))

(render-fingers!)
