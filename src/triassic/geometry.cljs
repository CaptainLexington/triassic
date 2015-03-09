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
  (transform
    mesh
    matrix/rotate
    axis
    angle))

(defn translate [mesh vec3]
  (transform
    mesh
    matrix/translate
    vec3))

(defn scale [mesh scalar]
  (transform
    mesh
    matrix/scale
    scalar))




(defn box [diagonal]
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
  (box (vector/vector-3 length length length)))

(defn pyramid [length width height]
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
