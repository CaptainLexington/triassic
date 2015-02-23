(ns triassic.matrix
  (:require [mat4]
            [quat]
            [triassic.vector :as vec3]
            [triassic.utils :as utils]))


;;; glMatrix mat4 wrapper

(defn matrix-str [matrix]
  (mat4/str matrix))

(defn identity-matrix []
  (let [new (mat4/create)]
    (mat4/identity new)))

(defn multiply [a b]
  (let [new (mat4/create)]
    (mat4/multiply new a b)
    new))

(defn from-quaternion [quat]
  (let [new (mat4/create)]
    (mat4/fromQuat new quat)
    new))

(defn scale [mat scalar]
  (let [new (mat4/create)]
    (mat4/scale new mat scalar)
    new))

(defn determinant [mat]
  (mat4/determinant mat))

(defn invert [mat]
  (let [new (mat4/create)]
    (mat4/invert new mat)))

(defn look-at [eye center up]
  (let [new (mat4/create)]
    (mat4/lookAt new eye center up)))

(defn translate [mat vec3]
  (let [new (mat4/create)]
    (mat4/translate new mat vec3)))

(defn rotate [mat axis angle]
  (let [new (mat4/create)]
    (mat4/rotate new mat angle axis)))
(defn transpose [mat]
  (let [new (mat4/create)]
    (mat4/transpose new mat)))


(defn- make-orthogonal [left right bottom top near far]
  (let [new (mat4/create)]
    (mat4/ortho new left right bottom top near far)))

(defn- make-perspective [fov-y aspect-ratio near far]
  (let [new (mat4/create)]
    (mat4/perspective new fov-y aspect-ratio near far)))

(defn camera
  ([default] (camera nil))
  ([default opts])
  ([fov-y aspect-ratio near far]
   (make-perspective fov-y aspect-ratio near far))
  ([left right bottom top near far]
   (make-orthogonal left right bottom top near far)))

