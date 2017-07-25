(ns vertcyl.thumbs
  (:require [scad-clj.model :refer [difference translate union]]
            [vertcyl.switch :refer [mx-cutter mx-plate plate-height
                                    plate-width]]
            [vertcyl.utils :refer [render!]]))


(defn place
  [row column block]
  (let [x (* plate-width row)
        y (* plate-height column)
        z 0]

    (->> block
         (translate [x y z]))))


(defn grid-keys
  [block]
  (union
    (place -0.5 1 block)
    (place 0.5 1 block)
    (place -1 0 block)
    (place 0 0 block)
    (place 1 0 block)
    (place -0.5 -1 block)
    (place 0.5 -1 block)
    ))

(def thumbs
  (difference
    (grid-keys mx-plate)
    (grid-keys mx-cutter)))

(defn render-thumbs!
  []
  (render! "thumbs" thumbs))

(render-thumbs!)
