(ns vertcyl.core
  (:require [scad-clj.model :refer [union rotate translate sphere cube 
                                    cylinder
                                    difference scale]]
            [scad-clj.scad :refer [write-scad]]
            [vertcyl.row :refer [row-with-switches place-row normalise-row
                                 thickness]]
            )
  (:gen-class))


(defn row-set
  [radius switch columns rows]
  (->> (map 
         #(->> (row-with-switches radius switch columns)
               (place-row radius switch %))
         (range rows))
       (apply union)))
       ;(rotate (/ Math/PI 4) [0 0 -1])))



(def complete
  (let [radius 120
        switch :mx
        columns 5
        rows 4
        gap 30]
    (union
      (difference
        (union
          (->> (row-set radius switch columns rows))
          (->> (difference
                 (cube 86 50 105)
                 (cube 80 44 99)

                 )
               (translate [31 100 0])
               ))
          ))))




(defn render!
  []
  (spit "out/complete.scad"
        (write-scad complete)))

(render!)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (render!))
