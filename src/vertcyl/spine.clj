(ns vertcyl.spine
  (:require [scad-clj.model :refer [difference sphere cube translate]]
            [vertcyl.switch :refer [thickness]]
            [vertcyl.utils :refer [render!]]
            ))

(def radius 100)
(def inner-radius (- radius thickness))
(def circum (* radius 2))
(def offset (/ thickness 2))
;(def offset 10)

(def center
  (->> (difference
         (sphere radius)
         (sphere inner-radius)
         (->> (cube circum circum circum)
              (translate [(+ radius offset) 0 0]))
         (->> (cube circum circum circum)
              (translate [(- 0 radius offset) 0 0]))
         (->> (cube circum circum circum)
              (translate [0 0 (- radius)]))
         (->> (cube circum circum circum)
              (translate [0  (- radius) 0])))
       (translate [0 (/ radius -2) (/ radius -2)])))


(defn render-spine!
  []
  (render! "spine-center" center))

(render-spine!)
