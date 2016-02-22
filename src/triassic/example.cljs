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

;;Simply opens a WebSocket connection to a locally-running debug REPL, when available
(when-not (repl/alive?)
  (repl/connect "ws://localhost:9001"))

;;Since a loop abstraction, like big-bang!, is beyond the scope of this project,
;;We are handling loop state in this messy way
(def rad1 0)
(def rad2 1)

;;A function that moves a mesh and rotates it around its local axis.
;;Useful because the displacement from the origin is recomputed every frame
;;And our example rorates objects
(defn move-and-rotate [mesh displacement axis angle]
  (-> mesh
      (geo/translate displacement)
      (geo/rotate axis angle)))

;;Our map of asset URIs. It will soon become a map of assets!
(def ass {
          ;;Our pill is an OBJ file, and we use our lw-obj helper to load and parse it
          :pill (assets/lw-obj "/triassic/resources/obj/capsule.obj")

          ;;Our images will be loaded with our img helper
          :pill-texture (assets/img "/triassic/resources/obj/capsule0.jpg")
          :cube-texture (assets/img "/triassic/resources/obj/nehe.gif")})

;;This function inializes our whole scene
;;It will run after every asset loads
(defn init [asses]
  (let [
        ;;The canvas
        ;;(For it is on the canvas that our scene lies)
        canvas (.getElementById js/document "canvas")

        ;;The WebGL context of our canvas
        gl (init-gl canvas)

        ;;The dimensions of the WebGL viewport
        {viewport-width  :width,
         viewport-height :height} (utils/get-viewport gl)

        ;;A perspective projection matrix, with
        ;;- a 45-degree field of view
        ;;- the natural aspect ratio of the viewport
        ;;- a near value 0.1 units
        ;;- a far value of 100.0 units
        ;;(This means the camera only draws vertices between 0.1 and 100.0 units away)
        camera (matrix/camera 45
                              (/ viewport-width viewport-height)
                              0.1
                              100.0)

        ;;An image map given the UV-coordinates of a cube
        ;;And an image from the implementors of a much older OpenGL tutorial
        ;;Of which this is a distant descendent
        cube-material (materials/image-map gl
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


        ;;An image map given the UV-coordinates of simple pill-shaped object I got from an academic website
        ;;And an image from the same source
        pill-material (materials/image-map gl
                                           (:uv-coords (:pill asses))
                                           (:pill-texture asses))

        ;;The cube to which we will apply our cube material, of edge-length 2
        cube (mesh/cube gl 2 cube-material)

        ;;The aforementioned pill object, whose geometry was been parsed when it was loaded
        ;;And converted to vertex data
        pill (mesh/mesh gl (:pill asses) pill-material)

        ;;The displacement vectors for our cube and our pill, which set them off to right and left, respectively
        ;;And sets it back from the camera, which lies at the origin
        cube-displacement (vec3/vector-3 1.5 0 -8)
        pill-displacement (vec3/vector-3 -1.5 0 -8)]


    (animate
      (fn [frame]

        ;;These two "native" cljs calls can probably be dropped into our double-draw! function without
        ;;loss of expressivity
        (apply clear-color-buffer gl (colors/rgba colors/forest-green-traditional))
        (clear-depth-buffer gl 1)


        ;;Here we make our other concession to our messy handling of state:
        ;;MUTATION
        (set! rad2 (dec rad2))
        (set! rad1 (inc rad1))
        ;;Needless to say, this is not the recommended practice
        ;;But triassic is not opinionated about how state should be handled

        ;;We draw our items
        ;;Instead of a proper Scene Graph data structure,
        ;;;we transform them according to our helper function
        ;;;and drop them into a regular vector.
        (render/double-draw! gl
                             [
                              (move-and-rotate pill
                                               pill-displacement
                                               :y
                                               (/ rad2 25))
                              (move-and-rotate cube
                                               cube-displacement
                                               :y
                                               (/ rad1 100))
                              ]
                             camera)))))


;;This function is called in the mark-up of the example page.
;;It loads the assets and passes the init function as the callback
(defn ^:export start []
  (assets/load-assets nil ass init))
