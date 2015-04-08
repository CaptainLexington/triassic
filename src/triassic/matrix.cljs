(ns triassic.matrix
  (:require [mat4]
            [quat]
            [triassic.vector :as vec3]
            [triassic.utils :as utils]))


;;; glMatrix mat4 wrapper

(defn matrix-str [matrix]
  "Returns a string representation of a given matrix"
  (mat4/str matrix))

(defn identity-matrix []
  "Returns the 4x4 Identity Matrix"
  (let [new (mat4/create)]
    (mat4/identity new)))

(defn multiply [a b]
  "Returns a matrix that is the result of multiplying the first matrix by the second"
  (let [new (mat4/create)]
    (mat4/multiply new a b)
    new))

(defn from-quaternion [quat]
  "Returns a matrix from a given quaternion"
  (let [new (mat4/create)]
    (mat4/fromQuat new quat)
    new))

(defn scale [mat scalar]
  "Returns a new matrix that is the result of scaling the given matrix scaled by the given scalar"
  (let [new (mat4/create)]
    (mat4/scale new mat (vec3/vector-3 scalar scalar scalar))
    new))

(defn determinant [mat]
  "Returns the determinant of the given matrix"
  (mat4/determinant mat))

(defn invert [mat]
  "Returns the inverse of the given matrix"
  (let [new (mat4/create)]
    (mat4/invert new mat)))

(defn look-at [eye center up]
  "Returns a look-at matrix given an eye vector, a center point, and an up vector"
  (let [new (mat4/create)]
    (mat4/lookAt new eye center up)))

(defn translate [mat vec3]
  "Returns the matrix that is the result of translating the given matrix by the given vector"
  (let [new (mat4/create)]
    (mat4/translate new mat vec3)))

(defn rotate [mat axis angle]
  "Returns the matrix that is the result of rotating the given matrix along the given axis by the given angle, in radians.
  In addition to taking the axis as a vec3, it accepts keywords :x, :y, :z, :xy, :xz, :xyz as aliases for corresponding vectors along those axes."
  (let [new (mat4/create)]
    (case axis
      :x (mat4/rotate new mat angle (vec3/vector-3 1 0 0))
      :y (mat4/rotate new mat angle (vec3/vector-3 0 1 0))
      :z (mat4/rotate new mat angle (vec3/vector-3 0 0 1))
      :xy (mat4/rotate new mat angle (vec3/vector-3 1 1 0))
      :yz (mat4/rotate new mat angle (vec3/vector-3 0 1 1))
      :xz (mat4/rotate new mat angle (vec3/vector-3 1 0 1))
      :xyz (mat4/rotate new mat angle (vec3/vector-3 1 1 1))
      (mat4/rotate new mat angle axis))
    new))


(defn transpose [mat]
  "Returns the transposition of the given matrix"
  (let [new (mat4/create)]
    (mat4/transpose new mat)))


(defn- make-orthogonal [left right bottom top near far]
  (let [new (mat4/create)]
    (mat4/ortho new left right bottom top near far)))

(defn- make-perspective [fov-y aspect-ratio near far]
  (let [new (mat4/create)]
    (mat4/perspective new fov-y aspect-ratio near far)))

(defn camera
  "Returns a projection matrix.
  When called with four arguments, it returns the perspective matrix for a given field-of-view along the y axis, aspect ratio, and near and far distances.
  When called with six arguments, it returns the orthogonal matrix for the given left, right, bottom, top, near, and far values."
  ([default] (camera nil))
  ([default opts])
  ([fov-y aspect-ratio near far]
   (make-perspective fov-y aspect-ratio near far))
  ([left right bottom top near far]
   (make-orthogonal left right bottom top near far)))

