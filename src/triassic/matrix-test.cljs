(ns triassic.matrix-test
  (:require-macros [cemerick.cljs.test
                    :refer (is deftest with-test run-tests testing test-var)])
  (:require [triassic.vector :as vec3]
            [triassic.matrix :as matrix]
            [cemerick.cljs.test :as t]))

(enable-console-print!)

;;tests for vectors


(deftest vec3-create
  (is (= (vec3/vec3-str (vec3/vector-3 1 -1 0))  "vec3(1, -1, 0)")))

(deftest vec3-add
  (let [vec1 (vec3/vector-3 1 -1 0)
        vec2 (vec3/vector-3 3 -4 1)]
    (is (= (vec3/vec3-str (vec3/add vec1 vec2))  "vec3(4, -5, 1)"))))

(deftest vec3-cross
  (let [vec1 (vec3/vector-3 1 -1 0)
        vec2 (vec3/vector-3 3 -4 1)]
    (is (= (vec3/vec3-str (vec3/cross vec1 vec2))  "vec3(-1, -1, -1)"))))

(deftest vec3-distance
  (let [vec1 (vec3/vector-3 1 -1 0)
        vec2 (vec3/vector-3 3 -4 1)]
    (is (= (vec3/distance vec1 vec2)  3.7416573867739413))))

(deftest vec3-dot
  (let [vec1 (vec3/vector-3 1 -1 0)
        vec2 (vec3/vector-3 3 -4 1)]
    (is (= (vec3/dot vec1 vec2)  7))))

(deftest vec3-magnitude
  (is (= (vec3/magnitude (vec3/vector-3 1 -1 0)) (.sqrt js/Math 2))))

(deftest vec3-normalize
  (is (= (vec3/vec3-str (vec3/normalize (vec3/vector-3 2 0 0)))  "vec3(1, 0, 0)")))

(deftest vec3-scale
  (is (= (vec3/vec3-str (vec3/scale (vec3/vector-3 2 1 1) 4)) "vec3(8, 4, 4)")))

(deftest vec3-subtract
  (let [vec1 (vec3/vector-3 1 -1 0)
        vec2 (vec3/vector-3 3 -4 1)]
    (is (= (vec3/vec3-str (vec3/subtract vec1 vec2))  "vec3(-2, 3, -1)"))))


;;tests for matrices


(deftest mat4-ident
  (is (= (matrix/matrix-str (matrix/identity-matrix))
         "mat4(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1)")))

;; scale [mat scalar]

;; determinant [mat]

;; invert [mat]

;; look-at [eye center up]

;; translate [mat vec3]

;; transpose [mat]


;; make-orthogonal [left right bottom top near far]

;; make-perspective [fov-y aspect-ratio near far]


(deftest mat4-identity
  (is (= (matrix/matrix-str (matrix/identity-matrix))
         "mat4(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1)")))


(t/test-ns 'triassic.matrix-test)
