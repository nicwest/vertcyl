(ns vertcyl.thumbs
  (:require [scad-clj.model :refer [difference rotate union scale hull 
                                    translate]]
            [vertcyl.fingers :as fingers]
            [vertcyl.switch :refer [mx-cutter mx-plate plate-height 
                                    plate-width]]
            [vertcyl.utils :refer [render! two-way-range]]))

(def radius 90)
(def cirum (* Math/PI 2 radius))
(def step (/ (* Math/PI 2) (/ cirum plate-height)))

(defn place
  [column finger-row finger-column block]
  (let [z-offset (* column plate-width -1)
        y-offset 0
        ;y-offset (- radius)
        a (* column step)
        x 0
        y 0
        z 0
        ;y (* (Math/cos a) radius)
        ]
    (->> block
         (rotate (/ Math/PI 2) [1 0 0])
         ;(rotate a [-1 0 0])
         (translate [x (+ y y-offset) (+ z z-offset)])
         (fingers/place finger-row finger-column))))


(defn grid-keys
  [block]
  (let [
        finger-row (/ fingers/rows -2)
        finger-column-one (- (/ fingers/columns -2) -1.5)
        finger-column-two (+ finger-column-one 1) 
        ]
    (union
      (place 1 finger-row finger-column-one block)
      (place 2 finger-row finger-column-one block)
      (place 3 finger-row finger-column-one block)
      (place 1 finger-row finger-column-two block)
      (place 2 finger-row finger-column-two block)
      (place 3 finger-row finger-column-two block))))

(defn bind-keys
  [block]
  (let [cut-block (scale [1 2 2] block)
        top-block (difference block (translate [-1 0 0 ] cut-block))
        bottom-block (difference block (translate [1 0 0] cut-block))
        finger-row (/ fingers/rows -2)
        finger-column-one (- (/ fingers/columns -2) -1.5)
        finger-column-two (+ finger-column-one 1)]

    (union
      (for [column (range 1 4)]
        (hull
          (place column finger-row finger-column-one top-block)
          (place column finger-row finger-column-two bottom-block))))))

(def thumbs
  (difference
  (union
    (grid-keys mx-plate)
    (bind-keys mx-plate))
    (grid-keys mx-cutter)))

(defn render-thumbs!
  []
  (render! "thumbs" thumbs))

(render-thumbs!)
