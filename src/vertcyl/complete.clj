(ns vertcyl.complete
  (:require [scad-clj.model :refer [rotate scale translate union difference 
                                    hull]]
            [vertcyl.fingers :as fingers]
            [vertcyl.thumbs :as thumbs]
            [vertcyl.switch :as switch]
            [vertcyl.spine :refer [center parts]]
            [vertcyl.utils :refer [render!]]))

(def finger-offset 40)
(def thumb-offset-y -50)
(def thumb-offset-z 25)
(def half-offset 10)

(defn place
  [block]
  (->> block
         (rotate (/ Math/PI 2.25) [0 1 0])
         (translate [finger-offset 0 0])))

(def shell
  (let [[p1 p2 p3 p4 p5 p6 p7] (map #(translate [(- half-offset) 0 0] %) parts)
        block  switch/mx-plate
        side-cut-block (scale [2 1 2] block)
        top (->> block
                 (scale [1 2 2])
                 (translate [1 0 0])
                 (difference block))
        top-stub (translate [0 0 -10] top)
        side (->> block
                  (scale [2 1 2])
                  (translate [0 -1 0])
                  (difference block))
        side-stub (translate [0 0 -10] side)]
    (union
      (hull
        (place (fingers/place -2 -2 top))
        (place (fingers/place -2 -2 top-stub)))
      (hull
        (place (fingers/place -1 -2 top))
        (place (fingers/place -1 -2 top-stub)))
      (hull
        (place (fingers/place 0 -2 top))
        (place (fingers/place 0 -2 top-stub)))
      (hull
        (place (fingers/place 1 -2 top))
        (place (fingers/place 1 -2 top-stub)))
      (hull
        (place (fingers/place 2 -2 top))
        (place (fingers/place 2 -2 top-stub)))
      (hull
        (place (fingers/place 2 -2 side))
        (place (fingers/place 2 -2 side-stub)))
      (hull
        (place (fingers/place 2 -1 side))
        (place (fingers/place 2 -1 side-stub)))
      (hull
        (place (fingers/place 2 0 side))
        (place (fingers/place 2 0 side-stub)))
      (hull
        (place (fingers/place 2 1 side))
        (place (fingers/place 2 1 side-stub)))
      (hull
        (place (fingers/place 2 2 side))
        (place (fingers/place 2 2 side-stub)))

      (hull
        (hull
          (place (fingers/place -2 -2 top-stub))
          p1)
        (hull
          (place (fingers/place -1 -2 top-stub))
          p1))
      (hull
        (hull
          (place (fingers/place -1 -2 top-stub))
          p1)
        (hull
          (place (fingers/place 0 -2 top-stub))
          p2))
      (hull
        (hull
          (place (fingers/place 0 -2 top-stub))
          p2)
        (hull
          (place (fingers/place 1 -2 top-stub))
          p2))

      (hull
        (hull
          (place (fingers/place 1 -2 top-stub))
          p2)
        (hull
          (place (fingers/place 2 -2 top-stub))
          p3))

      (hull
        (hull
          (place (fingers/place 2 -2 top-stub))
          p3)
        (hull
          (place (fingers/place 2 -2 side-stub))
          p4))

      (hull
        (hull
          (place (fingers/place 2 -2 side-stub))
          p4)

        (hull
          (place (fingers/place 2 -1 side-stub))
          p4))
      (hull
        (hull
          (place (fingers/place 2 -1 side-stub))
          p4)

        (hull
          (place (fingers/place 2 0 side-stub))
          p5))
      (hull
        (hull
          (place (fingers/place 2 0 side-stub))
          p5)
        (hull
          (place (fingers/place 2 1 side-stub))
          p5))
      (hull
        (hull
          (place (fingers/place 2 1 side-stub))
          p5)
        (hull
          (place (fingers/place 2 2 side-stub))
          p6))

      (hull
        (hull
          (place (fingers/place 2 2 side-stub))
          p6)
        (hull
          (place (fingers/place 2 2 side-stub))
          p7))
      )))

(def complete-half
  (union
    (->> (union fingers/fingers thumbs/thumbs)
         (place))
  shell))

(def complete
  (union
    (->> complete-half
         (translate [half-offset 0 0 ]))
    (->> complete-half
         (scale [-1 1 1])
         (translate [(- half-offset) 0 0 ]))
    ))

(defn render-complete!
  []
  (render! "complete-half" complete-half)
  (render! "complete" complete))

(render-complete!)
