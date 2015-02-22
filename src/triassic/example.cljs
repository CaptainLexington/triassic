(ns triassic.example
  (:require [triassic.matrix :as matrix]
            [triassic.vector :as vec3]
            [triassic.geometry :as geo]
            [triassic.utils :refer [init-gl init-shaders get-perspective-matrix
                                    get-position-matrix deg->rad animate]]
            [WebGLUtils]
            [mat4]
            [cljs-webgl.buffers :refer [create-buffer clear-color-buffer clear-depth-buffer draw!]]
            [cljs-webgl.shaders :refer [get-attrib-location]]
            [cljs-webgl.constants.buffer-object :as buffer-object]
            [cljs-webgl.constants.capability :as capability]
            [cljs-webgl.constants.draw-mode :as draw-mode]
            [cljs-webgl.constants.data-type :as data-type]
            [cljs-webgl.typed-arrays :as ta]))


(defn ^:export start []
  (let [canvas      (.getElementById js/document "canvas")
        gl          (init-gl canvas)
        shader-prog (init-shaders gl)
        pyramid-vertex-position-buffer
        (create-buffer gl
                       (ta/float32 (geo/pyramid 1 2 4))
                       buffer-object/array-buffer
                       buffer-object/static-draw
                       3)

        pyramid-vertex-color-buffer
        (create-buffer gl
                       (ta/float32 [
                                    ; Front face
                                    1.0, 0.0, 0.0, 1.0,
                                    0.0, 1.0, 0.0, 1.0,
                                    0.0, 0.0, 1.0, 1.0,

                                    ; Right face
                                    1.0, 0.0, 0.0, 1.0,
                                    0.0, 0.0, 1.0, 1.0,
                                    0.0, 1.0, 0.0, 1.0,

                                    ; Back face
                                    1.0, 0.0, 0.0, 1.0,
                                    0.0, 1.0, 0.0, 1.0,
                                    0.0, 0.0, 1.0, 1.0,

                                    ; Left face
                                    1.0, 0.0, 0.0, 1.0,
                                    0.0, 0.0, 1.0, 1.0,
                                    0.0, 1.0, 0.0, 1.0 ])
                       buffer-object/array-buffer
                       buffer-object/static-draw
                       4)

        pyramid-matrix (get-position-matrix [-1.5 0.0 -8.0])

        cube-vertex-position-buffer
        (create-buffer gl
                       (ta/float32 (geo/cube 2))
                       buffer-object/array-buffer
                       buffer-object/static-draw
                       3)

        cube-vertex-color-buffer
        (create-buffer gl
                       (ta/float32 [
                                    ; Front face
                                    1.0, 0.0, 0.0, 1.0,
                                    1.0, 0.0, 0.0, 1.0,
                                    1.0, 0.0, 0.0, 1.0,
                                    1.0, 0.0, 0.0, 1.0,

                                    ; Back face
                                    1.0, 1.0, 0.0, 1.0,
                                    1.0, 1.0, 0.0, 1.0,
                                    1.0, 1.0, 0.0, 1.0,
                                    1.0, 1.0, 0.0, 1.0,

                                    ; Top face
                                    0.0, 1.0, 0.0, 1.0,
                                    0.0, 1.0, 0.0, 1.0,
                                    0.0, 1.0, 0.0, 1.0,
                                    0.0, 1.0, 0.0, 1.0,

                                    ; Bottom face
                                    1.0, 0.5, 0.5, 1.0,
                                    1.0, 0.5, 0.5, 1.0,
                                    1.0, 0.5, 0.5, 1.0,
                                    1.0, 0.5, 0.5, 1.0,

                                    ; Right face
                                    1.0, 0.0, 1.0, 1.0,
                                    1.0, 0.0, 1.0, 1.0,
                                    1.0, 0.0, 1.0, 1.0,
                                    1.0, 0.0, 1.0, 1.0,

                                    ; Left face
                                    0.0, 0.0, 1.0, 1.0,
                                    0.0, 0.0, 1.0, 1.0,
                                    0.0, 0.0, 1.0, 1.0,
                                    0.0, 0.0, 1.0, 1.0 ])
                       buffer-object/array-buffer
                       buffer-object/static-draw
                       4)

        cube-vertex-indices
        (create-buffer gl
                       (ta/unsigned-int16 [
                                           0, 1, 2,      0, 2, 3,      ; Front face
                                           4, 5, 6,      4, 6, 7,      ; Back face
                                           8, 9, 10,     8, 10, 11,    ; Top face
                                           12, 13, 14,   12, 14, 15,   ; Bottom face
                                           16, 17, 18,   16, 18, 19,   ; Right face
                                           20, 21, 22,   20, 22, 23])  ; Left face
                       buffer-object/element-array-buffer
                       buffer-object/static-draw
                       1)

        cube-matrix (get-position-matrix [ 1.5 0.0 -8.0])

        vertex-position-attribute (get-attrib-location gl shader-prog "aVertexPosition")
        vertex-color-attribute (get-attrib-location gl shader-prog "aVertexColor")
        perspective-matrix (get-perspective-matrix gl)
        one-degree (deg->rad 1)]

    (animate
      (fn [frame] ; frame is not used

        (clear-color-buffer gl 0.0 0.0 0.0 1.0)
        (clear-depth-buffer gl 1)

        ; Diverges slightly from the LearningWegGL example:
        ; We just rotate the matrices by 1 degree on each frame

        ; gl-matrix relies on mutating the matrices
        ; ... let's gloss over those details for the moment
        (mat4/rotate
          pyramid-matrix
          pyramid-matrix
          one-degree
          (ta/float32 [0 1 0]))

        (mat4/rotate
          cube-matrix
          cube-matrix
          one-degree
          (ta/float32 [1 1 1]))

        ; TODO: there's no point in building these structures each time
        ;       - should be able to do something like: (apply draw! gl opts)
        (draw!
          gl
          :shader shader-prog
          :draw-mode draw-mode/triangles
          :count (.-numItems pyramid-vertex-position-buffer)
          :attributes [{:buffer pyramid-vertex-position-buffer :location vertex-position-attribute}
                       {:buffer pyramid-vertex-color-buffer :location vertex-color-attribute}]
          :uniforms [{:name "uPMatrix" :type :mat4 :values perspective-matrix}
                     {:name "uMVMatrix" :type :mat4 :values pyramid-matrix}]
          :capabilities {capability/depth-test true})

        (draw!
          gl
          :shader shader-prog
          :draw-mode draw-mode/triangles
          :count (.-numItems cube-vertex-indices)
          :attributes [{:buffer cube-vertex-position-buffer :location vertex-position-attribute}
                       {:buffer cube-vertex-color-buffer :location vertex-color-attribute}]
          :uniforms [{:name "uPMatrix" :type :mat4 :values perspective-matrix}
                     {:name "uMVMatrix" :type :mat4 :values cube-matrix}]
          :element-array {:buffer cube-vertex-indices :type data-type/unsigned-short :offset 0}
          :capabilities {capability/depth-test true})))))
