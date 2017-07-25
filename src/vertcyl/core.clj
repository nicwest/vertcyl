(ns vertcyl.core
  (:require [vertcyl.switch :refer [render-switches!]]
            [vertcyl.fingers :refer [render-fingers!]]
            [vertcyl.thumbs :refer [render-thumbs!]]
            [vertcyl.complete :refer [render-complete!]])
  (:gen-class))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (render-switches!)
  (render-fingers!)
  (render-thumbs!)
  (render-complete!)
  )

