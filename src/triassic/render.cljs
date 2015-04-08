(ns triassic.render
  (:require [triassic.geometry :as geo]
            [triassic.utils :as utils]
            [cljs-webgl.buffers :refer [create-buffer clear-color-buffer clear-depth-buffer draw!]]
            [cljs-webgl.shaders :refer [get-attrib-location]]
            [cljs-webgl.constants.buffer-object :as buffer-object]
            [cljs-webgl.constants.capability :as capability]
            [cljs-webgl.constants.draw-mode :as draw-mode]
            [cljs-webgl.constants.data-type :as data-type]
            [cljs-webgl.typed-arrays :as ta]))




(defn- webglify [gl mesh camera]
  (let [
        ;vertex-position-buffer (create-buffer gl
        ;                                      (ta/float32 (:vertices mesh))
        ;                                      buffer-object/array-buffer
        ;                                      buffer-object/static-draw
        ;                                      3)

        vertex-position-buffer (:vertices mesh)

        vertex-position-attribute (get-attrib-location gl
                                                       (:shader (:material mesh))
                                                       "aVertexPosition")

        vertex-indices (:indices mesh)

        ;vertex-indices (if (nil? (:indices mesh))
        ;                 nil
        ;                 (create-buffer
        ;                   gl
        ;                   (ta/unsigned-int16 (:indices mesh))
        ;                   buffer-object/element-array-buffer
        ;                   buffer-object/static-draw
        ;                   1))
        ]
    (assoc {}
      :attributes (apply conj [{:buffer   vertex-position-buffer
                            :location vertex-position-attribute}]
                          (:attribute (:material mesh)))

      :uniforms [{:name   "uPMatrix"
                  :type   :mat4
                  :values (if (nil? (:camera mesh))
                            camera
                            (:camera mesh))}
                 {:name   "uMVMatrix"
                  :type   :mat4
                  :values (:transformation-matrix mesh)}]

      :textures (if (nil? (:textures (:material mesh)))
                  nil
                  (:textures (:material mesh)))

      :element-array (if (nil? vertex-indices)
                       nil
                       {:buffer vertex-indices
                        :type   data-type/unsigned-short
                        :offset 0})

      :shader (:shader (:material mesh))

      :count (if (nil? vertex-indices)
               (.-numItems vertex-position-buffer)
               (.-numItems vertex-indices))
      :webglified? true)))



(defn double-draw! [gl meshes camera]
  "Given a gl context, a scene graph, and a camera, draws the scene to the canvas."
  (let [defaults {:draw-mode     draw-mode/triangles
                  :capabilities  {capability/depth-test true}}
        prepped-meshes (map #(if-not (:webglified? %1)
                              (webglify gl %1 camera))
                            meshes)]
    (doseq [mesh prepped-meshes]
      (utils/mapply (partial draw! gl)
                    (merge defaults mesh)))))