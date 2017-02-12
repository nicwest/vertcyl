(ns vertcyl.shell
  (:require [scad-clj.model :refer [cube difference rotate scale sphere
                                    translate union]]
            [scad-clj.scad :refer [write-scad]]
            [vertcyl.switches :refer [grid-cutter]]))

(def plate-thickness 2)

(def wall-height 7)
(def wall-thickness 4)

(defn key-plate
  [radius switch-type]
  (let [outer (sphere radius)
        inner (sphere (- radius plate-thickness))
        r2 (* radius 2)
        cutter (cube r2 r2 r2)
        back-cutter (->> cutter
                         (translate [(- radius) 0 0]))
        side-cutter (->> cutter
                         (translate [0 (- radius) 0]))
        top-cutter (->> cutter
                        (translate [0 (+ radius (* radius 2/5))]))
        bottom-cutter (->> cutter
                        (translate [0 (- 0 radius (* radius 5/7))]))
        plate (difference
                   outer
                   inner
                   (grid-cutter radius switch-type)
                   back-cutter
                   side-cutter)
        plate (rotate (/ Math/PI 2) [1 0 0] plate)]
    (difference
      plate
      top-cutter
      bottom-cutter)))

(defn front-plate
  [radius switch-type]
  (union
    (->> (cube radius radius radius)
         (rotate (/ Math/PI 2) [0 1 0])
         (rotate (/ Math/PI 8) [0 0 -1])
         (translate [0 (+ radius (* radius 1/6)) 0])
         )
    (->> (key-plate radius switch-type)
         (rotate (/ Math/PI 2) [0 1 0])
         (rotate (/ Math/PI 8) [0 0 -1])
         (translate [0 (+ radius (* radius 1/6)) 0])
         )
    (->> (key-plate radius switch-type)
         (rotate (/ Math/PI 2) [0 1 0])
         (rotate (/ Math/PI 8) [0 0 -1])
         (scale [1 -1 1])
         (translate [0 (- 0 radius (* radius 1/6)) 0])
         )))

    
(defn render! 
  []
  (spit "out/key-plate.scad"
        (write-scad (key-plate 90 :mx)))
  (spit "out/front-plate.scad"
          (write-scad (front-plate 90 :mx))))

(render!)
