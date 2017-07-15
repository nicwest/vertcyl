(ns vertcyl.switch
  (:require [scad-clj.model :refer [cube difference rotate translate union]]
            [vertcyl.utils :refer [render!]]))

(def mx-base-width 14)
(def mx-base-hieght 14)

(def cutter-height 14)
(def thickness 2)
(def plate-width 21)
(def plate-height 22)

(def mx-cutter
  (let [extra (cube 3.5 15.6 cutter-height)
        extra (union 
                (translate [4 0 0] extra)
                (translate [-4 0 0] extra))]
    (union
      (cube mx-base-width mx-base-width cutter-height)
      extra
      (rotate (/ Math/PI 2) [0 0 1] extra))))

(def mx-plate
    (cube plate-width plate-height thickness))

(defn render-switches!
  []
  (render! "mx-cutter" mx-cutter)
  (render! "mx-switch" mx-plate))

(render-switches!)
