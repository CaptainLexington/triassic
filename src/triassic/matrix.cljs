(ns triassic.matrix
  (:require [mat4]
            [triassic.utils :as utils]))


(defn multiply [a b]
  (let [new (mat4/create)]
    (mat4/multiply new a b)
    new))
