(ns vertcyl.complete
  (:require [scad-clj.model :refer [rotate scale translate union]]
            [vertcyl.fingers :refer [fingers]]
            [vertcyl.thumbs :refer [thumbs]]
            [vertcyl.spine :refer [center]]
            [vertcyl.utils :refer [render!]]))

(def finger-offset 40)
(def thumb-offset-y -50)
(def thumb-offset-z 25)
(def half-offset 20)

(def complete-half
  (union
    (->> (union fingers thumbs)
         (rotate (/ Math/PI 2.25) [0 1 0])
         (translate [finger-offset 0 0]))))

(def complete
  (union
    (->> complete-half
         (translate [half-offset 0 0 ]))
    (->> complete-half
         (scale [-1 1 1])
         (translate [(- half-offset) 0 0 ]))
    center
    ))

(defn render-complete!
  []
  (render! "complete-half" complete-half)
  (render! "complete" complete))

(render-complete!)
