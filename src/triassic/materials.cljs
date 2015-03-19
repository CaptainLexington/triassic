(ns triassic.materials
  (:require [cljs-webgl.shaders :refer [get-shader create-shader create-program get-attrib-location]]
            [cljs-webgl.buffers :refer [create-buffer clear-color-buffer clear-depth-buffer draw!]]
            [cljs-webgl.texture :refer [create-texture]]
            [cljs-webgl.typed-arrays :as ta]
            [cljs-webgl.constants.buffer-object :as buffer-object]
            [triassic.utils :as utils]
            [cljs-webgl.constants.webgl :as webgl]
            [cljs-webgl.constants.texture-parameter-name :as texture-parameter-name]
            [cljs-webgl.constants.texture-filter :as texture-filter]))

(def vertex-shader
  "attribute vec3 aVertexPosition;
   attribute vec4 aVertexColor;

   uniform mat4 uMVMatrix;
   uniform mat4 uPMatrix;

   varying vec4 vColor;

   void main(void) {
       gl_Position = uPMatrix * uMVMatrix * vec4(aVertexPosition, 1.0);
       vColor = aVertexColor;
   }")


(defn shader-program-from-source [gl fragment-src vertex-src]
  (create-program gl
                  (create-shader gl
                                 cljs-webgl.constants.shader/fragment-shader
                                 fragment-src)
                  (create-shader gl
                                 cljs-webgl.constants.shader/vertex-shader
                                 vertex-src)))



(defn solid-color
  ([gl r g b a]
   (let [fragment
         (str "precision mediump float;
              varying vec4 vColor;
              void main(void) {
                 gl_FragColor = vec4("
              r
              ", "
              g
              ","
              b
              ","
              a
              ");
         }")
         shader (shader-program-from-source gl
                                            fragment
                                            vertex-shader)]
     {:shader shader}))
  ([gl [r g b] a]
   (solid-color gl r g b a)))


(defn color-map [gl vertices]
  (let [fragment
        "precision mediump float;

        varying vec4 vColor;

        void main(void) {
            gl_FragColor = vColor;
        }"
        shader (shader-program-from-source gl
                                           fragment
                                           vertex-shader)]
    {:shader    shader
     :attribute [{:buffer   (create-buffer gl
                                           (ta/float32 vertices)
                                           buffer-object/array-buffer
                                           buffer-object/static-draw
                                           4)
                  :location (get-attrib-location gl
                                                 shader
                                                 "aVertexColor")}]}))

(defn image-map [gl vertices img]
  (let [fragment
        "precision mediump float;
         varying vec2 vTextureCoord;
         uniform sampler2D uSampler;
         void main(void) {
           gl_FragColor = texture2D(uSampler, vec2(vTextureCoord.s, vTextureCoord.t));
         }
        "
        vertex
        "attribute vec3 aVertexPosition;
         attribute vec2 aTextureCoord;
         uniform mat4 uMVMatrix;
         uniform mat4 uPMatrix;
         varying vec2 vTextureCoord;
         void main(void) {
            gl_Position = uPMatrix * uMVMatrix * vec4(aVertexPosition, 1.0);
            vTextureCoord = aTextureCoord;
         }
        "
        shader (shader-program-from-source gl
                                           fragment
                                           vertex)
        texture (create-texture
                  gl
                  :image img
                  :pixel-store-modes {webgl/unpack-flip-y-webgl true}
                  :parameters {texture-parameter-name/texture-mag-filter texture-filter/nearest
                               texture-parameter-name/texture-min-filter texture-filter/nearest})]

    {:shader    shader
     :attribute [{:buffer   (create-buffer gl
                                           (ta/float32 vertices)
                                           buffer-object/array-buffer
                                           buffer-object/static-draw
                                           2)
                  :location (get-attrib-location gl
                                                 shader
                                                 "aTextureCoord")}]
     :textures  [{:buffer  "uSampler"
                  :texture texture}]}))
