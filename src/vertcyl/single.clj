(ns vertcyl.single
  (:require [scad-clj.model :refer [cube cylinder difference rotate translate
                                    union scale hull *fn* sphere with-fn]]
            [scad-clj.scad :refer [write-scad]]))

(def width 21)
(def height 21)
(def radius 110)
(def plate-thickness 5)
(def wall-thickness 12)
(def finger-rows 4)
(def finger-columns 5)
(def thumb-upper-rows 3)
(def thumb-lower-rows 2)
(def cirum (* Math/PI 2 radius))
(def step (/ (* Math/PI 2) (/ cirum width)))
(def offset (/ step 2))
(def screw-radius 1)
(def mounting-post-height 30)
(def mounting-post-radius 3)
(def shell-thickness 15)
(def shell-gap 5)
(def shell-height 90)

(def switch-cutter
  (let [extra (cube 3.5 15.6 14)
        angle (/ Math/PI 2)]
    (->> (union
           (cube 14 14 14)
           (translate [4.0 0 0] extra)
           (translate [-4.0 0 0] extra)
           (->> extra
                (rotate angle [0 0 1])
                (translate [0 4 0]))
           (->> extra
                (rotate angle [0 0 1])
                (translate [0 -4 0])))
         (rotate angle [-1 0 0]))))

(def wall-plate
  (->> (cube (+ width plate-thickness) (+ height plate-thickness) wall-thickness)
       (rotate (/ Math/PI 2) [-1 0 0])
       (translate [0 (- (/ wall-thickness 2)) 0])))

(def wall-plate-15
  (->> (cube (+ width plate-thickness) (* height 1.5) wall-thickness)
       (rotate (/ Math/PI 2) [-1 0 0])
       (translate [0 (- (/ wall-thickness 2)) 0])))

(def finger-cutter
  (->> (cube width height wall-thickness)
       (rotate (/ Math/PI 2) [-1 0 0])
       (translate [0 (- 0 (/ wall-thickness 2) plate-thickness) 0])))

(def switch-support
  (->> (cylinder 0.5 15)
       (rotate (/ Math/PI 2) [0 1 0])))

(defn place-finger-block
  [row column block]
  (let [a (+ offset (* column step))
        b (+ offset (* row step))
        x (* (Math/sin a) radius)
        y (* (Math/cos a) radius)]
    (->> block
         (rotate a [0 0 -1])
         (translate [x y 0])
         (rotate (/ Math/PI 2) [0 1 0])
         (rotate b [0 0 1]))))

(def mounting-post
  (->> (cylinder mounting-post-radius mounting-post-height)
       (with-fn 20)
       (rotate (/ Math/PI 2) [1 0 0])))

(def screw-cutter
  (->> (cylinder screw-radius mounting-post-height)
       (with-fn 20)
       (translate [0 0 (- (- wall-thickness plate-thickness))])
       (rotate (/ Math/PI 2) [1 0 0])))

(defn place-all-finger-blocks
  [block]
  (let [half-rows (/ finger-rows 2)
        half-columns (/ finger-columns 2)]
    (for [row (range (- half-rows) half-rows)
          column (range (- half-columns) half-columns)]
      (place-finger-block row column block))))


(defn place-all-thumb-blocks
  [upper-block lower-block]
  (let [half-upper-rows (/ thumb-upper-rows 2)
        half-lower-rows (/ thumb-lower-rows 2)]
    (union
      (for [row (range (- half-upper-rows) half-upper-rows) ]
        (place-finger-block row -0.5 upper-block))
      (for [row (range (- half-lower-rows) half-lower-rows)]
        (place-finger-block row 0.5 lower-block)))))

(defn place-all-posts
  [block]
  (union
    (place-finger-block -0.5 0 block)
    (place-finger-block -0.5 -1 block)
    (place-finger-block -1.5 -2 block)
    (place-finger-block -1.5 1 block)
    (place-finger-block 0.5 1 block)
    (place-finger-block 0.5 -2 block)))

(def fingers
  (difference
    (union 
      (place-all-finger-blocks wall-plate))
    (place-all-finger-blocks switch-cutter)
    (place-all-finger-blocks finger-cutter)))

(def thumbs
  (difference
    (union
      (place-all-thumb-blocks wall-plate wall-plate-15))
    (place-all-thumb-blocks switch-cutter switch-cutter)
    (place-all-thumb-blocks finger-cutter finger-cutter)))

(def shell-block
  (->> (cube width (+ height plate-thickness) shell-thickness)
       (rotate (/ Math/PI 2) [1 0 0])
       (translate [0 (+ (/ shell-thickness 2) shell-gap) 0])
       ))

(defn place-row-of-blocks
  [block]
  (let [half-columns (/ finger-columns 2)]
    (for [row (range (- half-columns) half-columns)]
    (place-finger-block row -0.5 block))))

(def shell-row
  (union
    (place-row-of-blocks shell-block)))

(def finger-shell
  (let [shell-rows (/ shell-height width)
        half-rows (/ shell-rows 2)]
    (union
      (for [row (range (- half-rows) half-rows)]
        (->> shell-row
             (translate [0 0 (* width row)]))))))

(def complete
  (union
    fingers 
    finger-shell))

(defn render-part!
  [[filename part]]
  (spit (str "out/" filename ".scad")
        (write-scad part)))

(defn render!
  []
  (dorun 
    (map render-part!
         {"switch-cutter" switch-cutter
          "finger-plate" finger-plate
          "fingers" fingers
          "thumbs" thumbs
          "wall-plate" wall-plate
          "mounting-post" mounting-post
          "complete" complete
          })))

(render!)
