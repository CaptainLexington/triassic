(ns triassic.example
  (:require [triassic.matrix :as matrix]
            [triassic.vector :as vec3]
            [triassic.geometry :as geo]
            [triassic.mesh :as mesh]
            [triassic.materials :as materials]
            [triassic.utils :refer [init-gl animate mapply]]
            [triassic.render :as render]
            [triassic.assets :as assets]
            [triassic.colors :as colors]
            [triassic.utils :as utils]
            [WebGLUtils]
            [mat4]
            [cljs-webgl.buffers :refer [clear-color-buffer clear-depth-buffer]]
            [weasel.repl :as repl]))

(when-not (repl/alive?)
  (repl/connect "ws://localhost:9001"))


(def rad1 0)
(def rad2 1)


(defn move-and-rotate [mesh displacement axis angle]
  (-> mesh
      (geo/translate displacement)
      (geo/rotate axis angle)))

(def ass {:diloph (assets/lw-obj "/resources/obj/diloph.obj")
          :trex (assets/lw-obj "/resources/obj/Trex.OBJ")
          :trex-texture (assets/img "/resources/obj/Trex_Diffuse.jpg")
          :diloph-texture (assets/img "/resources/obj/diloph.jpg")})

(defn init [asses]
  (let [canvas (.getElementById js/document "canvas")
        gl (init-gl canvas)
        {viewport-width  :width,
         viewport-height :height} (utils/get-viewport gl)
        camera (matrix/camera 45
                              (/ viewport-width viewport-height)
                              0.1
                              100.0)
        cube-material-2 (materials/image-map gl
                                           [
                                            ; Front face
                                            0.0, 0.0,
                                            1.0, 0.0,
                                            1.0, 1.0,
                                            0.0, 1.0,

                                            ; Back face
                                            1.0, 0.0,
                                            1.0, 1.0,
                                            0.0, 1.0,
                                            0.0, 0.0,

                                            ; Top face
                                            0.0, 1.0,
                                            0.0, 0.0,
                                            1.0, 0.0,
                                            1.0, 1.0,

                                            ; Bottom face
                                            1.0, 1.0,
                                            0.0, 1.0,
                                            0.0, 0.0,
                                            1.0, 0.0,

                                            ; Right face
                                            1.0, 0.0,
                                            1.0, 1.0,
                                            0.0, 1.0,
                                            0.0, 0.0,

                                            ; Left face
                                            0.0, 0.0,
                                            1.0, 0.0,
                                            1.0, 1.0,
                                            0.0, 1.0, ]
                                           (:cube-texture asses))


        trex-material (materials/image-map gl
                                           (:uv-coords (:dino asses))
                                           (:capsule-texture asses))


        cube-material (materials/solid-color gl (colors/rgb colors/forest-green-traditional) 1)

        pyramid-material-2 (materials/solid-color gl (colors/rgb colors/beige) 1)

        ;pyramid-material (materials/color-map gl
        ;                                      [
        ;                                       ; Front face
        ;                                       1.0, 0.0, 0.0, 1.0,
        ;                                       0.0, 1.0, 0.0, 1.0,
        ;                                       0.0, 0.0, 1.0, 1.0,
        ;
        ;                                       ; Right face
        ;                                       1.0, 0.0, 0.0, 1.0,
        ;                                       0.0, 0.0, 1.0, 1.0,
        ;                                       0.0, 1.0, 0.0, 1.0,
        ;
        ;                                       ; Back face
        ;                                       1.0, 0.0, 0.0, 1.0,
        ;                                       0.0, 1.0, 0.0, 1.0,
        ;                                       0.0, 0.0, 1.0, 1.0,
        ;
        ;                                       ; Left face
        ;                                       1.0, 0.0, 0.0, 1.0,
        ;                                       0.0, 0.0, 1.0, 1.0,
        ;                                       0.0, 1.0, 0.0, 1.0 ])

        ;cube (mesh/cube gl 2 cube-material-2)
        trex (mesh/mesh gl (:trex asses) trex-material)
        diloph (mesh/mesh gl (:diloph asses) trex-material)
        cube-displacement (vec3/vector-3 1.5 0 -8)
        ;pyramid (mesh/pyramid gl 2 2 2 pyramid-material)
        pyramid-displacement (vec3/vector-3 -1.5 0 -8)]



    (.log js/console (apply array (:uv-coords (:dino asses))))

    (animate
      (fn [frame]                                           ; frame is not used

        (apply clear-color-buffer gl (colors/rgba colors/forest-green-traditional))
        (clear-depth-buffer gl 1)

        (set! rad2 (dec rad2))
        (set! rad1 (inc rad1))

        (render/double-draw! gl
                             [
                              (move-and-rotate trex
                                               pyramid-displacement
                                               :y
                                               (/ rad2 25))

                              ;dino
                              (geo/scale
                                (move-and-rotate diloph
                                                 cube-displacement
                                                 :y
                                                 (/ rad1 100))
                                         0.075)
                              ]
                             camera)))))

(defn ^:export start []
  (assets/load-assets nil ass init))
