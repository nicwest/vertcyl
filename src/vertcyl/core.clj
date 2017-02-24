(ns vertcyl.core
  (:require [scad-clj.model :refer [union rotate translate sphere cube 
                                    cylinder
                                    difference scale]]
            [scad-clj.scad :refer [write-scad]]
            [vertcyl.row :refer [row-with-switches place-row normalise-row
                                 row-cutter thickness]]
            )
  (:gen-class))


(defn row-set
  [radius switch columns rows]
  (->> (map 
         #(->> (row-with-switches radius switch columns)
               (place-row radius switch %))
         (range rows))
       (apply union)
       (rotate (/ Math/PI 4) [0 0 -1])))

(defn cross-bar
  [x y z radius gap]

  (union
    (difference
      (->> (cube (* radius 2) 6 6)
           (translate [x y z]))
      (->> (sphere (+ radius thickness))
           (translate [(+ radius thickness (/ gap 2)) 0 0]))
      (->> (sphere (+ radius thickness))
           (translate [(- 0 radius thickness (/ gap 2)) 0 0]))

      (->> (cylinder 5/4 (* radius 2))
           (rotate (/ Math/PI 2) [0 1 0])
           (translate [x y z])))))

(defn screw-hole
  [x y z radius gap]
  (->> (cylinder 5/4 (* radius 2))
       (rotate (/ Math/PI 2) [0 1 0])
       (translate [x y z])))


(def complete
  (let [radius 120
        switch :mx
        columns 5
        rows 4
        gap 30
        ]
    (union
      (difference
        (union
          (->> (row-set radius switch columns rows)
               (translate [(- 0 radius thickness (/ gap 2)) 0 0]))
          (->> (row-set radius switch columns rows)
               (translate [(- 0 radius thickness (/ gap 2)) 0 0])
               (scale [-1 1 1])))

        (screw-hole 0 1 50 radius gap)
        (screw-hole 0 1 -50 radius gap)
        (screw-hole 0 89 32 radius gap)
        (screw-hole 0 89 -32 radius gap)
        )
    (cross-bar 0 1 50 radius gap)
    (cross-bar 0 1 -50 radius gap)
    (cross-bar 0 89 32 radius gap)
    (cross-bar 0 89 -32 radius gap)
      )))


(defn render!
  []
  (spit "out/complete.scad"
        (write-scad complete)))

(render!)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (render!))
