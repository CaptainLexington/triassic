(ns triassic.vector
  (:require [vec3]
            [triassic.utils :as utils]))

;;; glMatrix vec3 wrapper


(defn vector-3 [x y z]
  (vec3/fromValues x y z))

(defn x [vec3]
  (aget vec3 0 ))
(defn y [vec3]
  (aget vec3 1 ))
(defn z [vec3]
  (aget vec3 2 ))

(defn add [vec1 vec2]
  (let [new (vec3/create)]
    (vec3/add new vec1 vec2)))

(defn cross [vec1 vec2]
  (let [new (vec3/create)]
    (vec3/cross new vec1 vec2)))

(defn distance [vec1 vec2]
  (vec3/distance vec1 vec2))

(defn dot [vec1 vec2]
  (vec3/dot vec1 vec2))

(defn magnitude [vec1]
  (vec3/length vec1))

(defn normalize [vec1]
  (let [new (vec3/create)]
    (vec3/normalize new vec1)))

(defn scale [vec1 scalar]
  (let [new (vec3/create)]
    (vec3/scale new vec1 scalar)))

(defn subtract [vec1 vec2]
  (let [new (vec3/create)]
    (vec3/subtract new vec1 vec2)))

(defn vec3-str [vec1]
  (vec3/str vec1))
