(ns triassic.core
 (:require [mat4]
           [WebGLUtils]
           [triassic.matrix :as matrix]
           [triassic.matrix-test :as mat-test]))


(def a (mat4/create))
(def b (mat4/create))

(mat4/identity a)
(mat4/identity b)

(matrix/multiply a b)
