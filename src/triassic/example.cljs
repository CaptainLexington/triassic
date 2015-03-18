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

(def ass {:dino (assets/lw-obj "/dino.obj")
          :texture {:value "/test.json"
              :fn    identity}})

(defn init [asses]
  (let [canvas (.getElementById js/document "canvas")
        gl (init-gl canvas)
        {viewport-width  :width,
         viewport-height :height} (utils/get-viewport gl)
        camera (matrix/camera 45
                              (/ viewport-width viewport-height)
                              0.1
                              100.0)
        ;cube-material (materials/image-map gl
        ;                                   [
        ;                                    ; Front face
        ;                                    0.0, 0.0,
        ;                                    1.0, 0.0,
        ;                                    1.0, 1.0,
        ;                                    0.0, 1.0,
        ;
        ;                                    ; Back face
        ;                                    1.0, 0.0,
        ;                                    1.0, 1.0,
        ;                                    0.0, 1.0,
        ;                                    0.0, 0.0,
        ;
        ;                                    ; Top face
        ;                                    0.0, 1.0,
        ;                                    0.0, 0.0,
        ;                                    1.0, 0.0,
        ;                                    1.0, 1.0,
        ;
        ;                                    ; Bottom face
        ;                                    1.0, 1.0,
        ;                                    0.0, 1.0,
        ;                                    0.0, 0.0,
        ;                                    1.0, 0.0,
        ;
        ;                                    ; Right face
        ;                                    1.0, 0.0,
        ;                                    1.0, 1.0,
        ;                                    0.0, 1.0,
        ;                                    0.0, 0.0,
        ;
        ;                                    ; Left face
        ;                                    0.0, 0.0,
        ;                                    1.0, 0.0,
        ;                                    1.0, 1.0,
        ;                                    0.0, 1.0, ]
        ;                                   "nehe.gif")


        cube-material (materials/solid-color gl (colors/rgb colors/violet) 1)

        pyramid-material-2 (materials/solid-color gl (colors/rgb colors/beige) 1)

        pyramid-material (materials/color-map gl
                                              [
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

        cube (mesh/cube 2 cube-material)
        dino (mesh/mesh (:dino asses) pyramid-material)
        cube-displacement (vec3/vector-3 1.5 0 -8)
        pyramid (mesh/pyramid 2 2 2 pyramid-material)
        pyramid-displacement (vec3/vector-3 -1.5 0 -8)]


    (.log js/console asses)
    (.log js/console (:vertices cube))
    (.log js/console (:vertices dino))


    (animate
      (fn [frame]                                           ; frame is not used

        (clear-color-buffer gl 0.0 0.0 0.0 1.0)
        (clear-depth-buffer gl 1)

        (set! rad2 (dec rad2))
        (set! rad1 (inc rad1))

        (render/double-draw! gl
                             [
                              (move-and-rotate cube
                                               pyramid-displacement
                                               :y
                                               (/ rad2 25))
                              ;(move-and-rotate dino
                              ;                 cube-displacement
                              ;                 :xyz
                              ;                 (/ rad1 50))
                              ]
                             camera)))))

(defn ^:export start []
  (assets/load-assets nil ass init))
