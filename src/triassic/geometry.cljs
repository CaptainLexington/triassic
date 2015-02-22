(ns triassic.geometry
  (:require [triassic.matrix :as matrix]
            [triassic.vector :as vector]))


(defn box [diagonal]
  (let [x (vector/x diagonal)
        y (vector/y diagonal)
        z (vector/z diagonal)]
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