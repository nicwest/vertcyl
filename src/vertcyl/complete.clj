(ns vertcyl.complete
  (:require [scad-clj.model :refer [rotate scale translate union]]
            [vertcyl.fingers :refer [fingers]]
            [vertcyl.thumbs :refer [thumbs]]
            [vertcyl.utils :refer [render!]]))

(def finger-offset 40)
(def thumb-offset-y -50)
(def thumb-offset-z 25)
(def half-offset 30)

(def complete-half
  (union
    (->> fingers
         (rotate (/ Math/PI 2.25) [0 1 0])
         (translate [finger-offset 0 0]))
    (->> thumbs
         (rotate (/ Math/PI 2.5) [1 0 0])
         (rotate (/ Math/PI 12) [0 0 1])
         (translate [0 thumb-offset-y thumb-offset-z])
         )))

(def complete
  (union
    (->> complete-half
         (translate [half-offset 0 0 ]))
    (->> complete-half
         (scale [-1 1 1])
         (translate [(- half-offset) 0 0 ]))))

(defn render-complete!
  []
  (render! "complete" complete))

(render-complete!)
