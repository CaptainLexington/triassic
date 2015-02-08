(ns triassic.matrix
<<<<<<< HEAD
  (:require [vec3]
            [mat4]
=======
  (:require [goog.vec.Mat4 :as mat4]
>>>>>>> 056d380ee9a738186bcf1e282f241313a047d40a
            [triassic.utils :as utils]))

;;; glMatrix vec3 wrapper


(defn vector-3 [x y z]
  (vec3/fromValues x y z))

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


(vec3/str (add (vector-3 1 1 1) (vector-3 2 2 2)))


;;; glMatrix mat4 wrapper


(defn identity-matrix []
  (mat4/createFloat32Identity))

(defn from-vector [matrix]
  (mat4/createFloat32FromArray (js/Float32Array. (to-array matrix))))

(defn equals? [matA matB]
  (mat4/equals matA matB))

(defn multiply [a b]
  (let [new (mat4/create)]
    (mat4/multMat a b new)
    new))

(defn scale [mat scalar]
  (let [new (mat4/create)]
    (mat4/multScalar mat scalar new)
    new))

(defn camera
  ([default] (camera nil))
  ([default opts])
  ([fov-y aspect-ratio near far]
   (make-perspective fov-y aspect-ratio near far))
  ([left right bottom top near far]
   (make-orthogonal left right bottom top near far)))

(defn- make-orthogonal [left right bottom top near far]
  (let [new (mat4/create)]
    (mat4/makeOrtho new left right bottom top near far)))

(defn- make-perspective [fov-y aspect-ratio near far]
  (let [new (mat4/create)]
    (mat4/makePerspective new fov-y aspect-ratio near far)))
