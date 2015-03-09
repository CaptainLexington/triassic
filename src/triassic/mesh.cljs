(ns triassic.mesh
  (:require [triassic.geometry :as geo]
            [triassic.matrix :as matrix]
            [triassic.utils :as utils]
            [cljs-webgl.buffers :refer [create-buffer clear-color-buffer clear-depth-buffer draw!]]
            [cljs-webgl.shaders :refer [get-attrib-location]]
            [cljs-webgl.constants.buffer-object :as buffer-object]
            [cljs-webgl.constants.capability :as capability]
            [cljs-webgl.constants.draw-mode :as draw-mode]
            [cljs-webgl.constants.data-type :as data-type]
            [cljs-webgl.typed-arrays :as ta]))



(defn mesh [vertices indices material]
  {
   :vertices              vertices
   :indices               indices
   :transformation-matrix (matrix/identity-matrix)
   :material                material
   })

(defn cube [length material]
  (let [vertices (geo/cube length)

        indices [0, 1, 2,      0, 2, 3,      ; Front face
                 4, 5, 6,      4, 6, 7,      ; Back face
                 8, 9, 10,     8, 10, 11,    ; Top face
                 12, 13, 14,   12, 14, 15,   ; Bottom face
                 16, 17, 18,   16, 18, 19,   ; Right face
                 20, 21, 22,   20, 22, 23]]
    (mesh vertices indices material)))


(defn pyramid [length width height material]
  (let [vertices (geo/pyramid length width height)]
    (mesh vertices nil material)))