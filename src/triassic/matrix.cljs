(ns triassic.matrix
  (:require [goog.vec.Mat4 :as mat4]
            [triassic.utils :as utils]))



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
