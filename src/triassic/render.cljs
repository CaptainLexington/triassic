(ns triassic.render
  (:require    [triassic.geometry :as geo]
               [triassic.utils :refer [init-gl init-shaders get-perspective-matrix
                                       get-position-matrix deg->rad animate]]
               [cljs-webgl.buffers :refer [create-buffer clear-color-buffer clear-depth-buffer draw!]]
               [cljs-webgl.shaders :refer [get-attrib-location]]
               [cljs-webgl.constants.buffer-object :as buffer-object]
               [cljs-webgl.constants.capability :as capability]
               [cljs-webgl.constants.draw-mode :as draw-mode]
               [cljs-webgl.constants.data-type :as data-type]
               [cljs-webgl.typed-arrays :as ta]))



(defn double-draw! [gl meshes camera]
  (let [defaults {
                  :draw-mode     draw-mode/triangles
                  :capabilities  {capability/depth-test true}}]
    (doseq [mesh meshes]
      (mapply (partial draw! gl) mesh))))


(defn webglify [mesh camera]
  (let [vertex-position-buffer (create-buffer gl
                                              (ta/float32 (:vertices mesh))
                                              buffer-object/array-buffer
                                              buffer-object/static-draw
                                              3)

        vertex-position-attribute (get-attrib-location gl
                                                       (:shader-prog mesh)
                                                       "aVertexPosition")

        vertex-indices (create-buffer
                              gl
                              (ta/unsigned-int16 (:indices mesh))
                              buffer-object/element-array-buffer
                              buffer-object/static-draw
                              1)]
    (assoc mesh
      :attributes [{:buffer   vertex-position-buffer
                    :location vertex-position-attribute}]

      :uniforms [{:name   "uPMatrix"
                  :type   :mat4
                  :values (if (nil? (:camera mesh))
                            camera
                            (:camera mesh))}
                 {:name   "uMVMatrix"
                  :type   :mat4
                  :values (:transformation-matrix mesh)}]

      :element-array {:buffer vertex-indices
                      :type   data-type/unsigned-short
                      :offset 0}

      :count         (.-numItems cube-vertex-indices))))