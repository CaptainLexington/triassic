(ns triassic.core
 (:require [mat4]
           [WebGLUtils]
           [triassic.matrix :as matrix]))


(def a (mat4/create))
(def b (mat4/create))

(mat4/identity a)
(mat4/identity b)

(matrix/multiply a b)
