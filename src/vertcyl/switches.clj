(ns vertcyl.switches
  (:require [scad-clj.model :refer [cube rotate translate union]]
            [scad-clj.scad :refer [write-scad]]))

(def mx-cutter
  (let [extra (cube 3.5 15.6 15.6)
        angle (/ Math/PI 2)]
    (union
        (cube 14 14 14)
        (translate [4.0 0 0] extra)
        (translate [-4.0 0 0] extra)
        (->> extra
             (rotate angle [0 0 1])
             (translate [0 4 0]))
        (->> extra
             (rotate angle [0 0 1])
             (translate [0 -4 0])))))

(def matias-cutter
  (cube  12.8 15.5 15.6))

(defn grid
  [radius & {:keys [columns rows offset-columns offset-rows side]}]
  (let [;col spacing
        max-a (/ Math/PI 2)
        min-a  0
        da (- max-a min-a)
        ;column spacing
        max-b (/ Math/PI 2)
        min-b (/ Math/PI 8)
        db (- max-b min-b)
        icolumn (/ da columns)
        icol (/ db rows)]
    (for [column (range columns)
          col (range rows)]
      (let [a (* icolumn (+ column offset-columns))
            b (* icol (+ col offset-rows))
            x (* (Math/sin b) (Math/cos a) radius)
            y (* (Math/sin a) (Math/sin b) radius)
            z (* (Math/cos b) radius)]
        [x y z a b]))))

(defn place-cutter
  [cutter [x y z ax az]]
  (->> cutter
       (rotate az [0 1 0])
       (rotate ax [0 0 1])
       (translate [x y z])))

(defn grid-cutter
  [cutter-type]
  (let [points (grid 85 
                     :rows 5 
                     :columns 6 
                     :offset-rows 4 
                     :offset-columns 0.5 
                     :side :left)
        cutter (cond 
                 (= cutter-type :matias)
                 matias-cutter
                 :else
                 mx-cutter)]
    (apply union (map (partial place-cutter cutter) points))))

(defn render!
  []
  (spit "mx-cutter.scad"
        (write-scad mx-cutter))
  (spit "matias-cutter.scad"
        (write-scad matias-cutter))
  (spit "grid-test.scad"
        (write-scad (grid-cutter :mx))))

(render!)
