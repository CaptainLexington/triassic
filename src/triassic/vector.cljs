(ns triassic.vector
  (:require [vec3]
            [triassic.utils :as utils]))

;;; glMatrix vec3 wrapper


(defn vector-3 [x y z]
  "Returns a new vector initialized with the x, y, and z values"
  (vec3/fromValues x y z))

(defn x [vec3]
  "Returns the x component of the given vector"
  (aget vec3 0 ))
(defn y [vec3]
  "Returns the y component of the given vector"
  (aget vec3 1 ))
(defn z [vec3]
  "Returns the z component of the given vector"
  (aget vec3 2 ))

(defn add [vec1 vec2]
  "Returns the component-wise sum of two vectors"
  (let [new (vec3/create)]
    (vec3/add new vec1 vec2)))

(defn cross [vec1 vec2]
  "Returns the cross product of two vectors"
  (let [new (vec3/create)]
    (vec3/cross new vec1 vec2)))

(defn distance [vec1 vec2]
  "Returns the Euclidian distance between two points in space"
  (vec3/distance vec1 vec2))

(defn dot [vec1 vec2]
  "Returns the dot product of two vectors"
  (vec3/dot vec1 vec2))

(defn magnitude [vec1]
  "Returns the magnitude of the given vector"
  (vec3/length vec1))

(defn normalize [vec1]
  "Returns a unit vector in the same direction as the given vector"
  (let [new (vec3/create)]
    (vec3/normalize new vec1)))

(defn scale [vec1 scalar]
  "Returns a vector that is the result of scaling the given vector by the given scalar"
  (let [new (vec3/create)]
    (vec3/scale new vec1 scalar)))

(defn subtract [vec1 vec2]
  "Returns the component-wise difference between two vectors"
  (let [new (vec3/create)]
    (vec3/subtract new vec1 vec2)))

(defn vec3-str [vec1]
  "Returns the string representation of the given vector"
  (vec3/str vec1))
