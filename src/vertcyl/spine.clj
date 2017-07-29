(ns vertcyl.spine
  (:require [scad-clj.model :refer [difference sphere cube translate union]]
            [vertcyl.switch :refer [thickness]]
            [vertcyl.utils :refer [render!]]
            ))

(def radius 97)
(def inner-radius (- radius thickness))
(def circum (* Math/PI radius 2))
(def step (/ circum 24))
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

(def parts
  (let [block (cube thickness thickness thickness)]
      (for [n (range 7)]
        (let [a (* n step)
              y (* (Math/sin a) radius)
              z (* (Math/cos a) radius)]
          (->> block
               (translate [0 y z])
               (translate [0 (/ radius -2) (/ radius -2)]))))))


(defn render-spine!
  []
  (render! "spine-center" center)
  (render! "spine-parts" parts))

(render-spine!)
