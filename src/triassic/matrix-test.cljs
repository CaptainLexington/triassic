(ns triassic.matrix-test
  (:require-macros [cemerick.cljs.test
                    :refer (is deftest with-test run-tests testing test-var)])
  (:require [triassic.matrix :as matrix]
            [cemerick.cljs.test :as t]))

(enable-console-print!)

(deftest vec3-define
  (is (= (matrix/vec3-str (matrix/vector-3 1 -1 0))  "vec3(1, -1, 0)")))


(t/test-ns 'triassic.matrix-test)
