(ns triassic.space
  (:require [goog.vec.vec3 :as vec3]
            [triassic.utils :as utils]))



(defn point [x y z]
  [x y z])


(defn vec3 [x y z]
  [x y z])

(defn x [spc]
  (first spc))

(defn y [spc]
  (second spc))

(defn z [spc]
  (nth spc 2))

(defn distance [spc1 spc2]
  (utils/sqrt (apply
               +
               (mapv
                #(utils/square (- %1 %2))
                spc1
                spc2))))

(defn magnitude [vec3]
  (distance (point 0 0 0) vec3))

(defn add [spc vec3]
  (mapv + spc vec3))

(defn subtract [spc vec3]
  (mapv - spc vec3))

(defn dot [vec1 vec2]
  (apply + (map * vec1 vec2)))
