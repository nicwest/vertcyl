(ns vertcyl.shell
  (:require [scad-clj.model :refer [cube difference rotate sphere translate 
                                    union]]
            [scad-clj.scad :refer [write-scad]]
            [vertcyl.switches :refer [grid-cutter]]))

(def plate-thickness 2)

(def wall-height 7)
(def wall-thickness 4)

(defn base-plate
  [radius switch-type]
  (let [outer (sphere radius)
        inner (sphere (- radius plate-thickness))
        r2 (* radius 2)
        cutter (cube r2 r2 r2)
        back-cutter (->> cutter
                         (translate [(- radius) 0 0])
                         (rotate (/ Math/PI 54) [0 0 1]))
        side-cutter (->> cutter
                         (translate [0 (- radius) 0])
                         (rotate (/ Math/PI 54) [0 0 -1]))
        top-cutter (->> cutter
                         (translate [0 0 0])
                         (rotate (/ Math/PI 54) [0 0 -1]))
        ]
    (union 
    (difference
      outer
      inner
      (grid-cutter radius switch-type)
      back-cutter
      side-cutter
      top-cutter
      )
    top-cutter))

  )

(defn base-plate-cutter
  [radius]
  (let [wr (+ radius wall-height 5)
        outer (sphere wr)
        inner (sphere (- radius plate-thickness))
        r2 (* radius 2)
        wr2 (* wr 2)
        cutter (cube wr2 wr2 wr2)
        back-cutter (->> cutter
                         (translate [(- wr) 0 0])
                         (rotate (/ Math/PI 54) [0 0 1]))
        side-cutter (->> cutter
                         (translate [0 (- wr) 0])
                         (rotate (/ Math/PI 54) [0 0 -1]))]
    (difference
      outer
      inner
      back-cutter
      side-cutter)))

(defn base-wall
  [radius]
  (let [wr (+ radius wall-height)
        outer (sphere wr)
        inner (sphere (- radius plate-thickness))
        wr2 (* wr 2)
        base-cutter (base-plate-cutter radius)
        cutter (cube wr2 wr2 wr2)
        back-cutter (->> cutter
                         (translate [(- wr) 0 0])
                         (rotate (/ Math/PI 18) [0 0 1]))
        side-cutter (->> cutter
                         (translate [0 (- wr) 0])
                         (rotate (/ Math/PI 18) [0 0 -1]))]
    (difference 
      outer
      inner
      base-cutter
      back-cutter
      side-cutter)))

(defn base
  [radius switch-type]
  (union
    (base-plate radius switch-type)

    ;(base-plate-cutter radius)
    ))
    

    ;(base-top-wall radius)))
    
    ;(base-wall radius)))


(defn render! 
  []
  (spit "out/base.scad"
        (write-scad (base 85 :mx))))

(render!)
