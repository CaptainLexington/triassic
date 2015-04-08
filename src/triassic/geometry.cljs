(ns triassic.geometry
  (:require [triassic.matrix :as matrix]
            [triassic.vector :as vector]))


;;Geometry transformations

(defn- transform [mesh transformation & opts]
  (assoc
    mesh
    :transformation-matrix
    (apply (partial transformation
                    (:transformation-matrix mesh))
           opts)))

(defn rotate [mesh axis angle]
  "Returns the mesh that is the result of rotating the given matrix along the given axis by the given angle, in radians.
  In addition to taking the axis as a vec3, it accepts keywords :x, :y, :z, :xy, :xz, :xyz as aliases for corresponding vectors along those axes."
  (transform
    mesh
    matrix/rotate
    axis
    angle))

(defn translate [mesh vec3]
  "Returns the result of translating the given mesh by the given vector"
  (transform
    mesh
    matrix/translate
    vec3))

(defn scale [mesh scalar]
  "Returns the resulting of scaling the given mesh by the given scalar"
  (transform
    mesh
    matrix/scale
    scalar))




(defn box [diagonal]
  "Returns the rectangular form in three-dimensional space, centered at the origin, and described by the given diagonal vector"
  (let [x (/ (vector/x diagonal) 2)
        y (/ (vector/y diagonal) 2)
        z (/ (vector/z diagonal) 2)]
    [
     ; Front face
     (- x), (- y),  z,
     x, (- y),  z,
     x,  y,  z,
     (- x),  y,  z,

     ; Back face
     (- x), (- y), (- z),
     (- x),  y, (- z),
     x,  y, (- z),
     x, (- y), (- z),

     ; Top face
     (- x), y, (- z),
     (- x),  y,  z,
     x,  y,  z,
     x,  y, (- z),

     ; Bottom face
     (- x), (- y), (- z),
     x, (- y), (- z),
     x, (- y),  z,
     (- x), (- y),  z,

     ; Right face
     x, (- y), (- z),
     x,  y, (- z),
     x,  y,  z,
     x, (- y),  z,

     ; Left face
     (- x), (- y), (- z),
     (- x), (- y),  z,
     (- x),  y,  z,
     (- x),  y, (- z)
     ]))

(defn cube [length]
  "Returns a cube in three-dimensional space, centered at the origin, with the given edge length"
  (box (vector/vector-3 length length length)))

(defn pyramid [length width height]
  "Returns a pyramid in three-dimenstional space, centered at the origin, with the given length, width, and height"
  (let [x (/ length 2)
        y (/ height 2)
        z (/ width 2)]
    [
     ; Front face
     0.0,  y,  0.0,
     (- x), (- y),  z,
     x, (- y),  z,

     ; Right face
     0.0,  y,  0.0,
     x, (- y),  z,
     x, (- y), (- z),

     ; Back face
     0.0,  y,  0.0,
     x, (- y), (- z),
     (- x), (- y), (- z),

     ; Left face
     0.0,  y,  0.0,
     (- x), (- y), (- z),
     (- x), (- y),  z
     ]))
